package org.symphonyoss.symphony.bots.ai.helpdesk.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.utility.client.SymphonyClientUtil;
import org.symphonyoss.symphony.bots.utility.message.SymMessageUtil;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.io.File;

/**
 * The message producer will process messages into the MessageML format and send them via a
 * SymStream.
 * Created by rsanchez on 30/11/17.
 */
public class MessageProducer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageProducer.class);

  private final MembershipClient membershipClient;

  private final SymphonyClient symphonyClient;

  private final SymphonyClientUtil symphonyClientUtil;

  /**
   * Constructor to the MessageProducer class
   * @param membershipClient The MembershipClient
   * @param symphonyClient The SymphonyClient
   * @return A MessageProducer object
   */
  public MessageProducer(MembershipClient membershipClient, SymphonyClient symphonyClient) {
    this.membershipClient = membershipClient;
    this.symphonyClient = symphonyClient;
    this.symphonyClientUtil = new SymphonyClientUtil(symphonyClient);
  }

  /**
   * Build and send a message(s) into a stream (if it is a Client message with multiple attachments,
   * it will send one for each)
   * @param aiMessage AiMessage with the message contents
   * @param streamId String that contains the streamId
   */
  public void publishMessage(AiMessage aiMessage, String streamId) {
    Long userId = aiMessage.getFromUserId();

    if (SymMessageUtil.isChime(aiMessage.toSymMessage())) {
      sendChimeMessage(aiMessage, streamId);
      return;
    }

    if (userId == null) {
      sendAgentMessage(aiMessage, streamId);
      return;
    }

    Membership membership = membershipClient.getMembership(userId);

    if (MembershipClient.MembershipType.CLIENT.getType().equals(membership.getType())) {
      sendClientMessage(aiMessage, streamId);
    } else {
      sendAgentMessage(aiMessage, streamId);
    }
  }

  /**
   * Build Client message and send it. If it has multiple attachments, it will send one for each
   * @param aiMessage AiMessage with the message contents
   * @param streamId String that contains the streamId
   */
  private void sendClientMessage(AiMessage aiMessage, String streamId) {
    if (SymMessageUtil.hasAttachment(aiMessage.toSymMessage())) {
      sendClientMessageWithAttachments(aiMessage, streamId);
    } else {
      SymMessage symMessage = buildClientMessage(aiMessage);
      sendMessage(symMessage, streamId);
    }
  }

  /**
   * Build Client messages and send it, one for each attachment. Only the first message contains the
   * original message text, if any.
   * @param aiMessage AiMessage with the message contents
   * @param streamId String that contains the streamId
   */
  private void sendClientMessageWithAttachments(AiMessage aiMessage,
      String streamId) {
    SymMessage symMessage = buildClientMessage(aiMessage);
    symMessage.setAttachment(
        getClientAttachment(aiMessage, aiMessage.getAttachments().get(0)));
    sendMessage(symMessage, streamId);

    aiMessage.setAiMessage("");
    aiMessage.setMessageData("");

    aiMessage.getAttachments().stream()
        .skip(1)
        .forEach(attachmentInfo -> {
          SymMessage attachmentMessage = buildClientMessage(aiMessage);
          attachmentMessage.setAttachment(getClientAttachment(aiMessage, attachmentInfo));
          sendMessage(attachmentMessage, streamId);
        });
  }

  /**
   * Build and send an Agent message
   * @param aiMessage AiMessage with the message contents
   * @param streamId String that contains the streamId
   */
  private void sendAgentMessage(AiMessage aiMessage, String streamId) {
    SymMessage symMessage = buildAgentMessage(aiMessage);
    sendMessage(symMessage, streamId);
  }

  /**
   * Build and send a SymMessage that contains a chime
   * @param aiMessage AiMessage with the message contents
   * @param streamId String that contains the streamId
   */
  private void sendChimeMessage(AiMessage aiMessage, String streamId) {
    SymMessage symMessage = buildChimeMessage(aiMessage);
    sendMessage(symMessage, streamId);
  }

  /**
   * Build a SymMessage that contains a chime
   * @param aiMessage AiMessage with the message contents
   * @return The built SymMessage
   */
  private SymMessage buildChimeMessage(AiMessage aiMessage) {
    StringBuilder message = new StringBuilder("<messageML>");
    SymMessage symMessage = new SymMessage();
    message.append(SymMessageUtil.parseMessage(aiMessage.toSymMessage()));
    message.append("</messageML>");

    symMessage.setMessage(message.toString());
    symMessage.setEntityData(aiMessage.getEntityData());

    return symMessage;
  }

  /**
   * Build a SymMessage for a Client given a AiMessage
   * @param aiMessage AiMessage with the message contents
   * @return The built SymMessage
   */
  private SymMessage buildClientMessage(AiMessage aiMessage) {
    StringBuilder header = new StringBuilder();
    Long userId = aiMessage.getFromUserId();

    try {
      SymUser user = symphonyClient.getUsersClient().getUserFromId(userId);

      header.append("<b>");
      header.append(user.getDisplayName());
      header.append("</b>: ");
    } catch (UsersClientException e) {
      LOGGER.info("User " + userId + " not found");
    }

    return buildMessage(aiMessage, header.toString());
  }

  /**
   * Build a SymMessage for an Agent given a AiMessage
   * @param aiMessage AiMessage with the message contents
   * @return The built SymMessage
   */
  private SymMessage buildAgentMessage(AiMessage aiMessage) {
    return buildMessage(aiMessage, "");
  }

  /**
   * Build a SymMessage given a AiMessage and a header String
   * @param aiMessage AiMessage with the message contents
   * @param header String with the message header (e.g. Client name)
   * @return The built SymMessage
   */
  private SymMessage buildMessage(AiMessage aiMessage, String header) {
    StringBuilder message = new StringBuilder("<messageML>");
    SymMessage symMessage = new SymMessage();

    message.append(header);
    if (aiMessage.getMessageData() != null) {
      message.append(SymMessageUtil.parseMessage(aiMessage.toSymMessage()));
    } else {
      message.append(aiMessage.getAiMessage());
    }

    message.append("</messageML>");

    symMessage.setMessage(message.toString());
    symMessage.setEntityData(aiMessage.getEntityData());
    symMessage.setAttachments(aiMessage.getAttachments());
    symMessage.setAttachment(aiMessage.getAttachment());

    return symMessage;
  }

  /**
   * Get an Attachment to a AiMessage
   * @param aiMessage The AiMessage containing the attachment
   * @param attachmentInfo The SymAttachmentInfo of the specific attachment to be added
   * @return The attachment
   */
  private File getClientAttachment(AiMessage aiMessage,
      SymAttachmentInfo attachmentInfo) {
    SymMessage attachmentMessage = new SymMessage();
    attachmentMessage.setId(aiMessage.getMessageId());
    attachmentMessage.setStreamId(aiMessage.getStreamId());

    return symphonyClientUtil.getFileAttachment(attachmentInfo, attachmentMessage);
  }

  /**
   * Send a message into a stream
   * @param symMessage The SymMessage to be sent
   * @param streamId A String specifying the stream where the message should be sent to
   * @throws MessagesException Failure to send message
   */
  private void sendMessage(SymMessage symMessage, String streamId) {
    SymStream stream = new SymStream();
    stream.setStreamId(streamId);

    try {
      symphonyClient.getMessagesClient().sendMessage(stream, symMessage);
    } catch (MessagesException e) {
      LOGGER.error("AI could not send message: ", e);
    }
  }

}
