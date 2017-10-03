package com.symphony.bots.ai.impl;

import com.symphony.bots.ai.model.AiSessionKey;

import org.symphonyoss.client.services.ChatListener;

/**
 * Created by nick.tarsillo on 9/27/17.
 * Symphony chat listener for Symphony Ai.
 */
public abstract class AiSymphonyChatListener implements ChatListener {
  private AiSessionKey aiSessionKey;

  public AiSessionKey getAiSessionKey() {
    return aiSessionKey;
  }

  public void setAiSessionKey(AiSessionKey aiSessionKey) {
    this.aiSessionKey = aiSessionKey;
  }
}
