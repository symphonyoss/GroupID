package org.symphonyoss.symphony.bots.ai.model;

import org.symphonyoss.symphony.bots.ai.AiEventListener;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.AiResponseIdentifier;

import java.util.Set;

/**
 * This class groups the AI Conversation main functionalities
 * <p>
 * Created by nick.tarsillo on 8/30/17.
 */
public abstract class Ai {

  /**
   *  Proxy a message event to the corresponding event listener: <br>
   *  <ul>
   *    <li>A command event listener if the message is a command and the conversation allows it</li>
   *    <li>A conversation message event listener if the message is a simple message</li>
   *  </ul>
   *  And then add it to the context
   * @param aiSessionKey The key for a session context
   * @param message The received message
   */
  public void onAiMessage(AiSessionKey aiSessionKey, AiMessage message) {
    AiSessionContext sessionContext =  getSessionContext(aiSessionKey);
    AiConversation aiConversation = getAiConversationManager().getConversation(sessionContext);

    if(sessionContext.getLastMessage() == null || !sessionContext.getLastMessage().equals(message)) {
      if ((aiConversation == null || aiConversation.isAllowCommands()) &&
          sessionContext.getAiCommandMenu() != null) {
        getAiEventListener().onCommand(message, sessionContext);
      }

      if (aiConversation != null) {
        getAiEventListener().onConversation(message, aiConversation);
      }

      sessionContext.setLastMessage(message);
    }
  }

  /**
   * Registers a new conversation in the conversation manager
   * @param aiSessionKey a session context key
   * @param aiConversation conversation to be started
   */
  public void startConversation(AiSessionKey aiSessionKey, AiConversation aiConversation) {
    AiSessionContext aiSessionContext = getSessionContext(aiSessionKey);
    aiConversation.setAiSessionContext(aiSessionContext);
    getAiConversationManager().registerConversation(aiSessionContext, aiConversation);
  }

  /**
   * Retrieves a conversation in the given session context
   * @param aiSessionKey a session context key
   * @return The conversation in the given session context, if it exists
   */
  public AiConversation getConversation(AiSessionKey aiSessionKey) {
    AiSessionContext aiSessionContext = getAiSessionContextManager().getSessionContext(aiSessionKey);
    return getAiConversationManager().getConversation(aiSessionContext);
  }

  /**
   * Removes the a conversation from the given session context
   * @param aiSessionKey a session context key
   */
  public void endConversation(AiSessionKey aiSessionKey) {
    AiSessionContext aiSessionContext = getAiSessionContextManager().getSessionContext(aiSessionKey);
    getAiConversationManager().removeConversation(aiSessionContext);
  }

  /**
   * Sends the given message to the session context with the given {@link AiSessionKey session key}
   * @param aiMessage message to send
   * @param responseIdentifierSet set with the ids to where the message should be sent
   * @param aiSessionKey a session context key
   */
  public void sendMessage(AiMessage aiMessage, Set<AiResponseIdentifier> responseIdentifierSet, AiSessionKey aiSessionKey) {
    AiSessionContext aiSessionContext = getSessionContext(aiSessionKey);
    AiResponse aiResponse = new AiResponse(aiMessage, responseIdentifierSet);
    getAiResponder().addResponse(aiSessionContext, aiResponse);
    getAiResponder().respond(aiSessionContext);
  }

  /**
   * Retrieve the session context with the given {@link AiSessionKey session key}
   * @param aiSessionKey a session context key
   * @return session context with the given key
   */
  public AiSessionContext getSessionContext(AiSessionKey aiSessionKey) {
    AiSessionContext sessionContext = getAiSessionContextManager().getSessionContext(aiSessionKey);
    if(sessionContext == null) {
      sessionContext = newAiSessionContext(aiSessionKey);
      getAiSessionContextManager().putSessionContext(aiSessionKey, sessionContext);
    }

    return sessionContext;
  }

  /**
   * Retrieve the AI event listener
   * @return AI event listener for the current session
   */
  protected abstract AiEventListener getAiEventListener();

  /**
   * Retrieve the AI session context manager
   * @return AI session context manager
   */
  protected abstract AiSessionContextManager getAiSessionContextManager();

  /**
   * Retrieve the AI conversation manager
   * @return AI conversation manager
   */
  protected abstract AiConversationManager getAiConversationManager();

  /**
   * Get the current AI responder
   * @return AI responder
   */
  protected abstract AiResponder getAiResponder();

  /**
   * Creates a new session context with the given key
   * @param aiSessionKey key to be used as the session context key
   * @return
   */
  public abstract AiSessionContext newAiSessionContext(AiSessionKey aiSessionKey);
}
