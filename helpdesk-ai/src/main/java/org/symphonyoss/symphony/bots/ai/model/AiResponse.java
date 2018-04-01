package org.symphonyoss.symphony.bots.ai.model;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class represents a response to be sent from the Ai.
 * <p>
 * Created by nick.tarsillo on 8/20/17.
 */
public class AiResponse {

  private AiMessage message;

  private Set<String> respondTo;

  /**
   * Creates a new {@link AiResponse} instance with the response text and where the answer should
   * be sent to
   * @param message the message text
   * @param respondTo who should receive the message
   */
  public AiResponse(AiMessage message, String... respondTo) {
    this.message = message;
    this.respondTo = Arrays.stream(respondTo).collect(Collectors.toSet());
  }

  public AiResponse(AiMessage message, Set<String> respondTo) {
    this.message = message;
    this.respondTo = respondTo;
  }

  public AiMessage getMessage() {
    return message;
  }

  public Set<String> getRespondTo() {
    return respondTo;
  }
}
