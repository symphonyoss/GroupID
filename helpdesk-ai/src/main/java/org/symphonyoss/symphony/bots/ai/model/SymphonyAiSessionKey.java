package org.symphonyoss.symphony.bots.ai.model;

import java.util.Objects;

/**
 * This class represents the object used as the session context key for an AI session.
 * <p>
 * Created by nick.tarsillo on 11/10/17.
 */
public class SymphonyAiSessionKey {

  private String sessionKey;

  private String streamId;

  private Long uid;

  public SymphonyAiSessionKey(String sessionKey, Long uid, String streamId) {
    this.sessionKey = sessionKey;
    this.streamId = streamId;
    this.uid = uid;
  }

  public String getSessionKey() {
    return sessionKey;
  }

  public String getStreamId() {
    return streamId;
  }

  public Long getUid() {
    return uid;
  }

  @Override
  public boolean equals(Object other) {
    return (other instanceof SymphonyAiSessionKey
        && Objects.equals(sessionKey, ((SymphonyAiSessionKey) other).getSessionKey()));
  }

  @Override
  public int hashCode() {
    return Objects.hash(sessionKey);
  }
}
