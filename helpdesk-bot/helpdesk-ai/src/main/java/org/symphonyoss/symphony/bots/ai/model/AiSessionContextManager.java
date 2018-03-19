package org.symphonyoss.symphony.bots.ai.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.bots.ai.common.AiConstants;

/**
 * Manages all sessions in the Ai.
 * <p>
 * Created by nick.tarsillo on 8/20/17.
 */
public class AiSessionContextManager {
  private static final Logger LOG = LoggerFactory.getLogger(AiSessionContextManager.class);

  protected LoadingCache<AiSessionKey, AiSessionContext> sessionCache;

  public AiSessionContextManager() {
    sessionCache = CacheBuilder.newBuilder()
        .concurrencyLevel(4)
        .maximumSize(10000)
        .expireAfterWrite(AiConstants.EXPIRE_TIME, AiConstants.EXPIRE_TIME_UNIT)
        .build(new CacheLoader<AiSessionKey, AiSessionContext>() {
          @Override
          public AiSessionContext load(AiSessionKey key) throws Exception {
            return null;
          }
        });
  }

  /**
   * Adds a session context in the cache
   * @param aiSessionKey session context key
   * @param aiSessionContext session context itself
   */
  public void putSessionContext(AiSessionKey aiSessionKey, AiSessionContext aiSessionContext) {
    sessionCache.put(aiSessionKey, aiSessionContext);
  }

  public AiSessionContext getSessionContext(AiSessionKey aiSessionKey) {
    AiSessionContext aiSessionContext = null;
    try {
      aiSessionContext = sessionCache.asMap().get(aiSessionKey);
    } catch (Exception e) {
      LOG.warn("Could not get session.", e);
    }

    return aiSessionContext;
  }
}