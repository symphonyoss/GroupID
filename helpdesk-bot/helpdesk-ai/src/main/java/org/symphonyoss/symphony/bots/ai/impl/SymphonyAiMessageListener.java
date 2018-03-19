package org.symphonyoss.symphony.bots.ai.impl;

import org.symphonyoss.client.services.MessageListener;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;

/**
 * Concrete implementation of {@link MessageListener Symphony's client message listener} for the
 * Symphony Ai.
 * <p>
 * Created by nick.tarsillo on 9/27/17.
 */
public abstract class SymphonyAiMessageListener implements MessageListener {
  private AiSessionKey aiSessionKey;

  public AiSessionKey getAiSessionKey() {
    return aiSessionKey;
  }

  public void setAiSessionKey(AiSessionKey aiSessionKey) {
    this.aiSessionKey = aiSessionKey;
  }
}
