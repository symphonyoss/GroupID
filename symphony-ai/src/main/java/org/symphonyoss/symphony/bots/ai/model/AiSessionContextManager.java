package org.symphonyoss.symphony.bots.ai.model;

import org.symphonyoss.symphony.bots.ai.common.AiConstants;
import org.symphonyoss.symphony.bots.ai.util.file.ExpiringFileLoaderCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nick.tarsillo on 8/20/17.
 * Manages all sessions with the Ai.
 */
public abstract class AiSessionContextManager {
  private static final Logger LOG = LoggerFactory.getLogger(AiSessionContextManager.class);

  protected ExpiringFileLoaderCache<AiSessionKey, AiSessionContext> sessionCache;

  public AiSessionContextManager(String aiSessionContextDir) {
    sessionCache = new ExpiringFileLoaderCache<>(
        aiSessionContextDir,
        (key) -> key.getSessionKey(),
        AiConstants.EXPIRE_TIME,
        AiConstants.EXPIRE_TIME_UNIT,
        AiSessionContext.class);
  }

  public AiSessionContext getSessionContext(AiSessionKey aiSessionKey) {
    AiSessionContext aiSessionContext = null;
    try {
      aiSessionContext = sessionCache.get(aiSessionKey);
    } catch (Exception e) {
      LOG.warn("Could not get session.", e);
    }

    if(aiSessionContext == null) {
      aiSessionContext = newSessionContext(aiSessionKey);
    }

    return aiSessionContext;
  }

  protected abstract AiSessionContext newSessionContext(AiSessionKey aiSessionKey);
}