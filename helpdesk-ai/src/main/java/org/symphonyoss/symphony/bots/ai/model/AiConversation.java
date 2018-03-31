package org.symphonyoss.symphony.bots.ai.model;

import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;

import java.util.HashSet;
import java.util.Set;

/**
 * A conversation between a user and the bot. Used to create contextual Ai conversations.
 * <p>
 * Created by nick.tarsillo on 8/27/17.
 */
public abstract class AiConversation {

  protected AiCommandMenu aiCommandMenu;

  protected boolean allowCommands;

  protected String lastMessageId;

  public AiConversation(boolean allowCommands) {
    this.allowCommands = allowCommands;
  }

  public AiConversation(boolean allowCommands, AiCommandMenu aiCommandMenu) {
    this.aiCommandMenu = aiCommandMenu;
    this.allowCommands = allowCommands;
  }

  /**
   * Called when a conversation message is sent
   * @param responder object used to respond the message
   * @param message the message itself
   */
  public abstract void onMessage(AiResponder responder, SymphonyAiMessage message);

  /**
   * Check if this conversation allow commands
   * @return true if the conversation allows commands, false otherwise
   */
  public boolean isAllowCommands() {
    return allowCommands;
  }

  /**
   * Set command menu
   * @param aiCommandMenu List of available commands
   */
  public void setAiCommandMenu(AiCommandMenu aiCommandMenu) {
    this.aiCommandMenu = aiCommandMenu;
  }

  /**
   * Retrieve command menu.
   * @return List of available commands
   */
  public AiCommandMenu getAiCommandMenu() {
    return aiCommandMenu;
  }

  /**
   * Retrieve the conversation last message
   * @return last message
   */
  public String getLastMessageId() {
    return lastMessageId;
  }

  /**
   * Set this conversation last message
   * @param lastMessageId last message
   */
  public void setLastMessageId(String lastMessageId) {
    this.lastMessageId = lastMessageId;
  }
}
