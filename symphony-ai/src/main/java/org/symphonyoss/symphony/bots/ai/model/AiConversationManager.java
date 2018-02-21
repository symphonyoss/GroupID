package org.symphonyoss.symphony.bots.ai.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.symphonyoss.symphony.bots.ai.common.AiConstants;

/**
 * Manages all current Ai conversations.
 * <p>
 * Created by nick.tarsillo on 8/27/17.
 */
public class AiConversationManager {
  private LoadingCache<AiSessionContext, AiConversation> conversationCache;

  public AiConversationManager() {
    conversationCache = CacheBuilder.newBuilder()
        .concurrencyLevel(4)
        .maximumSize(10000)
        .expireAfterWrite(AiConstants.EXPIRE_TIME, AiConstants.EXPIRE_TIME_UNIT)
        .build(new CacheLoader<AiSessionContext, AiConversation>() {
          @Override
          public AiConversation load(AiSessionContext key) throws Exception {
            return null;
          }
        });
  }

  /**
   * Register an new Ai Conversation.
   * @param aiSessionContext the session context for the conversation.
   * @param aiConversation the conversation to register.
   */
  public void registerConversation(AiSessionContext aiSessionContext, AiConversation aiConversation) {
    conversationCache.put(aiSessionContext, aiConversation);
  }

  /**
   * Removes an Ai Conversation.
   * @param aiSessionContext
   */
  public void removeConversation(AiSessionContext aiSessionContext) {
    conversationCache.invalidate(aiSessionContext);
  }

  /**
   * Gets an Ai Conversation.
   * @param aiSessionContext the session context associated with the Ai Conversation
   * @return
   */
  public AiConversation getConversation(AiSessionContext aiSessionContext) {
    try {
      return conversationCache.get(aiSessionContext);
    } catch (Exception e) {
      return null;
    }
  }
}
