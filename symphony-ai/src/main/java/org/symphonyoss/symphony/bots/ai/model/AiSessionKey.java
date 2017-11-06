package org.symphonyoss.symphony.bots.ai.model;

import java.util.Objects;

/**
 * Created by nick.tarsillo on 8/20/17.
 * A key identifying an Ai session.
 */
public class AiSessionKey {
  private String sessionKey;

  public AiSessionKey(String sessionKey) {
    this.sessionKey = sessionKey;
  }

  public String getSessionKey() {
    return sessionKey;
  }

  @Override
  public boolean equals(Object other) {
    return (other instanceof AiSessionKey
        && Objects.equals(sessionKey, ((AiSessionKey) other).getSessionKey()));
  }

  @Override
  public int hashCode() {
    return Objects.hash(sessionKey);
  }
}
