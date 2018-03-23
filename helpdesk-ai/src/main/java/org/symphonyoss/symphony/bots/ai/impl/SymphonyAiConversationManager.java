package org.symphonyoss.symphony.bots.ai.impl;

import org.symphonyoss.symphony.bots.ai.conversation.NullConversation;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all current Ai conversations.
 * <p>
 * Created by nick.tarsillo on 8/27/17.
 */
public class SymphonyAiConversationManager {

  private static final AiConversation DEFAULT_CONVERSATION = new NullConversation();

  private Map<AiSessionContext, AiConversation> conversations = new ConcurrentHashMap<>();

  /**
   * Register an new Ai Conversation.
   * @param aiSessionContext the session context for the conversation.
   * @param aiConversation the conversation to register.
   */
  public void registerConversation(AiSessionContext aiSessionContext, AiConversation aiConversation) {
    conversations.put(aiSessionContext, aiConversation);
  }

  /**
   * Removes an Ai Conversation.
   * @param aiSessionContext
   */
  public void removeConversation(AiSessionContext aiSessionContext) {
    conversations.remove(aiSessionContext);
  }

  /**
   * Gets an Ai Conversation.
   * @param aiSessionContext the session context associated with the Ai Conversation
   * @return
   */
  public AiConversation getConversation(AiSessionContext aiSessionContext) {
    if (conversations.containsKey(aiSessionContext)) {
      return conversations.get(aiSessionContext);
    }

    return DEFAULT_CONVERSATION;
  }
}
