package com.symphony.bots.ai.model;

/**
 * Created by nick.tarsillo on 10/2/17.
 * A message sent or received by the Ai.
 */
public class AiMessage {
  private String aiMessage;

  public AiMessage(String aiMessage) {
    this.aiMessage = aiMessage;
  }

  public String getAiMessage() {
    return aiMessage;
  }

  public void setAiMessage(String aiMessage) {
    this.aiMessage = aiMessage;
  }
}
