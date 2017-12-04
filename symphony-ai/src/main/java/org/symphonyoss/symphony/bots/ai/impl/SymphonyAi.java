package org.symphonyoss.symphony.bots.ai.impl;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.services.MessageListener;
import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Created by nick.tarsillo on 8/20/17.
 */
public class SymphonyAi extends AiImpl implements MessageListener {

  public SymphonyAi(SymphonyClient symphonyClient, boolean suggestCommand) {
    super(suggestCommand);
    AiCommandInterpreter aiCommandInterpreter = new SymphonyAiCommandInterpreter(symphonyClient.getLocalUser());
    aiResponder = new SymphonyAiResponder(symphonyClient.getMessagesClient());
    aiEventListener = new AiEventListenerImpl(aiCommandInterpreter, aiResponder, suggestCommand);
  }

  @Override
  public void onMessage(SymMessage symMessage) {
    AiSessionKey aiSessionKey = getSessionKey(symMessage.getFromUserId(), symMessage.getStreamId());
    onAiMessage(aiSessionKey, new SymphonyAiMessage(symMessage));
  }

  public AiSessionKey getSessionKey(Long userId, String streamId) {
    return new SymphonyAiSessionKey(userId + ":" + streamId, userId, streamId);
  }

}
