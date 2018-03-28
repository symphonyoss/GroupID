package org.symphonyoss.symphony.bots.ai.helpdesk;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.ai.AiResponseIdentifier;
import org.symphonyoss.symphony.bots.ai.helpdesk.message.MessageProducer;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiResponder;

/**
 * Created by rsanchez on 30/11/17.
 */
public class HelpDeskAiResponder extends SymphonyAiResponder {

  private final MessageProducer messageProducer;

  public HelpDeskAiResponder(SymphonyClient symphonyClient, MessageProducer messageProducer) {
    super(symphonyClient.getMessagesClient());
    this.messageProducer = messageProducer;
  }

  /**
   * Publish a message via a SymStream
   * @param respond responder object to identify to where the message should be sent
   * @param symphonyAiMessage message to be sent
   */
  @Override
  protected void publishMessage(AiResponseIdentifier respond, SymphonyAiMessage symphonyAiMessage) {
    messageProducer.publishMessage(symphonyAiMessage, respond.getResponseIdentifier());
  }

}
