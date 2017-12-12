package org.symphonyoss.symphony.bots.ai;

import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiResponse;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;

/**
 * Created by nick.tarsillo on 8/20/17.
 */
public interface AiResponder {
  /**
   * Respond based on a given session context.
   * @param sessionContext the session context to base the response on.
   */
  void respond(AiSessionContext sessionContext);

  /**
   * Add a response based on a given session context.
   * @param sessionContext the session context to add the response to.
   * @param aiResponse the response to add.
   */
  void addResponse(AiSessionContext sessionContext, AiResponse aiResponse);

  /**
   * Send response menu.
   * @param sessionContext the session context to base the response menu on.
   */
  void respondWithUseMenu(AiSessionContext sessionContext, AiMessage message);

  /**
   * Send suggestion message based on menu.
   * @param sessionContext the session context to add the response to.
   * @param command the incorrect menu.
   */
  void respondWithSuggestion(AiSessionContext sessionContext, AiCommandInterpreter aiCommandInterpreter, AiMessage command);
}
