package org.symphonyoss.symphony.bots.utility.client;

import org.apache.commons.io.FileUtils;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.AttachmentsException;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.symphony.authenticator.model.Token;
import org.symphonyoss.symphony.clients.MessagesClient;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;

/**
 * Helper class for retrieving Symphony data.
 * <p>
 * Created by nick.tarsillo on 12/6/17.
 */
public class SymphonyClientUtil {

  private static final int DEFAULT_MAX_MESSAGES = 10;
  private static final String MESSAGE_COULD_NOT_CREATE_FILE = "Couldn't create a file.";
  private static final String MESSAGE_ATTACHMENT_NOT_FOUND = "Attachment not found.";
  private static final String MESSAGE_FAILED_TO_CREATE_FILE = "Failed to create File";

  private SymphonyClient symphonyClient;

  public SymphonyClientUtil(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
  }

  /**
   * Get an attachment for a message
   * @param symAttachmentInfo SymAttachmentInfo describing the attachment
   * @param symMessage Message that contains the attachment
   * @return The attachment File
   * @throws InternalServerErrorException Failure to create file remotely
   * @throws BadRequestException Failure to retrieve attachment from message
   */
  public File getFileAttachment(SymAttachmentInfo symAttachmentInfo, SymMessage symMessage) {
    String tmpDir = System.getProperty("java.io.tmpdir");
    File directory = new File(tmpDir + File.separator + symAttachmentInfo.getId());
    if (!directory.exists()) {
      directory.mkdir();
    }

    File file = new File(directory + File.separator + symAttachmentInfo.getName());
    try {
      file.createNewFile();
    } catch (IOException e) {
      throw new InternalServerErrorException(MESSAGE_COULD_NOT_CREATE_FILE);
    }

    byte[] aByte;
    try {
      aByte =
          symphonyClient.getAttachmentsClient().getAttachmentData(symAttachmentInfo, symMessage);
    } catch (AttachmentsException e) {
      throw new BadRequestException(MESSAGE_ATTACHMENT_NOT_FOUND);
    }

    InputStream inputStream = new ByteArrayInputStream(aByte);
    try {
      FileUtils.copyInputStreamToFile(inputStream, file);
    } catch (IOException e) {
      throw new InternalServerErrorException(MESSAGE_FAILED_TO_CREATE_FILE);
    }

    return file;
  }

  /**
   * Get a list of messages from a stream
   * @param stream Stream to retrieve messages from
   * @param since Date (long) from point in time
   * @param maxMessages Maximum number of messages to retrieve from the specified time (since)
   * @return List of messages matching the given parameters
   * @throws MessagesException Failure to retrieve messages
   */
  public List<SymMessage> getSymMessages(SymStream stream, Long since, int maxMessages)
      throws MessagesException {
    MessagesClient messagesClient = symphonyClient.getMessagesClient();
    return messagesClient.getMessagesFromStream(stream, since, 0, maxMessages);
  }

  /**
   * Get the message from the given stream according to the conversation ID
   * @param streamId Stream to retrieve messages from
   * @param since Date (long) from point in time
   * @param id Conversation ID
   * @return Message according to the conversation ID
   * @throws MessagesException Failure to retrieve messages
   */
  public Optional<SymMessage> getSymMessageByStreamAndId(String streamId, Long since, String id)
      throws MessagesException {
    SymStream stream = new SymStream();
    stream.setStreamId(streamId);
    List<SymMessage> symMessageList = getSymMessages(stream, since, DEFAULT_MAX_MESSAGES);

    Optional<SymMessage> symMessage = symMessageList.stream()
        .filter(message -> message.getId().equals(id))
        .findFirst();

    return symMessage;
  }

  /**
   * Retrieves JWT of bot user.
   *
   * @return Json Web Token
   */
  public String getAuthToken() {
    Token sessionToken = symphonyClient.getSymAuth().getSessionToken();
    return sessionToken.getToken();
  }
}
