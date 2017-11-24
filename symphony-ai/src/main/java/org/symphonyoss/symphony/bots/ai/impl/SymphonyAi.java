package org.symphonyoss.symphony.bots.ai.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Created by nick.tarsillo on 8/20/17.
 */
public class SymphonyAi extends AiImpl {
  private static final Logger LOG = LoggerFactory.getLogger(SymphonyAi.class);

  private SymphonyClient symphonyClient;

  public SymphonyAi(SymphonyClient symphonyClient, boolean suggestCommand) {
    super(suggestCommand);
    AiCommandInterpreter aiCommandInterpreter = new SymphonyAiCommandInterpreter(symphonyClient.getLocalUser());
    aiResponder = new SymphonyAiResponder(symphonyClient.getMessagesClient());
    aiEventListener = new AiEventListenerImpl(aiCommandInterpreter, aiResponder, suggestCommand);

    this.symphonyClient = symphonyClient;
  }

  public AiSessionKey getSessionKey(Long userId, String streamId) {
    return new SymphonyAiSessionKey(userId + ":" + streamId, userId, streamId);
  }

  @Override
  public AiSessionContext newAiSessionContext(AiSessionKey aiSessionKey) {
    SymphonyAiSessionContext aiSessionContext = new SymphonyAiSessionContext();
    SymphonyAiSessionKey sessionKey = (SymphonyAiSessionKey) aiSessionKey;

    SymphonyAiMessageListener messageListener;
    if(StringUtils.isBlank(sessionKey.getStreamId())) {
      messageListener = new SymphonyAiMessageListener() {
        @Override
        public void onMessage(SymMessage symMessage) {
          if(symMessage.getFromUserId().equals(sessionKey.getUid())) {
            SymphonyAiMessage symphonyAiMessage = new SymphonyAiMessage(symMessage);
            onAiMessage(sessionKey, symphonyAiMessage);
          }
        }
      };
    } else {
      messageListener = new SymphonyAiMessageListener() {
        @Override
        public void onMessage(SymMessage symMessage) {
          if(symMessage.getFromUserId().equals(sessionKey.getUid())
              && symMessage.getStreamId().equals(sessionKey.getStreamId())) {
            SymphonyAiMessage symphonyAiMessage = new SymphonyAiMessage(symMessage);
            onAiMessage(sessionKey, symphonyAiMessage);
          }
        }
      };
    }
    messageListener.setAiSessionKey(aiSessionKey);
    aiSessionContext.setSymphonyAiMessageListener(messageListener);

    symphonyClient.getMessageService().addMessageListener(messageListener);

    return aiSessionContext;
  }
}
