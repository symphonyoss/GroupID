package org.symphonyoss.symphony.bots.ai.impl;

import org.symphonyoss.symphony.bots.ai.conversation.NullConversation;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.ai.model.SymphonyAiSessionKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all current Ai conversations.
 * <p>
 * Created by nick.tarsillo on 8/27/17.
 */
public class SymphonyAiConversationManager {

  private static final AiConversation DEFAULT_CONVERSATION = new NullConversation();

  private Map<SymphonyAiSessionKey, AiConversation> cache = new ConcurrentHashMap<>();

  /**
   * Register an new Ai Conversation.
   * @param aiConversation the conversation to register.
   */
  public void registerConversation(AiConversation aiConversation) {
    AiSessionContext aiSessionContext = aiConversation.getAiSessionContext();
    cache.put(aiSessionContext.getAiSessionKey(), aiConversation);
  }

  /**
   * Removes an Ai Conversation.
   * @param aiSessionKey session key
   */
  public void removeConversation(SymphonyAiSessionKey aiSessionKey) {
    cache.remove(aiSessionKey);
  }

  /**
   * Gets an Ai Conversation.
   * @param aiSessionKey session key
   * @return
   */
  public AiConversation getConversation(SymphonyAiSessionKey aiSessionKey) {
    if (cache.containsKey(aiSessionKey)) {
      return cache.get(aiSessionKey);
    }

    return DEFAULT_CONVERSATION;
  }
}
