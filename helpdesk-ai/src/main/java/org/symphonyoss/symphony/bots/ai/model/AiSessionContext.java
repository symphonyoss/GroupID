package org.symphonyoss.symphony.bots.ai.model;

/**
 * Class representing the context for a session in the Ai.<br>
 * It's identified by a session key an kepts the last command and the last message in the session.
 * <br>
 * Also it has the command menu (supported commands) for this session.
 * <p>
 * Created by nick.tarsillo on 8/20/17.
 */
public class AiSessionContext {

  private final SymphonyAiSessionKey aiSessionKey;

  private AiCommandMenu aiCommandMenu;

  private String lastMessageId;

  public AiSessionContext(SymphonyAiSessionKey aiSessionKey) {
    this.aiSessionKey = aiSessionKey;
  }

  public SymphonyAiSessionKey getAiSessionKey() {
    return aiSessionKey;
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

  public boolean allowCommands() {
    return aiCommandMenu != null;
  }

}
