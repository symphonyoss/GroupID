package org.symphonyoss.symphony.bots.ai;

import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;

/**
 * Created by nick.tarsillo on 9/29/17.
 * A session key for help desk AI sessions.
 */
public class HelpDeskAiSessionKey extends AiSessionKey {
  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getStreamId() {
    return streamId;
  }

  public void setStreamId(String streamId) {
    this.streamId = streamId;
  }

  public enum SessionType {
    AGENT_SERVICE,
    AGENT,
    CLIENT
  }

  public HelpDeskAiSessionKey(String sessionKey, String groupId, SessionType sessionType) {
    super(sessionKey);
    this.sessionType = sessionType;
    this.groupId = groupId;
  }

  private SessionType sessionType;
  private String uid;
  private String streamId;
  private String groupId;

  public SessionType getSessionType() {
    return sessionType;
  }

  public void setSessionType(
      SessionType sessionType) {
    this.sessionType = sessionType;
  }
}
