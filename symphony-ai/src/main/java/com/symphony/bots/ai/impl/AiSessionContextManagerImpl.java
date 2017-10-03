package com.symphony.bots.ai.impl;

import com.symphony.bots.ai.model.AiSessionContext;
import com.symphony.bots.ai.model.AiSessionContextManager;
import com.symphony.bots.ai.model.AiSessionKey;

/**
 * Created by nick.tarsillo on 10/2/17.
 */
public class AiSessionContextManagerImpl extends AiSessionContextManager{
  public AiSessionContextManagerImpl(String aiSessionContextDir) {
    super(aiSessionContextDir);
  }

  @Override
  protected AiSessionContext newSessionContext(AiSessionKey aiSessionKey) {
    AiSessionContext aiSessionContext = new AiSessionContext();
    aiSessionContext.setAiSessionKey(aiSessionKey);
    return aiSessionContext;
  }
}
