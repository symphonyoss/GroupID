package org.symphonyoss.symphony.bots.ai;

import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;

import java.util.Set;

/**
 * Interface to implement an <i>Agent Interface</i> Conversation
 * <p>
 * Created by nick.tarsillo on 8/30/17.
 */
public interface Ai {

  /**
   * Listener method to deal with AI message.
   *
   * @param aiSessionKey The key for a session context
   * @param message The received message
   */
  void onAiMessage(AiSessionKey aiSessionKey, SymphonyAiMessage message);

  /**
   * Registers a new conversation in the conversation manager
   * @param aiSessionKey a session context key
   * @param aiConversation conversation to be started
   */
  void startConversation(AiSessionKey aiSessionKey, AiConversation aiConversation);

  /**
   * Retrieves a conversation in the given session context
   * @param aiSessionKey a session context key
   * @return The conversation in the given session context, if it exists
   */
  AiConversation getConversation(AiSessionKey aiSessionKey);

  /**
   * Removes the a conversation from the given session context
   * @param aiSessionKey a session context key
   */
  void endConversation(AiSessionKey aiSessionKey);

  /**
   * Sends the given message to the session context with the given {@link AiSessionKey session key}
   * @param aiMessage message to send
   * @param responseIdentifierSet set with the ids to where the message should be sent
   * @param aiSessionKey a session context key
   */
  void sendMessage(SymphonyAiMessage aiMessage, Set<AiResponseIdentifier> responseIdentifierSet,
      AiSessionKey aiSessionKey);

  /**
   * Retrieve the session context with the given {@link AiSessionKey session key}
   * @param aiSessionKey a session context key
   * @return session context with the given key
   */
  AiSessionContext getSessionContext(AiSessionKey aiSessionKey);

  /**
   * Creates a new session context with the given key
   * @param aiSessionKey key to be used as the session context key
   * @return
   */
  AiSessionContext newAiSessionContext(AiSessionKey aiSessionKey);

}
