package org.symphonyoss.symphony.bots.ai.model;

import org.symphonyoss.symphony.bots.ai.AiAction;

/**
 * Created by nick.tarsillo on 8/20/17.
 * A context for a users current session the Ai.
 */
public class AiSessionContext {
  private String sessionName;
  private AiSessionKey aiSessionKey;
  private AiCommandMenu aiCommandMenu;
  private AiAction lastCommand;
  private AiMessage lastMessage;

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

  public AiMessage getLastMessage() {
    return lastMessage;
  }

  public void setLastMessage(AiMessage lastMessage) {
    this.lastMessage = lastMessage;
  }
}
