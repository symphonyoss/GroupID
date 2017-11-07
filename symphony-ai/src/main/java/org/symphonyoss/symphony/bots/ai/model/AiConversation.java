package org.symphonyoss.symphony.bots.ai.model;

import org.symphonyoss.symphony.bots.ai.AiResponder;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by nick.tarsillo on 8/27/17.
 * A conversation between a user and the bot. Used to create contextual Ai conversations.
 */
public abstract class  AiConversation {
  protected AiSessionContext aiSessionContext;
  protected Set<String> previousMessages = new HashSet<>();
  protected boolean allowCommands;

  public AiConversation(boolean allowCommands) {
    this.allowCommands = allowCommands;
  }

  public abstract void onMessage(AiResponder responder, AiMessage message);

  public Set<String> getPreviousMessages() {
    return previousMessages;
  }

  public boolean isAllowCommands() {
    return allowCommands;
  }

  public void setAllowCommands(boolean allowCommands) {
    this.allowCommands = allowCommands;
  }

  public AiSessionContext getAiSessionContext() {
    return aiSessionContext;
  }

  public void setAiSessionContext(AiSessionContext sessionContext) {
    this.aiSessionContext = sessionContext;
  }
}
