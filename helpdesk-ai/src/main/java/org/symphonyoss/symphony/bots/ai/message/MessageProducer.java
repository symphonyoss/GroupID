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
import java.util.ArrayList;
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

  public MessageProducer(MessagesClient messagesClient, MembershipClient membershipClient,
                         UsersClient usersClient, SymphonyClient symphonyClient) {
    this.messagesClient = messagesClient;
    this.membershipClient = membershipClient;
    this.usersClient = usersClient;
    this.symphonyClientUtil = new SymphonyClientUtil(symphonyClient);
  }

  public void publishMessage(SymphonyAiMessage symphonyAiMessage, String streamId) throws MessagesException {
    SymMessage symMessage = buildMessage(symphonyAiMessage);
    sendMessage(symMessage, streamId);
  }

  private SymMessage buildMessage(SymphonyAiMessage symphonyAiMessage) {
    StringBuilder message = new StringBuilder("<messageML>");
    StringBuilder content = new StringBuilder("");

    Long userId = symphonyAiMessage.getFromUserId();

    if (userId != null && !isChime(symphonyAiMessage.getMessageData())) {
      Membership membership = membershipClient.getMembership(userId);

      if ((membership != null) && (MembershipClient.MembershipType.CLIENT.getType()
          .equals(membership.getType()))) {
        try {
          SymUser user = usersClient.getUserFromId(userId);

          message.append("<b>");
          message.append(user.getDisplayName());
          message.append("</b>: ");
        } catch (UsersClientException e) {
          LOGGER.info("User " + userId + " not found");
        }
      }
    }

    if (symphonyAiMessage.getMessageData() != null) {
      content.append(this.parseMessage(symphonyAiMessage.getMessageData()));
      message.append(content);
    } else {
      message.append(symphonyAiMessage.getAiMessage());
    }

    message.append("</messageML>");

    SymMessage symMessage = new SymMessage();

    if (userId != null && hasAttachment(symphonyAiMessage)) {
      SymMessage attachmentMessage = new SymMessage();
      attachmentMessage.setId(symphonyAiMessage.getMessageId());
      attachmentMessage.setStreamId(symphonyAiMessage.getStreamId());

      List<SymAttachmentInfo> attachmentInfoList = symphonyAiMessage.getAttachments();

      File file = symphonyClientUtil.getFileAttachment(attachmentInfoList.get(0), attachmentMessage);
      symphonyAiMessage.setAttachment(file);
    }

    symMessage.setMessage(message.toString());
    symMessage.setEntityData(symphonyAiMessage.getEntityData());
    symMessage.setAttachments(symphonyAiMessage.getAttachments());
    symMessage.setAttachment(symphonyAiMessage.getAttachment());

    return symMessage;
  }

  private boolean hasAttachment(SymphonyAiMessage symMessage) {
    List<SymAttachmentInfo> attachments = symMessage.getAttachments();
    return attachments != null && !attachments.isEmpty();
  }

    private boolean isChime(String message) {
        Element elementMessageML;
        Document doc = Jsoup.parse(message);

        elementMessageML = doc.select("messageML").first();
        if (elementMessageML == null) {
            elementMessageML = doc.select("div").first();
        }

        if (elementMessageML != null) {
            elementMessageML = doc.select("audio").first();
        }

        return elementMessageML != null;
    }

  private StringBuilder parseMessage(String message) {
    Element elementMessageML;
    StringBuilder textDoc = new StringBuilder("");

    Document doc = Jsoup.parse(message);

    doc.select("errors").remove();
    elementMessageML = doc.select("messageML").first();
    if (elementMessageML == null) {
      elementMessageML = doc.select("div").first();
    }

    if (elementMessageML != null) {
      textDoc = new StringBuilder();
      Iterator var3 = elementMessageML.childNodes().iterator();

      while(var3.hasNext()) {
        Node node = (Node)var3.next();
        if(node.toString().equalsIgnoreCase("<br>")) {
            textDoc.append("<br/>");
        } else {
            textDoc.append(node.toString());
        }
      }
    }

    return textDoc;
  }

  private void sendMessage(SymMessage symMessage, String streamId) throws MessagesException {
    SymStream stream = new SymStream();
    stream.setStreamId(streamId);

    messagesClient.sendMessage(stream, symMessage);
  }

}
