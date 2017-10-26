package org.symphonyoss.symphony.bots.ai.impl;

import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContextManager;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;

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
