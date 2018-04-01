package org.symphonyoss.symphony.bots.ai.helpdesk;

import org.symphonyoss.symphony.bots.ai.helpdesk.message.MessageProducer;
import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiResponder;

/**
 * Created by rsanchez on 30/11/17.
 */
public class HelpDeskAiResponder extends SymphonyAiResponder {

  private final MessageProducer messageProducer;

  public HelpDeskAiResponder(MessageProducer messageProducer) {
    this.messageProducer = messageProducer;
  }

  @Override
  protected void publishMessage(String streamId, AiMessage aiMessage) {
    messageProducer.publishMessage(aiMessage, streamId);
  }

}
