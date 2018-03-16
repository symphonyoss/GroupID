package org.symphonyoss.symphony.bots.ai.model;

/**
 * This class represents a message sent or received by the Ai.
 * <p>
 * Created by nick.tarsillo on 10/2/17.
 */
public class AiMessage {
  private String aiMessage;

  /**
   * Instantiates a new {@link AiMessage} with the message text
   * @param aiMessage the message text
   */
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
