package com.symphony.bots.ai.impl;

import com.symphony.bots.ai.model.AiSessionKey;

import org.symphonyoss.client.services.MessageListener;

/**
 * Created by nick.tarsillo on 9/27/17.
 * SJC message listener for the Symphony Ai.
 */
public abstract class AiSymphonyMessageListener implements MessageListener {
  private AiSessionKey aiSessionKey;

  public AiSessionKey getAiSessionKey() {
    return aiSessionKey;
  }

  public void setAiSessionKey(AiSessionKey aiSessionKey) {
    this.aiSessionKey = aiSessionKey;
  }
}
