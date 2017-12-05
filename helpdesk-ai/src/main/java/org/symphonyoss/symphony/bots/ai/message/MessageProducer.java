package org.symphonyoss.symphony.bots.ai.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.clients.MessagesClient;
import org.symphonyoss.symphony.clients.UsersClient;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;

/**
 * Created by rsanchez on 30/11/17.
 */
public class MessageProducer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageProducer.class);

  private final MessagesClient messagesClient;

  private final MembershipClient membershipClient;

  private final UsersClient usersClient;

  public MessageProducer(MessagesClient messagesClient, MembershipClient membershipClient,
      UsersClient usersClient) {
    this.messagesClient = messagesClient;
    this.membershipClient = membershipClient;
    this.usersClient = usersClient;
  }

  public void publishMessage(SymphonyAiMessage symphonyAiMessage, String streamId) throws MessagesException {
    SymMessage symMessage = buildMessage(symphonyAiMessage);
    sendMessage(symMessage, streamId);
  }

  private SymMessage buildMessage(SymphonyAiMessage symphonyAiMessage) {
    StringBuilder message = new StringBuilder("<messageML>");

    String fromUserId = symphonyAiMessage.getFromUserId();

    if (symphonyAiMessage.getFromUserId() != null) {
      Long userId = Long.valueOf(fromUserId);
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

    message.append(symphonyAiMessage.getAiMessage());
    message.append("</messageML>");

    SymMessage symMessage = new SymMessage();
    symMessage.setMessage(message.toString());
    symMessage.setEntityData(symphonyAiMessage.getEntityData());
    symMessage.setAttachments(symphonyAiMessage.getAttachments());

    return symMessage;
  }

  private void sendMessage(SymMessage symMessage, String streamId) throws MessagesException {
    SymStream stream = new SymStream();
    stream.setStreamId(streamId);

    messagesClient.sendMessage(stream, symMessage);
  }

}
