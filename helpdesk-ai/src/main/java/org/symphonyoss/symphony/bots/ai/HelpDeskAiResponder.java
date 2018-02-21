package org.symphonyoss.symphony.bots.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiResponder;
import org.symphonyoss.symphony.bots.ai.message.MessageProducer;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;

/**
 * Created by rsanchez on 30/11/17.
 */
public class HelpDeskAiResponder extends SymphonyAiResponder {

  private static final Logger LOGGER = LoggerFactory.getLogger(HelpDeskAiResponder.class);

  private final MessageProducer messageProducer;

  public HelpDeskAiResponder(SymphonyClient symphonyClient, MembershipClient membershipClient) {
    super(symphonyClient.getMessagesClient());
    this.messageProducer = new MessageProducer(membershipClient, symphonyClient);
  }

  @Override
  protected void publishMessage(AiResponseIdentifier respond, SymphonyAiMessage symphonyAiMessage) {
    try {
      messageProducer.publishMessage(symphonyAiMessage, respond.getResponseIdentifier());
    } catch (MessagesException e) {
      LOGGER.error("Ai could not send message: ", e);
    }
  }

}
