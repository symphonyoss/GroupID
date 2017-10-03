package com.symphony.bots.ai;

import com.symphony.bots.ai.model.AiSessionContext;

/**
 * Created by nick.tarsillo on 8/20/17.
 */
public interface AiPermission {
  /**
   * Check if session has permission to perform action.
   * @param sessionContext the session to check permissions of.
   */
  boolean sessionHasPermission(AiSessionContext sessionContext);
}
