package org.symphonyoss.symphony.bots.ai.model;

import org.symphonyoss.symphony.bots.ai.AiAction;
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

  private String sessionName;

  private AiSessionKey aiSessionKey;

  private AiCommandMenu aiCommandMenu;

  private AiAction lastCommand;

  private SymphonyAiMessage lastMessage;

  public AiSessionKey getAiSessionKey() {
    return aiSessionKey;
  }

  public void setAiSessionKey(AiSessionKey aiSessionKey) {
    this.aiSessionKey = aiSessionKey;
  }

  public AiCommandMenu getAiCommandMenu() {
    return aiCommandMenu;
  }

  public void setAiCommandMenu(AiCommandMenu aiCommandMenu) {
    this.aiCommandMenu = aiCommandMenu;
  }

  public String getSessionName() {
    return sessionName;
  }

  public void setSessionName(String sessionName) {
    this.sessionName = sessionName;
  }

  public AiAction getLastCommand() {
    return lastCommand;
  }

  public void setLastCommand(AiAction lastCommand) {
    this.lastCommand = lastCommand;
  }

  public SymphonyAiMessage getLastMessage() {
    return lastMessage;
  }

  public void setLastMessage(SymphonyAiMessage lastMessage) {
    this.lastMessage = lastMessage;
  }
}
