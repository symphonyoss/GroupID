package org.symphonyoss.symphony.bots.ai.impl;

import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;

/**
 * Symphony AI implementation for {@link AiSessionKey}
 * <p>
 * Created by nick.tarsillo on 11/10/17.
 */
public class SymphonyAiSessionKey extends AiSessionKey {

  private String streamId;

  private Long uid;

  public SymphonyAiSessionKey(String sessionKey, Long uid, String streamId) {
    super(sessionKey);
    this.streamId = streamId;
    this.uid = uid;
  }

  public String getStreamId() {
    return streamId;
  }

  public void setStreamId(String streamId) {
    this.streamId = streamId;
  }

  public Long getUid() {
    return uid;
  }

  public void setUid(Long uid) {
    this.uid = uid;
  }
}
