package org.symphonyoss.symphony.bots.ai;

import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;

/**
 * Class to listen to AI events
 * <p>
 * Created by nick.tarsillo on 8/20/17.
 */
public interface AiEventListener {

  /**
   * Interprets an AI message as a part of a contextual conversation with the AI.
   *
   * @param sessionKey session key
   * @param message {@link AiMessage} containing the conversation message
   * @param aiConversation The {@link AiConversation} object containing the current conversation
   */
  void onMessage(AiSessionKey sessionKey, AiMessage message, AiConversation aiConversation);

}
