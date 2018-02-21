package org.symphonyoss.symphony.bots.ai.impl;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.services.MessageListener;
import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Main entry point for the AI messages. This class works as both session context manager and
 * message listener, managing all messages sent to this AI.
 * <p>
 * Created by nick.tarsillo on 8/20/17.
 */
public class SymphonyAi extends AiImpl implements MessageListener {

  public SymphonyAi(SymphonyClient symphonyClient, boolean suggestCommand) {
    super(suggestCommand);
    AiCommandInterpreter aiCommandInterpreter =
        new SymphonyAiCommandInterpreter(symphonyClient.getLocalUser());
    aiResponder = new SymphonyAiResponder(symphonyClient.getMessagesClient());
    aiEventListener = new AiEventListenerImpl(aiCommandInterpreter, aiResponder, suggestCommand);
  }

  @Override
  public void onMessage(SymMessage symMessage) {
    AiSessionKey aiSessionKey = getSessionKey(symMessage.getFromUserId(), symMessage.getStreamId());
    onAiMessage(aiSessionKey, new SymphonyAiMessage(symMessage));
  }

  /**
   * Retrieve a new {@link AiSessionKey AI session key} using the userId and streamId as the key
   * @param userId user id used to build the session key
   * @param streamId stream id used to build the session key
   * @return a new instance of a {@link AiSessionKey AI session key}
   */
  public AiSessionKey getSessionKey(Long userId, String streamId) {
    return new SymphonyAiSessionKey(userId + ":" + streamId, userId, streamId);
  }

}
