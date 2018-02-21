package org.symphonyoss.symphony.bots.ai;

import org.symphonyoss.symphony.bots.ai.model.AiArgumentMap;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;

/**
 * Representation of an action to when command event is triggered.
 * <p>
 * Created by nick.tarsillo on 8/20/17.
 */
public interface AiAction {

  /**
   * Performs an action in the given session context
   * @param sessionContext current session context
   * @param responder object used to perform message answering
   * @param aiArgumentMap arguments passed to execute this action
   */
  void doAction(AiSessionContext sessionContext, AiResponder responder,
      AiArgumentMap aiArgumentMap);
}
