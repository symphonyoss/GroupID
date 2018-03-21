package org.symphonyoss.symphony.bots.ai.model;

import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;

/**
 * Class representing the context for a session in the Ai.<br>
 * It's identified by a session key an kepts the last command and the last message in the session.
 * <br>
 * Also it has the command menu (supported commands) for this session.
 * <p>
 * Created by nick.tarsillo on 8/20/17.
 */
public class AiSessionContext {

  private SymphonyAiSessionKey aiSessionKey;

  private AiCommandMenu aiCommandMenu;

  private String lastMessageId;

  public SymphonyAiSessionKey getAiSessionKey() {
    return aiSessionKey;
  }

  public void setAiSessionKey(SymphonyAiSessionKey aiSessionKey) {
    this.aiSessionKey = aiSessionKey;
  }

  public AiCommandMenu getAiCommandMenu() {
    return aiCommandMenu;
  }

  public void setAiCommandMenu(AiCommandMenu aiCommandMenu) {
    this.aiCommandMenu = aiCommandMenu;
  }

  public String getSessionName() {
    return aiSessionKey.getSessionKey();
  }

  public String getLastMessageId() {
    return lastMessageId;
  }

  public void setLastMessageId(String lastMessageId) {
    this.lastMessageId = lastMessageId;
  }
}
