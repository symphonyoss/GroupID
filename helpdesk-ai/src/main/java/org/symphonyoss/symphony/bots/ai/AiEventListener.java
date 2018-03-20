package org.symphonyoss.symphony.bots.ai;

import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;

/**
 * Class to listen to AI events
 * <p>
 * Created by nick.tarsillo on 8/20/17.
 */
public interface AiEventListener {

  /**
   * Interprets an AI message as a command line command.
   * @param command {@link SymphonyAiMessage} containing the command
   * @param sessionContext The current session context
   */
  void onCommand(SymphonyAiMessage command, AiSessionContext sessionContext);

  /**
   * Interprets an AI message as a part of a contextual conversation with the ai.
   * @param message {@link SymphonyAiMessage} containing the conversation message
   * @param aiConversation The {@link AiConversation} object containing the current conversation
   */
  void onConversation(SymphonyAiMessage message, AiConversation aiConversation);
}
