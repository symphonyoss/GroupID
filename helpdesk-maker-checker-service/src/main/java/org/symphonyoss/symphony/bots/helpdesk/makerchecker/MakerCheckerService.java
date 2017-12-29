package org.symphonyoss.symphony.bots.helpdesk.makerchecker;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.AttachmentsException;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.check.Checker;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client.MakercheckerClient;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.BadRequestException;

/**
 * Created by nick.tarsillo on 9/26/17.
 * Used in conjunction with the Symphony AI.
 * Validates a messages, and requests validation from another user when checks fail.
 */
public class MakerCheckerService {

  private static final Logger LOG = LoggerFactory.getLogger(MakerCheckerService.class);
  private static final String MESSAGE_COULD_NOT_CREATE_TEMP_FILE = "Couldn't create a temp file.";
  private static final String MESSAGE_ATTACHMENT_NOT_FOUND = "Attachment not found.";
  private static final String MESSAGE_NOT_FOUND = "Message with id %s could not be found.";
  private static final String MESSAGE_STREAM_NOT_FOUND = "The stream %s could not be found.";
  private static final String MESSAGE_FAILED_TO_CREATE_FILE = "Failed to create File";

  private Set<Checker> checkerSet = new HashSet<>();

  private final MakercheckerClient makercheckerClient;

  private final SymphonyClient symphonyClient;

  public MakerCheckerService(MakercheckerClient client, SymphonyClient symphonyClient) {
    this.makercheckerClient = client;
    this.symphonyClient = symphonyClient;
  }

  /**
   * Add a check to the maker checker service.
   * @param checker the check to add.
   */
  public void addCheck(Checker checker) {
    checkerSet.add(checker);
  }

  /**
   * Validates that all checks pass.
   * @param symMessage the message to validate.
   * @return if all the checks passed.
   */
  public boolean allChecksPass(SymMessage symMessage) {
    for(Checker checker: checkerSet) {
      Set<Object> flagged = checker.check(symMessage);
      if (flagged != null && !flagged.isEmpty()) {
        return false;
      }
    }

    return true;
  }

  /**
   * Approve a maker checker message.
   * Find the message.
   * Send the message to client stream.
   * @param makerCheckerMessage the maker checker message
   */
  public SymMessage getApprovedMessage(MakerCheckerMessage makerCheckerMessage) {
    try {
      SymStream stream = new SymStream();
      stream.setStreamId(makerCheckerMessage.getStreamId());

      List<SymMessage> symMessageList = symphonyClient.getMessagesClient()
          .getMessagesFromStream(stream, makerCheckerMessage.getTimeStamp() - 1, 0, 10);

      SymMessage symMessageApproved = null;
      for(SymMessage symMessage : symMessageList) {
        if(symMessage.getId().equals(makerCheckerMessage.getMessageId())) {
          symMessageApproved = symMessage;
        }
      }

      if(symMessageApproved == null) {
        throw new BadRequestException(String.format(MESSAGE_NOT_FOUND, makerCheckerMessage.getMessageId()));
      }

      SymMessage approvedMessage = new SymMessage();
      for(Checker checker: checkerSet) {
        if(checker.isCheckerType(makerCheckerMessage)) {
          for(String streamId: makerCheckerMessage.getProxyToStreamIds()) {
            stream = new SymStream();
            stream.setStreamId(streamId);

            approvedMessage.setStreamId(streamId);
            approvedMessage.setStream(stream);
            approvedMessage.setMessage(symMessageApproved.getMessage());
            approvedMessage.setEntityData(symMessageApproved.getEntityData());
            approvedMessage.setTimestamp(symMessageApproved.getTimestamp());
            approvedMessage.setFromUserId(symMessageApproved.getFromUserId());

            List<SymAttachmentInfo> attachmentInfoList = new ArrayList<>();
            attachmentInfoList.add(checker.getApprovedAttachment(makerCheckerMessage, symMessageApproved));
            approvedMessage.setAttachments(attachmentInfoList);

            File file = getFileAttachment(attachmentInfoList.get(0), symMessageApproved);
            approvedMessage.setAttachment(file);
          }
        }
      }

      return approvedMessage;
    } catch (MessagesException e) {
      LOG.warn("Error accepting maker checker message: ", e);
      throw new BadRequestException(String.format(MESSAGE_STREAM_NOT_FOUND, makerCheckerMessage.getStreamId()));
    }
  }

  private File getFileAttachment(SymAttachmentInfo symAttachmentInfo, SymMessage symMessage) {
    File tempFile;
    try {
      String prefix = symAttachmentInfo.getName().split("\\.")[0];
      String suffix = "." + symAttachmentInfo.getName().split("\\.")[1];
      tempFile = File.createTempFile(prefix, suffix);
    } catch (IOException e) {
      throw new BadRequestException(MESSAGE_COULD_NOT_CREATE_TEMP_FILE);
    }

    byte[] aByte;
    try {
      aByte = symphonyClient.getAttachmentsClient().getAttachmentData(symAttachmentInfo, symMessage);
    } catch (AttachmentsException e) {
      throw new BadRequestException(MESSAGE_ATTACHMENT_NOT_FOUND);
    }

    tempFile.deleteOnExit();
    InputStream inputStream = new ByteArrayInputStream(aByte);
    try {
      FileUtils.copyInputStreamToFile(inputStream, tempFile);
    } catch (IOException e) {
      throw new BadRequestException(MESSAGE_FAILED_TO_CREATE_FILE);
    }

    return tempFile;
  }

  /**
   * If all checks did not pass, get a message to send back to user who sent the message.
   * This message will request validation from another user.
   * @param symMessage the message to base the maker checker message on.
   * @return the maker checker message.
   */
  public Set<SymMessage> getMakerCheckerMessages(SymMessage symMessage, Set<String> proxyToIds) {
    Set<SymMessage> makerCheckerMessages = new HashSet<>();

    for(Checker checker: checkerSet) {
      Set<Object> checkFlagged = checker.check(symMessage);

      if (checkFlagged != null && !checkFlagged.isEmpty()) {
        makerCheckerMessages.addAll(checker.buildSymCheckerMessages(symMessage, proxyToIds));
      }
    }

    return makerCheckerMessages;
  }

  public void sendMakerCheckerMesssage(SymMessage message) {
    try {
      createMakerchecker(message);
      symphonyClient.getMessagesClient().sendMessage(message.getStream(), message);
    } catch (MessagesException e) {
      LOG.error("Error sending an attachment to the room", e);
    }
  }

  private void createMakerchecker(SymMessage symMessage) {
    String makerCheckerId = symMessage.getId();
    String attachmentId = symMessage.getAttachments().get(0).getId();

    this.makercheckerClient.createMakerchecker(makerCheckerId, symMessage.getFromUserId(),
        symMessage.getStreamId(), attachmentId);
  }

}
