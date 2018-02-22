package org.symphonyoss.symphony.bots.ai.impl;

import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.AiEventListener;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.model.Ai;
import org.symphonyoss.symphony.bots.ai.model.AiConversationManager;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContextManager;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;

/**
 * Concrete implementation of {@link Ai}
 * <p>
 * Created by nick.tarsillo on 11/10/17.
 */
public class AiImpl extends Ai {
  protected AiEventListener aiEventListener;
  protected AiSessionContextManager aiSessionContextManager;
  protected AiConversationManager aiConversationManager;
  protected AiResponder aiResponder;

  public AiImpl(boolean suggestCommand) {
    AiCommandInterpreter aiCommandInterpreter = new AiCommandInterpreterImpl();
    aiResponder = new AiResponderImpl();
    aiEventListener = new AiEventListenerImpl(aiCommandInterpreter, aiResponder, suggestCommand);
    aiSessionContextManager = new AiSessionContextManager();
    aiConversationManager = new AiConversationManager();
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

  @Override
  protected AiResponder getAiResponder() {
    return aiResponder;
  }

  @Override
  public AiSessionContext newAiSessionContext(AiSessionKey aiSessionKey) {
    AiSessionContext aiSessionContext = new AiSessionContext();
    aiSessionContext.setAiSessionKey(aiSessionKey);

    return aiSessionContext;
  }
}