package org.symphonyoss.symphony.bots.ai.impl;

import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.AiEventListener;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.model.Ai;
import org.symphonyoss.symphony.bots.ai.model.AiConversationManager;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContextManager;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;

import org.symphonyoss.client.services.MessageListener;
import org.symphonyoss.symphony.clients.MessagesClient;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Created by nick.tarsillo on 8/20/17.
 */
public class SymphonyAi extends Ai {
  protected AiEventListener aiEventListener;
  protected AiSessionContextManager aiSessionContextManager;
  protected AiConversationManager aiConversationManager;

  public SymphonyAi(MessagesClient messagesClient, boolean suggestCommand, String sessionContext) {
    AiCommandInterpreter aiCommandInterpreter = new AiCommandInterpreterImpl();
    AiResponder aiResponder = new SymphonyAiResponder(messagesClient);
    aiEventListener = new AiEventListenerImpl(aiCommandInterpreter, aiResponder, suggestCommand);
    aiSessionContextManager = new AiSessionContextManagerImpl(sessionContext);
    aiConversationManager = new AiConversationManager(sessionContext);
  }

  /**
   * Create a symphony Ai session.
   * Creates a session unique to the user and the stream they are in.
   * @param userId the id of the user.
   * @param streamId the stream id of the user.
   * @return a chat listener that allows the Ai to listen to chats, using SJC.
   */
  public AiSymphonyChatListener createNewSymphonySession(Long userId, String streamId) {
    AiSessionKey aiSessionKey = new AiSessionKey(userId + ":" + streamId);
    AiSymphonyChatListener chatListener = new AiSymphonyChatListener() {
      @Override
      public void onChatMessage(SymMessage message) {
        AiSessionKey key = aiSessionKey;
        if(message.getFromUserId().equals(userId) && message.getStreamId().equals(streamId)) {
          onAiMessage(key, new SymphonyAiMessage(message));
        }
      }
    };

    chatListener.setAiSessionKey(aiSessionKey);

    return chatListener;
  }

  /**
   * Create a symphony Ai session.
   * Creates a session unique to the user.
   * @param userId the id of the user.
   * @return a chat listener that allows the Ai to listen to chats, using SJC.
   */
  public MessageListener createNewSymphonySession(String userId) {
    AiSessionKey aiSessionKey = new AiSessionKey("" + userId);
    AiSymphonyMessageListener messageListener = new AiSymphonyMessageListener() {
      @Override
      public void onMessage(SymMessage message) {
        AiSessionKey key = aiSessionKey;
        if(message.getFromUserId().equals(userId)) {
          onAiMessage(key, new SymphonyAiMessage(message));
        }
      }
    };

    messageListener.setAiSessionKey(aiSessionKey);

    return messageListener;
  }

  public AiSessionKey getSessionKey(String userId) {
    return new AiSessionKey("" + userId);
  }

  public AiSessionKey getSessionKey(String userId, String streamId) {
    return new AiSessionKey(userId + ":" + streamId);
  }

  @Override
  protected AiEventListener getAiEventListener() {
    return aiEventListener;
  }

  @Override
  protected AiSessionContextManager getAiSessionContextManager() {
    return aiSessionContextManager;
  }

  @Override
  protected AiConversationManager getAiConversationManager() {
    return aiConversationManager;
  }
}
