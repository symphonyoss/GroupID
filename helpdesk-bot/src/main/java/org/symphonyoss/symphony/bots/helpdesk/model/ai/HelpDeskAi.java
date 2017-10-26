package org.symphonyoss.symphony.bots.helpdesk.model.ai;

import org.symphonyoss.symphony.bots.ai.impl.AiSymphonyChatListener;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAi;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;
import org.symphonyoss.symphony.bots.helpdesk.model.HelpDeskBotSession;

import org.symphonyoss.symphony.clients.MessagesClient;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Created by nick.tarsillo on 9/28/17.
 * An extension of the Symphony Ai, that supports help desk functions.
 */
public class HelpDeskAi extends SymphonyAi {

  public HelpDeskAi(MessagesClient messagesClient,
      boolean suggestCommand, String sessionContext, HelpDeskBotSession helpDeskBotSession) {
    super(messagesClient, suggestCommand, sessionContext);
    aiSessionContextManager = new HelpDeskSessionContextManager(sessionContext, helpDeskBotSession);
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
  public AiSymphonyChatListener createNewHelpDeskSession(String userId, String streamId, String groupId, HelpDeskAiSessionKey.SessionType sessionType) {
    HelpDeskAiSessionKey aiSessionKey = new HelpDeskAiSessionKey(userId + ":" + streamId, groupId, sessionType);
    aiSessionKey.setStreamId(streamId);
    aiSessionKey.setUid(userId);
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
