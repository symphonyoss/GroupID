package org.symphonyoss.symphony.bots.ai.message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.utility.client.SymphonyClientUtil;
import org.symphonyoss.symphony.bots.utility.message.SymMessageUtil;
import org.symphonyoss.symphony.clients.MessagesClient;
import org.symphonyoss.symphony.clients.UsersClient;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.io.File;
import java.util.List;

/**
 * Created by rsanchez on 30/11/17.
 */
public class MessageProducer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageProducer.class);

  private final MessagesClient messagesClient;

  private final MembershipClient membershipClient;

  private final UsersClient usersClient;

  private final SymphonyClientUtil symphonyClientUtil;

  /**
   * Constructor to the MessageProducer class
   * @param membershipClient The MembershipClient
   * @param symphonyClient The SymphonyClient
   * @return A MessageProducer object
   */
  public MessageProducer(MembershipClient membershipClient, SymphonyClient symphonyClient) {
    this.messagesClient = symphonyClient.getMessagesClient();
    this.membershipClient = membershipClient;
    this.usersClient = symphonyClient.getUsersClient();
    this.symphonyClientUtil = new SymphonyClientUtil(symphonyClient);
  }

  /**
   * Build and send a message(s) into a stream (if it is a Client message with multiple attachments,
   * it will send one for each)
   * @param symphonyAiMessage SymphonyAiMessage with the message contents
   * @param streamId String that contains the streamId
   */
  public void publishMessage(SymphonyAiMessage symphonyAiMessage, String streamId) {
    Long userId = symphonyAiMessage.getFromUserId();

    if (SymMessageUtil.isChime(symphonyAiMessage.toSymMessage())) {
      sendChimeMessage(symphonyAiMessage, streamId);
      return;
    }

    if (userId == null) {
      sendAgentMessage(symphonyAiMessage, streamId);
      return;
    }

    Membership membership = membershipClient.getMembership(userId);

    if (MembershipClient.MembershipType.CLIENT.getType().equals(membership.getType())) {
      sendClientMessage(symphonyAiMessage, streamId);
    } else {
      sendAgentMessage(symphonyAiMessage, streamId);
    }
  }

  /**
   * Build Client message and send it. If it has multiple attachments, it will send one for each
   * @param symphonyAiMessage SymphonyAiMessage with the message contents
   * @param streamId String that contains the streamId
   */
  private void sendClientMessage(SymphonyAiMessage symphonyAiMessage, String streamId) {
    if (SymMessageUtil.hasAttachment(symphonyAiMessage.toSymMessage())) {
      sendClientMessageWithAttachments(symphonyAiMessage, streamId);
    } else {
      SymMessage symMessage = buildClientMessage(symphonyAiMessage);
      sendMessage(symMessage, streamId);
    }
  }

  /**
   * Build Client messages and send it, one for each attachment. Only the first message contains the
   * original message text, if any.
   * @param symphonyAiMessage SymphonyAiMessage with the message contents
   * @param streamId String that contains the streamId
   */
  private void sendClientMessageWithAttachments(SymphonyAiMessage symphonyAiMessage,
      String streamId) {
    SymMessage symMessage = buildClientMessage(symphonyAiMessage);
    symMessage.setAttachment(
        getClientAttachment(symphonyAiMessage, symphonyAiMessage.getAttachments().get(0)));
    sendMessage(symMessage, streamId);

    symphonyAiMessage.setAiMessage("");
    symphonyAiMessage.setMessageData("");

    symphonyAiMessage.getAttachments().stream()
        .skip(1)
        .forEach(attachmentInfo -> {
          SymMessage attachmentMessage = buildClientMessage(symphonyAiMessage);
          attachmentMessage.setAttachment(getClientAttachment(symphonyAiMessage, attachmentInfo));
          sendMessage(attachmentMessage, streamId);
        });
  }

  /**
   * Build and send an Agent message
   * @param symphonyAiMessage SymphonyAiMessage with the message contents
   * @param streamId String that contains the streamId
   */
  private void sendAgentMessage(SymphonyAiMessage symphonyAiMessage, String streamId) {
    SymMessage symMessage = buildAgentMessage(symphonyAiMessage);
    sendMessage(symMessage, streamId);
  }

  /**
   * Build and send a SymMessage that contains a chime
   * @param symphonyAiMessage SymphonyAiMessage with the message contents
   * @param streamId String that contains the streamId
   */
  private void sendChimeMessage(SymphonyAiMessage symphonyAiMessage, String streamId) {
    SymMessage symMessage = buildChimeMessage(symphonyAiMessage);
    sendMessage(symMessage, streamId);
  }

  /**
   * Build a SymMessage that contains a chime
   * @param symphonyAiMessage SymphonyAiMessage with the message contents
   * @return The built SymMessage
   */
  private SymMessage buildChimeMessage(SymphonyAiMessage symphonyAiMessage) {
    StringBuilder message = new StringBuilder("<messageML>");
    SymMessage symMessage = new SymMessage();
    message.append(parseMessage(symphonyAiMessage));
    message.append("</messageML>");

    symMessage.setMessage(message.toString());
    symMessage.setEntityData(symphonyAiMessage.getEntityData());

    return symMessage;
  }

  /**
   * Build a SymMessage for a Client given a SymphonyAiMessage
   * @param symphonyAiMessage SymphonyAiMessage with the message contents
   * @return The built SymMessage
   */
  private SymMessage buildClientMessage(SymphonyAiMessage symphonyAiMessage) {
    StringBuilder header = new StringBuilder();
    Long userId = symphonyAiMessage.getFromUserId();

    try {
      SymUser user = usersClient.getUserFromId(userId);

      header.append("<b>");
      header.append(user.getDisplayName());
      header.append("</b>: ");
    } catch (UsersClientException e) {
      LOGGER.info("User " + userId + " not found");
    }

    return buildMessage(symphonyAiMessage, header.toString());
  }

  /**
   * Build a SymMessage for an Agent given a SymphonyAiMessage
   * @param symphonyAiMessage SymphonyAiMessage with the message contents
   * @return The built SymMessage
   */
  private SymMessage buildAgentMessage(SymphonyAiMessage symphonyAiMessage) {
    return buildMessage(symphonyAiMessage, "");
  }

  /**
   * Build a SymMessage given a SymphonyAiMessage and a header String
   * @param symphonyAiMessage SymphonyAiMessage with the message contents
   * @param header String with the message header (e.g. Client name)
   * @return The built SymMessage
   */
  private SymMessage buildMessage(SymphonyAiMessage symphonyAiMessage, String header) {
    StringBuilder message = new StringBuilder("<messageML>");
    SymMessage symMessage = new SymMessage();

    message.append(header);
    if (symphonyAiMessage.getMessageData() != null) {
      message.append(parseMessage(symphonyAiMessage));
    } else {
      message.append(symphonyAiMessage.getAiMessage());
    }

    message.append("</messageML>");

    symMessage.setMessage(message.toString());
    symMessage.setEntityData(symphonyAiMessage.getEntityData());
    symMessage.setAttachments(symphonyAiMessage.getAttachments());
    symMessage.setAttachment(symphonyAiMessage.getAttachment());

    return symMessage;
  }

  /**
   * Get an Attachment to a SymphonyAiMessage
   * @param symphonyAiMessage The SymphonyAiMessage containing the attachment
   * @param attachmentInfo The SymAttachmentInfo of the specific attachment to be added
   * @return The attachment
   */
  private File getClientAttachment(SymphonyAiMessage symphonyAiMessage,
      SymAttachmentInfo attachmentInfo) {
    SymMessage attachmentMessage = new SymMessage();
    attachmentMessage.setId(symphonyAiMessage.getMessageId());
    attachmentMessage.setStreamId(symphonyAiMessage.getStreamId());

    return symphonyClientUtil.getFileAttachment(attachmentInfo, attachmentMessage);
  }
  
  /**
   * Parse a message into valid MessageML format
   * @param message SymphonyAiMessage with the message contents
   * @return StringBuilder with the output message in MessageML format
   */
  private String parseMessage(SymphonyAiMessage message) {
    Element elementMessageML;
    StringBuilder textDoc = new StringBuilder("");

    Document doc = Jsoup.parse(message.getMessageData());

    doc.select("errors").remove();
    elementMessageML = doc.select("messageML").first();
    if (elementMessageML == null) {
      elementMessageML = doc.select("div").first();
    }

    if (elementMessageML != null) {
      elementMessageML.childNodes().forEach(node -> {
        if (node.toString().equalsIgnoreCase("<br>")) {
          textDoc.append("<br/>");
        } else {
          textDoc.append(node.toString());
        }
      });
    }

    return textDoc.toString();
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
      messagesClient.sendMessage(stream, symMessage);
    } catch (MessagesException e) {
      LOGGER.error("AI could not send message: ", e);
    }
  }

}
