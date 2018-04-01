package org.symphonyoss.symphony.bots.ai.impl;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.ai.Ai;
import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.AiEventListener;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.ai.model.AiResponse;
import org.symphonyoss.symphony.bots.ai.model.SymphonyAiSessionKey;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Main entry point for the <i>Agent Interface</i> messages. This class works as both session context manager and
 * message listener, managing all messages sent to this AI.
 * <p>
 * Created by nick.tarsillo on 8/20/17.
 */
public class SymphonyAi implements Ai {

  protected AiEventListener aiEventListener;

  protected SymphonyAiConversationManager aiConversationManager;

  protected AiResponder aiResponder;

  public SymphonyAi(SymphonyClient symphonyClient) {
    AiCommandInterpreter aiCommandInterpreter = new SymphonyAiCommandInterpreter(symphonyClient);
    aiEventListener = new SymphonyAiEventListenerImpl(aiCommandInterpreter, aiResponder);
    aiConversationManager = new SymphonyAiConversationManager();
  }

  public SymphonyAi(AiEventListener aiEventListener,
      SymphonyAiConversationManager aiConversationManager, AiResponder aiResponder) {
    this.aiEventListener = aiEventListener;
    this.aiConversationManager = aiConversationManager;
    this.aiResponder = aiResponder;
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
    AiConversation aiConversation = aiConversationManager.getConversation(aiSessionKey);
    aiEventListener.onMessage(aiSessionKey, message, aiConversation);
  }

  @Override
  public void startConversation(SymphonyAiSessionKey aiSessionKey, AiConversation aiConversation) {
    aiConversationManager.registerConversation(aiSessionKey, aiConversation);
  }

  /**
   * Retrieves a conversation in the given session context
   * @param aiSessionKey a session context key
   * @return The conversation in the given session context, if it exists
   */
  @Override
  public AiConversation getConversation(SymphonyAiSessionKey aiSessionKey) {
    return aiConversationManager.getConversation(aiSessionKey);
  }

  /**
   * Removes the a conversation from the given session context
   * @param aiSessionKey a session context key
   */
  @Override
  public void endConversation(SymphonyAiSessionKey aiSessionKey) {
    aiConversationManager.removeConversation(aiSessionKey);
  }

  @Override
  public void sendMessage(SymphonyAiMessage aiMessage, String... responseIdentifiers) {
    AiResponse aiResponse = new AiResponse(aiMessage, responseIdentifiers);
    aiResponder.respond(aiResponse);
  }

  @Override
  public AiCommandMenu newAiCommandMenu(SymphonyAiSessionKey aiSessionKey) {
    return null;
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
