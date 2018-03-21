package org.symphonyoss.symphony.bots.ai.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.bots.ai.common.AiConstants;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.ai.model.SymphonyAiSessionKey;

/**
 * Manages all sessions in the Ai.
 * <p>
 * Created by nick.tarsillo on 8/20/17.
 */
public class SymphonyAiSessionContextManager {
  private static final Logger LOG = LoggerFactory.getLogger(SymphonyAiSessionContextManager.class);

  protected LoadingCache<SymphonyAiSessionKey, AiSessionContext> sessionCache;

  public SymphonyAiSessionContextManager() {
    sessionCache = CacheBuilder.newBuilder()
        .concurrencyLevel(4)
        .maximumSize(10000)
        .expireAfterWrite(AiConstants.EXPIRE_TIME, AiConstants.EXPIRE_TIME_UNIT)
        .build(new CacheLoader<SymphonyAiSessionKey, AiSessionContext>() {
          @Override
          public AiSessionContext load(SymphonyAiSessionKey key) throws Exception {
            return null;
          }
        });
  }

  /**
   * Adds a session context in the cache
   * @param aiSessionKey session context key
   * @param aiSessionContext session context itself
   */
  public void putSessionContext(SymphonyAiSessionKey aiSessionKey, AiSessionContext aiSessionContext) {
    sessionCache.put(aiSessionKey, aiSessionContext);
  }

  public AiSessionContext getSessionContext(SymphonyAiSessionKey aiSessionKey) {
    AiSessionContext aiSessionContext = null;
    try {
      aiSessionContext = sessionCache.asMap().get(aiSessionKey);
    } catch (Exception e) {
      LOG.warn("Could not get session.", e);
    }

    return aiSessionContext;
  }
}