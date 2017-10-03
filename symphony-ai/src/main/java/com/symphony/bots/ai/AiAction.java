package com.symphony.bots.ai;

import com.symphony.bots.ai.model.AiArgumentMap;
import com.symphony.bots.ai.model.AiSessionContext;

/**
 * Created by nick.tarsillo on 8/20/17.
 * Representation of an action to when command event is triggered.
 */
public interface AiAction {
  void doAction(AiSessionContext sessionContext, AiResponder responder, AiArgumentMap aiArgumentMap);
}
