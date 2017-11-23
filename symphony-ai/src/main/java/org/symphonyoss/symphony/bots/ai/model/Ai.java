package org.symphonyoss.symphony.bots.ai.model;

import org.symphonyoss.symphony.bots.ai.AiEventListener;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.AiResponseIdentifier;

import java.util.Set;

/**
 * Created by nick.tarsillo on 8/30/17.
 * Representation of an Ai.
 */
public abstract class Ai {
  public void onAiMessage(AiSessionKey aiSessionKey, AiMessage message) {
    AiSessionContext sessionContext =  getSessionContext(aiSessionKey);
    AiConversation aiConversation = getAiConversationManager().getConversation(sessionContext);

    if((aiConversation == null || aiConversation.isAllowCommands()) &&
        sessionContext.getAiCommandMenu() != null) {
      getAiEventListener().onCommand(message, sessionContext);
    }

    if(aiConversation != null) {
      getAiEventListener().onConversation(message, aiConversation);
    }

    sessionContext.setLastMessage(message);
  }

  public void startConversation(AiSessionKey aiSessionKey, AiConversation aiConversation) {
    AiSessionContext aiSessionContext = getSessionContext(aiSessionKey);
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

  public void sendMessage(AiMessage aiMessage, Set<AiResponseIdentifier> responseIdentifierSet, AiSessionKey aiSessionKey) {
    AiSessionContext aiSessionContext = getSessionContext(aiSessionKey);
    AiResponse aiResponse = new AiResponse(aiMessage, responseIdentifierSet);
    getAiResponder().addResponse(aiSessionContext, aiResponse);
    getAiResponder().respond(aiSessionContext);
  }

  public AiSessionContext getSessionContext(AiSessionKey aiSessionKey) {
    AiSessionContext sessionContext =  getAiSessionContextManager().getSessionContext(aiSessionKey);
    if(sessionContext == null) {
      getAiSessionContextManager().putSessionContext(aiSessionKey, new AiSessionContext());
      sessionContext = newAiSessionContext(aiSessionKey);
      getAiSessionContextManager().putSessionContext(aiSessionKey, sessionContext);
    }

    return sessionContext;
  }

  protected abstract AiEventListener getAiEventListener();

  protected abstract AiSessionContextManager getAiSessionContextManager();

  protected abstract AiConversationManager getAiConversationManager();

  protected abstract AiResponder getAiResponder();

  public abstract AiSessionContext newAiSessionContext(AiSessionKey aiSessionKey);
}
