package com.symphony.bots.helpdesk.model.ai;

import com.symphony.bots.ai.model.AiSessionKey;

/**
 * Created by nick.tarsillo on 9/29/17.
 * A session key for help desk AI sessions.
 */
public class HelpDeskAiSessionKey extends AiSessionKey {
  public enum SessionType {
    AGENT_SERVICE,
    AGENT,
    CLIENT
  }

  public HelpDeskAiSessionKey(String sessionKey, SessionType sessionType) {
    super(sessionKey);
    this.sessionType = sessionType;
  }

  private SessionType sessionType;

  public SessionType getSessionType() {
    return sessionType;
  }

  public void setSessionType(
      SessionType sessionType) {
    this.sessionType = sessionType;
  }
}
