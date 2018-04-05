package org.symphonyoss.symphony.bots.ai;

import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;

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
  void onAiMessage(AiSessionKey aiSessionKey, AiMessage message);

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
   * @param responseIdentifiers array with the ids to where the message should be sent
   */
  void sendMessage(AiMessage aiMessage, String... responseIdentifiers);

  /**
   * Creates a new command menu based on session key
   * @param aiSessionKey session key
   * @return List of available commands
   */
  AiCommandMenu newAiCommandMenu(AiSessionKey aiSessionKey);

}
