package org.symphonyoss.symphony.bots.ai.model;

import java.util.Objects;

/**
 * This class represents the object used as the session context key for an AI session.
 * <p>
 * Created by nick.tarsillo on 8/20/17.
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
