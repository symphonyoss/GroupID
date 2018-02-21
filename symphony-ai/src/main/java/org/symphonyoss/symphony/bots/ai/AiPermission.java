package org.symphonyoss.symphony.bots.ai;

import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;

/**
 * Class created to check permissions in a given session context
 * <p>
 * Created by nick.tarsillo on 8/20/17.
 */
public interface AiPermission {
  /**
   * Check if session has permission to perform action.
   * @param sessionContext the session to check permissions of.
   */
  boolean sessionHasPermission(AiSessionContext sessionContext);
}
