package org.symphonyoss.symphony.bots.ai.impl;

import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;

/**
 * Created by nick.tarsillo on 11/10/17.
 */
public class SymphonyAiSessionContext extends AiSessionContext {
  private SymphonyAiChatListener symphonyAiChatListener;
  private SymphonyAiMessageListener symphonyAiMessageListener;

  public SymphonyAiChatListener getSymphonyAiChatListener() {
    return symphonyAiChatListener;
  }

  public void setSymphonyAiChatListener(
      SymphonyAiChatListener symphonyAiChatListener) {
    this.symphonyAiChatListener = symphonyAiChatListener;
  }

  public SymphonyAiMessageListener getSymphonyAiMessageListener() {
    return symphonyAiMessageListener;
  }

  public void setSymphonyAiMessageListener(
      SymphonyAiMessageListener symphonyAiMessageListener) {
    this.symphonyAiMessageListener = symphonyAiMessageListener;
  }
}
