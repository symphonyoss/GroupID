package org.symphonyoss.symphony.bots.ai.impl;

import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.ai.model.SymphonyAiSessionKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all sessions in the Ai.
 * <p>
 * Created by nick.tarsillo on 8/20/17.
 */
public class SymphonyAiSessionContextManager {

  private Map<SymphonyAiSessionKey, AiSessionContext> sessions = new ConcurrentHashMap<>();

  /**
   * Adds a session context in the cache
   * @param aiSessionKey session context key
   * @param aiSessionContext session context itself
   */
  public void putSessionContext(SymphonyAiSessionKey aiSessionKey, AiSessionContext aiSessionContext) {
    sessions.put(aiSessionKey, aiSessionContext);
  }

  public AiSessionContext getSessionContext(SymphonyAiSessionKey aiSessionKey) {
    return sessions.get(aiSessionKey);
  }

}