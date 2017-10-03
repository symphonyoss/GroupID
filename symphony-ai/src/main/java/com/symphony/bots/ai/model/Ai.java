package com.symphony.bots.ai.model;

import com.symphony.bots.ai.AiEventListener;

/**
 * Created by nick.tarsillo on 8/30/17.
 * Representation of an Ai.
 */
public abstract class Ai {
  public void onAiMessage(AiSessionKey aiSessionKey, AiMessage message) {
    AiSessionContext sessionContext =  getAiSessionContextManager().getSessionContext(aiSessionKey);
    AiConversation aiConversation = getAiConversationManager().getConversation(sessionContext);

    if(aiConversation == null || aiConversation.isAllowCommands()) {
      getAiEventListener().onCommand(message, sessionContext);
    }

    if(aiConversation != null) {
      getAiEventListener().onConversation(message, aiConversation);
    }
  }

  public void startConversation(AiSessionKey aiSessionKey, AiConversation aiConversation) {
    AiSessionContext aiSessionContext = getAiSessionContextManager().getSessionContext(aiSessionKey);
    aiConversation.setAiSessionContext(aiSessionContext);
    getAiConversationManager().registerConversation(aiConversation.getAiSessionContext(), aiConversation);
  }

  public AiConversation getConversation(AiSessionKey aiSessionKey) {
    AiSessionContext aiSessionContext = getAiSessionContextManager().getSessionContext(aiSessionKey);
    return getAiConversationManager().getConversation(aiSessionContext);
  }

  public void endConversation(AiSessionKey aiSessionKey) {
    AiSessionContext aiSessionContext = getAiSessionContextManager().getSessionContext(aiSessionKey);
    getAiConversationManager().removeConversation(aiSessionContext);
  }

  protected abstract AiEventListener getAiEventListener();

  protected abstract AiSessionContextManager getAiSessionContextManager();

  protected abstract AiConversationManager getAiConversationManager();
}
