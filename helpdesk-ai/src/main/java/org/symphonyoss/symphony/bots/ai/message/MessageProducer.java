package org.symphonyoss.symphony.bots.ai.message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.utility.client.SymphonyClientUtil;
import org.symphonyoss.symphony.clients.MessagesClient;
import org.symphonyoss.symphony.clients.UsersClient;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.io.File;
import java.lang.reflect.Member;
import java.util.Iterator;
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
   * Build and send a message into a stream, if it has multiple attachments, it will send one for
   * each
   * @param symphonyAiMessage SymphonyAiMessage with the message contents
   * @param streamId String that contains the streamId
   * @throws MessagesException Failure to send the message
   */
  public void publishMessage(SymphonyAiMessage symphonyAiMessage, String streamId)
      throws MessagesException {
    Long userId = symphonyAiMessage.getFromUserId();
    Membership membership;
    boolean isClient = false;

    if (userId != null) {
      membership = membershipClient.getMembership(userId);
      if ((membership != null) && (MembershipClient.MembershipType.CLIENT.getType()
          .equals(membership.getType()))) {
        isClient = true;
      }
    }

    if (hasAttachment(symphonyAiMessage) && isClient) {
      for (int i = 1; i <= symphonyAiMessage.getAttachments().size(); i++) {
        SymMessage symMessage = buildMessage(symphonyAiMessage, i, isClient);
        sendMessage(symMessage, streamId);
      }
    } else {
      SymMessage symMessage = buildMessage(symphonyAiMessage, 0, isClient);
      sendMessage(symMessage, streamId);
    }
  }

  /**
   * Build a SymMessage given a SymphonyAiMessage
   * @param symphonyAiMessage SymphonyAiMessage with the message contents
   * @return The built SymMessage
   */
  private SymMessage buildMessage(SymphonyAiMessage symphonyAiMessage, int attachmentId,
      boolean isClient) {
    StringBuilder message = new StringBuilder("<messageML>");

    Long userId = symphonyAiMessage.getFromUserId();

    if (isClient && !isChime(symphonyAiMessage)) {
      try {
        SymUser user = usersClient.getUserFromId(userId);

        message.append("<b>");
        message.append(user.getDisplayName());
        message.append("</b>: ");
      } catch (UsersClientException e) {
        LOGGER.info("User " + userId + " not found");
      }
    }

    if (attachmentId < 2) {
      if (symphonyAiMessage.getMessageData() != null) {
        message.append(parseMessage(symphonyAiMessage));
      } else {
        message.append(symphonyAiMessage.getAiMessage());
      }
    }

    message.append("</messageML>");

    SymMessage symMessage = new SymMessage();

    if (isClient && attachmentId > 0) {
      setClientAttachment(symphonyAiMessage, attachmentId);
    }

    symMessage.setMessage(message.toString());
    symMessage.setEntityData(symphonyAiMessage.getEntityData());
    symMessage.setAttachments(symphonyAiMessage.getAttachments());
    symMessage.setAttachment(symphonyAiMessage.getAttachment());

    return symMessage;
  }

  /**
   * Add an Attachment to a SymphonyAiMessage
   * @param symphonyAiMessage The SymphonyAiMessage where to add the attachment
   * @param attachmentId The id of the attachment to be added
   * @return The built SymMessage
   */
  private void setClientAttachment(SymphonyAiMessage symphonyAiMessage, int attachmentId) {
    SymMessage attachmentMessage = new SymMessage();
    attachmentMessage.setId(symphonyAiMessage.getMessageId());
    attachmentMessage.setStreamId(symphonyAiMessage.getStreamId());

    List<SymAttachmentInfo> attachmentInfoList = symphonyAiMessage.getAttachments();

    File file = symphonyClientUtil.getFileAttachment(attachmentInfoList.get(attachmentId - 1),
        attachmentMessage);
    symphonyAiMessage.setAttachment(file);
  }

  /**
   * Check if a SymphonyAiMessage contains an attachment
   * @param message SymphonyAiMessage with the message contents
   * @return True if message contains an attachment, false otherwise
   */
  private boolean hasAttachment(SymphonyAiMessage message) {
    List<SymAttachmentInfo> attachments = message.getAttachments();
    return attachments != null && !attachments.isEmpty();
  }

  /**
   * Check if a SymphonyAiMessage is a chime
   * @param message SymphonyAiMessage with the message contents
   * @return True if message is a chime, false otherwise
   */
  private boolean isChime(SymphonyAiMessage message) {
    Element elementMessageML;
    Document doc = Jsoup.parse(message.getMessageData());

    elementMessageML = doc.select("messageML").first();
    if (elementMessageML == null) {
      elementMessageML = doc.select("div").first();
    }

    if (elementMessageML != null) {
      elementMessageML = doc.select("audio").first();
    }

    return elementMessageML != null;
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
  private void sendMessage(SymMessage symMessage, String streamId) throws MessagesException {
    SymStream stream = new SymStream();
    stream.setStreamId(streamId);

    messagesClient.sendMessage(stream, symMessage);
  }

}
