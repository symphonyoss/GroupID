package org.symphonyoss.symphony.bots.ai.impl;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.ai.Ai;
import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.AiEventListener;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.AiResponseIdentifier;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.ai.model.AiResponse;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.ai.model.SymphonyAiSessionKey;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.Set;

/**
 * Main entry point for the <i>Agent Interface</i> messages. This class works as both session context manager and
 * message listener, managing all messages sent to this AI.
 * <p>
 * Created by nick.tarsillo on 8/20/17.
 */
public class SymphonyAi implements Ai {

  protected AiEventListener aiEventListener;

  protected SymphonyAiSessionContextManager aiSessionContextManager;

  protected SymphonyAiConversationManager aiConversationManager;

  protected AiResponder aiResponder;

  public SymphonyAi(SymphonyClient symphonyClient, boolean suggestCommand) {
    AiCommandInterpreter aiCommandInterpreter = new SymphonyAiCommandInterpreter(symphonyClient.getLocalUser());
    aiResponder = new SymphonyAiResponder(symphonyClient.getMessagesClient());
    aiEventListener = new SymphonyAiEventListenerImpl(aiCommandInterpreter, aiResponder, suggestCommand);
    aiSessionContextManager = new SymphonyAiSessionContextManager();
    aiConversationManager = new SymphonyAiConversationManager();
  }

  public void onMessage(SymMessage symMessage) {
    SymphonyAiSessionKey aiSessionKey = getSessionKey(symMessage.getFromUserId(), symMessage.getStreamId());
    onAiMessage(aiSessionKey, new SymphonyAiMessage(symMessage));
  }

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
  @Override
  public void onAiMessage(SymphonyAiSessionKey aiSessionKey, SymphonyAiMessage message) {
    AiSessionContext sessionContext =  getSessionContext(aiSessionKey);
    AiConversation aiConversation = aiConversationManager.getConversation(sessionContext);

    if(sessionContext.getLastMessage() == null || !sessionContext.getLastMessage().equals(message)) {
      if ((aiConversation == null || aiConversation.isAllowCommands()) &&
          sessionContext.getAiCommandMenu() != null) {
        aiEventListener.onCommand(message, sessionContext);
      }

      if (aiConversation != null) {
        aiEventListener.onConversation(message, aiConversation);
      }

      sessionContext.setLastMessage(message);
    }
  }

  @Override
  public void startConversation(SymphonyAiSessionKey aiSessionKey, AiConversation aiConversation) {
    AiSessionContext aiSessionContext = getSessionContext(aiSessionKey);
    aiConversation.setAiSessionContext(aiSessionContext);

    aiConversationManager.registerConversation(aiSessionContext, aiConversation);
  }

  /**
   * Retrieves a conversation in the given session context
   * @param aiSessionKey a session context key
   * @return The conversation in the given session context, if it exists
   */
  @Override
  public AiConversation getConversation(SymphonyAiSessionKey aiSessionKey) {
    AiSessionContext aiSessionContext = aiSessionContextManager.getSessionContext(aiSessionKey);
    return aiConversationManager.getConversation(aiSessionContext);
  }

  /**
   * Removes the a conversation from the given session context
   * @param aiSessionKey a session context key
   */
  @Override
  public void endConversation(SymphonyAiSessionKey aiSessionKey) {
    AiSessionContext aiSessionContext = aiSessionContextManager.getSessionContext(aiSessionKey);
    aiConversationManager.removeConversation(aiSessionContext);
  }

  /**
   * Sends the given message to the session context with the given {@link SymphonyAiSessionKey session key}
   * @param aiMessage message to send
   * @param responseIdentifierSet set with the ids to where the message should be sent
   * @param aiSessionKey a session context key
   */
  @Override
  public void sendMessage(SymphonyAiMessage aiMessage,
      Set<AiResponseIdentifier> responseIdentifierSet, SymphonyAiSessionKey aiSessionKey) {
    AiSessionContext aiSessionContext = getSessionContext(aiSessionKey);

    AiResponse aiResponse = new AiResponse(aiMessage, responseIdentifierSet);
    aiResponder.addResponse(aiSessionContext, aiResponse);
    aiResponder.respond(aiSessionContext);
  }

  /**
   * Retrieve the session context with the given {@link SymphonyAiSessionKey session key}
   * @param aiSessionKey a session context key
   * @return session context with the given key
   */
  @Override
  public AiSessionContext getSessionContext(SymphonyAiSessionKey aiSessionKey) {
    AiSessionContext sessionContext = aiSessionContextManager.getSessionContext(aiSessionKey);

    if(sessionContext == null) {
      sessionContext = newAiSessionContext(aiSessionKey);
      aiSessionContextManager.putSessionContext(aiSessionKey, sessionContext);
    }

    return sessionContext;
  }

  @Override
  public AiSessionContext newAiSessionContext(SymphonyAiSessionKey aiSessionKey) {
    AiSessionContext aiSessionContext = new AiSessionContext();
    aiSessionContext.setAiSessionKey(aiSessionKey);

    return aiSessionContext;
  }

  /**
   * Retrieve a new {@link SymphonyAiSessionKey AI session key} using the userId and streamId as the key
   * @param userId user id used to build the session key
   * @param streamId stream id used to build the session key
   * @return a new instance of a {@link SymphonyAiSessionKey AI session key}
   */
  public SymphonyAiSessionKey getSessionKey(Long userId, String streamId) {
    return new SymphonyAiSessionKey(userId + ":" + streamId, userId, streamId);
  }

}
