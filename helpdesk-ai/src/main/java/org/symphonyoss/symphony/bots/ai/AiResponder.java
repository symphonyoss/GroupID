package org.symphonyoss.symphony.bots.ai;

import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;
import org.symphonyoss.symphony.bots.ai.model.AiResponse;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;

/**
 * The {@link AiResponder} is used as message responder for the given session context
 * <p>
 * Created by nick.tarsillo on 8/20/17.
 */
public interface AiResponder {

  /**
   * Respond based on a list of responses.
   * @param responses List of responses
   */
  void respond(AiResponse... responses);

  /**
   * Send response menu.
   * @param sessionKey the session key.
   * @param commandMenu Set of available commands.
   */
  void respondWithUseMenu(AiSessionKey sessionKey, AiCommandMenu commandMenu,
      AiMessage message);

}
