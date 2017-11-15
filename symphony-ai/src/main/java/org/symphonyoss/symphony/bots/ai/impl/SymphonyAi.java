package org.symphonyoss.symphony.bots.ai.impl;

import org.apache.commons.lang3.StringUtils;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;

import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Created by nick.tarsillo on 8/20/17.
 */
public class SymphonyAi extends AiImpl {
  private SymphonyClient symphonyClient;

  public SymphonyAi(SymphonyClient symphonyClient, boolean suggestCommand) {
    super(suggestCommand);
    AiCommandInterpreter aiCommandInterpreter = new AiCommandInterpreterImpl();
    aiResponder = new SymphonyAiResponder(symphonyClient.getMessagesClient());
    aiEventListener = new AiEventListenerImpl(aiCommandInterpreter, aiResponder, suggestCommand);

    this.symphonyClient = symphonyClient;
  }

  public AiSessionKey getSessionKey(String userId) {
    return new SymphonyAiSessionKey(userId, userId);
  }

  public AiSessionKey getSessionKey(String userId, String streamId) {
    return new SymphonyAiSessionKey(userId + ":" + streamId, userId, streamId);
  }

  @Override
  public AiSessionContext newAiSessionContext(AiSessionKey aiSessionKey) {
    SymphonyAiSessionContext aiSessionContext = new SymphonyAiSessionContext();
    SymphonyAiSessionKey sessionKey = (SymphonyAiSessionKey) aiSessionKey;

    if(StringUtils.isBlank(sessionKey.getStreamId())) {
      SymphonyAiMessageListener messageListener = new SymphonyAiMessageListener() {
        @Override
        public void onMessage(SymMessage symMessage) {
          if(symMessage.getFromUserId().toString().equals(sessionKey.getUid())) {
            SymphonyAiMessage symphonyAiMessage = new SymphonyAiMessage(symMessage);
            onAiMessage(sessionKey, symphonyAiMessage);
          }
        }
      };
      messageListener.setAiSessionKey(aiSessionKey);
      aiSessionContext.setSymphonyAiMessageListener(messageListener);

      symphonyClient.getMessageService().addMessageListener(messageListener);
    } else {
      SymphonyAiChatListener chatListener = new SymphonyAiChatListener() {
        @Override
        public void onChatMessage(SymMessage symMessage) {
          if(symMessage.getFromUserId().toString().equals(sessionKey.getUid())) {
            SymphonyAiMessage symphonyAiMessage = new SymphonyAiMessage(symMessage);
            onAiMessage(sessionKey, symphonyAiMessage);
          }
        }
      };
      chatListener.setAiSessionKey(aiSessionKey);
      aiSessionContext.setSymphonyAiChatListener(chatListener);

      symphonyClient.getChatService().getChatByStream(sessionKey.getStreamId()).addListener(chatListener);
    }

    return aiSessionContext;
  }
}
