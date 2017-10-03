package com.symphony.bots.ai.model;

import com.symphony.bots.ai.AiResponseIdentifier;

import java.util.Set;

/**
 * Created by nick.tarsillo on 8/20/17.
 * A response from the Ai.
 */
public class AiResponse {
  private AiMessage message;
  private Set<AiResponseIdentifier> respondTo;

  public AiResponse(AiMessage message, Set<AiResponseIdentifier> respondTo) {
    this.message = message;
    this.respondTo = respondTo;
  }

  public AiMessage getMessage() {
    return message;
  }

  public Set<AiResponseIdentifier> getRespondTo() {
    return respondTo;
  }
}
