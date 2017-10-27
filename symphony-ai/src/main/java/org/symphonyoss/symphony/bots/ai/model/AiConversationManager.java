package org.symphonyoss.symphony.bots.ai.model;

import org.symphonyoss.symphony.bots.ai.common.AiConstants;
import org.symphonyoss.symphony.bots.utility.file.ExpiringFileLoaderCache;

/**
 * Created by nick.tarsillo on 8/27/17.
 * Manages all current Ai conversations.
 */
public class AiConversationManager {
  private ExpiringFileLoaderCache<AiSessionContext, AiConversation> conversationCache;

  public AiConversationManager(String aiSessionContextDir) {
    conversationCache = new ExpiringFileLoaderCache<>(
        aiSessionContextDir,
        (key) -> key.getSessionName() + ":" + key.getAiSessionKey().getSessionKey(),
        AiConstants.EXPIRE_TIME,
        AiConstants.EXPIRE_TIME_UNIT,
        AiConversation.class);
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
    conversationCache.remove(aiSessionContext);
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
