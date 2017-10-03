package com.symphony.bots.helpdesk.model.ai;

import com.symphony.bots.ai.impl.AiSymphonyChatListener;
import com.symphony.bots.ai.impl.SymphonyAi;
import com.symphony.bots.ai.impl.SymphonyAiMessage;
import com.symphony.bots.ai.model.AiSessionKey;

import org.symphonyoss.symphony.clients.MessagesClient;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Created by nick.tarsillo on 9/28/17.
 * An extension of the Symphony Ai, that supports help desk functions.
 */
public class HelpDeskAi extends SymphonyAi {

  public HelpDeskAi(MessagesClient messagesClient,
      boolean suggestCommand, String sessionContext) {
    super(messagesClient, suggestCommand, sessionContext);
    aiSessionContextManager = new HelpDeskSessionContextManager(sessionContext);
  }

  /**
   * Create a new ai session.
   * This method will use a help desk session key, instead of the generic one.
   * This allows the AI to differentiate between help desk session types. (CLIENT, AGENT, AGENT_SERVICE)
   * @param userId the id of the user to create the session for.
   * @param streamId the stream the user is in.
   * @param sessionType the help desk session type.
   * @return a chat listener that can be registered with the SJC so the AI can receive messages.
   */
  public AiSymphonyChatListener createNewHelpDeskSession(Long userId, String streamId, HelpDeskAiSessionKey.SessionType sessionType) {
    HelpDeskAiSessionKey aiSessionKey = new HelpDeskAiSessionKey(userId + ":" + streamId, sessionType);
    AiSymphonyChatListener chatListener = new AiSymphonyChatListener() {
      @Override
      public void onChatMessage(SymMessage message) {
        AiSessionKey key = aiSessionKey;
        if(message.getFromUserId().equals(userId) && message.getStreamId().equals(streamId)) {
          onAiMessage(key, new SymphonyAiMessage(message));
        }
      }
    };

    chatListener.setAiSessionKey(aiSessionKey);

    return chatListener;
  }
}
