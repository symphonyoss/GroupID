package org.symphonyoss.symphony.bots.ai.conversation;

import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;

/**
 * Null pattern for Ai conversation.
 * <p>
 * Created by rsanchez on 23/03/18.
 */
public class NullConversation extends AiConversation {

  public NullConversation() {
    super(false);
  }

  @Override
  public void onMessage(AiResponder responder, SymphonyAiMessage message) {
    // Do nothing
  }

}
