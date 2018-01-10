package org.symphonyoss.symphony.bots.helpdesk.makerchecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.check.Checker;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client.MakercheckerClient;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.BadRequestException;

/**
 * Created by nick.tarsillo on 9/26/17.
 * Used in conjunction with the Symphony AI.
 * Validates a messages, and requests validation from another user when checks fail.
 */
public class MakerCheckerService {

  private static final Logger LOG = LoggerFactory.getLogger(MakerCheckerService.class);
  private static final String MESSAGE_NOT_FOUND = "Message with id %s could not be found.";
  private static final String MESSAGE_STREAM_NOT_FOUND = "The stream %s could not be found.";

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
  public Set<SymMessage> getApprovedMakercheckerMessage(MakerCheckerMessage makerCheckerMessage) {
    try {
      SymStream stream = new SymStream();
      stream.setStreamId(makerCheckerMessage.getStreamId());

      SymMessage symMessage = getApprovedMessage(makerCheckerMessage, stream);
      Set<SymMessage> symApprovedMessages = new HashSet<>();

      for(Checker checker: checkerSet) {
        if(checker.isCheckerType(makerCheckerMessage)) {
          symApprovedMessages.addAll(checker.makeApprovedMessages(makerCheckerMessage, symMessage));
        }
      }

      return symApprovedMessages;
    } catch (MessagesException e) {
      LOG.warn("Error accepting maker checker message: ", e);
      throw new BadRequestException(String.format(MESSAGE_STREAM_NOT_FOUND, makerCheckerMessage.getStreamId()));
    }
  }

  private SymMessage getApprovedMessage(MakerCheckerMessage makerCheckerMessage, SymStream stream) throws MessagesException {
    List<SymMessage> symMessageList = symphonyClient.getMessagesClient()
        .getMessagesFromStream(stream, makerCheckerMessage.getTimeStamp() - 1, 0, 10);

    Optional<SymMessage> symMessage = symMessageList.stream()
        .filter(message -> message.getId().equals(makerCheckerMessage.getMessageId()))
        .findFirst();

    if (symMessage.isPresent()) {
      return symMessage.get();
    }

    throw new BadRequestException(String.format(MESSAGE_NOT_FOUND, makerCheckerMessage.getMessageId()));
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

  public void sendMakerCheckerMesssage(SymMessage message, String messageId, Set<String> proxyToStreamId) {
    try {
      createMakerchecker(message, messageId, proxyToStreamId);
      symphonyClient.getMessagesClient().sendMessage(message.getStream(), message);
    } catch (MessagesException e) {
      LOG.error("Error sending an attachment to the room", e);
    }
  }

  private void createMakerchecker(SymMessage symMessage, String messageId, Set<String> proxyToStreamId) {
    String makerCheckerId = symMessage.getId();
    Long makerId = symMessage.getFromUserId();
    String attachmentId = symMessage.getAttachments().get(0).getId();
    Long timeStamp = Long.valueOf(symMessage.getTimestamp());

    List<String> proxyToStreamIdsList = new ArrayList<String>(proxyToStreamId);

    this.makercheckerClient.createMakerchecker(makerCheckerId, makerId,
        symMessage.getStreamId(), attachmentId, messageId, timeStamp, proxyToStreamIdsList);
  }

  public void afterSendApprovedMessage(SymMessage symMessage) {
    for (Checker checker : checkerSet) {
      checker.afterSendApprovedMessage(symMessage);
    }
  }

}
