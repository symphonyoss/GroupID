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

  protected AiSessionContext aiSessionContext;

  protected Set<String> previousMessages = new HashSet<>();

  protected boolean allowCommands;

  protected SymphonyAiMessage lastMessage;

  public AiConversation(boolean allowCommands) {
    this.allowCommands = allowCommands;
  }

  /**
   * Called when a conversation message is sent
   * @param responder object used to respond the message
   * @param message the message itself
   */
  public abstract void onMessage(AiResponder responder, SymphonyAiMessage message);

  /**
   * Retrieve the previous messages in the conversation
   * @return {@link Set<String> containing the previous conversations}
   */
  public Set<String> getPreviousMessages() {
    return previousMessages;
  }

  /**
   * Check if this conversation allow commands
   * @return true if the conversation allows commands, false otherwise
   */
  public boolean isAllowCommands() {
    return allowCommands;
  }

  /**
   * Turn on/off the flag to accept commands in this conversation
   * @param allowCommands true if this conversation can have command messages, false otherwise
   */
  public void setAllowCommands(boolean allowCommands) {
    this.allowCommands = allowCommands;
  }

  /**
   * Retrieve this conversation session context
   * @return {@link AiSessionContext session context} for this conversation
   */
  public AiSessionContext getAiSessionContext() {
    return aiSessionContext;
  }

  /**
   * Set this conversation session context
   * @param sessionContext {@link AiSessionContext session context} for this conversation
   */
  public void setAiSessionContext(AiSessionContext sessionContext) {
    this.aiSessionContext = sessionContext;
  }

  /**
   * Retrieve the conversation last message
   * @return last message
   */
  public SymphonyAiMessage getLastMessage() {
    return lastMessage;
  }

  /**
   * Set this conversation last message
   * @param lastMessage last message
   */
  public void setLastMessage(SymphonyAiMessage lastMessage) {
    this.lastMessage = lastMessage;
  }
}
