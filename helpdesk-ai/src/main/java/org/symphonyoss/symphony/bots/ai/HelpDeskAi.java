package org.symphonyoss.symphony.bots.ai;

import static org.symphonyoss.symphony.bots.ai.HelpDeskAiSessionContext.SessionType.AGENT;
import static org.symphonyoss.symphony.bots.ai.HelpDeskAiSessionContext.SessionType.AGENT_SERVICE;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.ai.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.ai.impl.AiCommandInterpreterImpl;
import org.symphonyoss.symphony.bots.ai.impl.AiEventListenerImpl;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAi;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiSessionKey;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.clients.MessagesClient;
import org.symphonyoss.symphony.clients.UsersClient;

/**
 * Created by nick.tarsillo on 9/28/17.
 * An extension of the Symphony Ai, that supports help desk functions.
 */
public class HelpDeskAi extends SymphonyAi {

  private HelpDeskAiSession helpDeskAiSession;

  public HelpDeskAi(HelpDeskAiSession helpDeskAiSession) {
    super(helpDeskAiSession.getSymphonyClient(), helpDeskAiSession.getHelpDeskAiConfig().isSuggestCommands());

    this.helpDeskAiSession = helpDeskAiSession;

    AiCommandInterpreter aiCommandInterpreter = new AiCommandInterpreterImpl();

    boolean suggestCommands = helpDeskAiSession.getHelpDeskAiConfig().isSuggestCommands();
    SymphonyClient symphonyClient = helpDeskAiSession.getSymphonyClient();

    MessagesClient messagesClient = symphonyClient.getMessagesClient();
    UsersClient usersClient = symphonyClient.getUsersClient();
    MembershipClient membershipClient = helpDeskAiSession.getMembershipClient();

    this.aiResponder = new HelpDeskAiResponder(messagesClient, membershipClient, usersClient);
    this.aiEventListener = new AiEventListenerImpl(aiCommandInterpreter, aiResponder, suggestCommands);
  }

  public void onAiMessage(AiSessionKey aiSessionKey, AiMessage message) {
    HelpDeskAiSessionContext sessionContext = (HelpDeskAiSessionContext) getSessionContext(aiSessionKey);

    if (!AGENT_SERVICE.equals(sessionContext.getSessionType())) {
      super.onAiMessage(aiSessionKey, message);
    }
  }

  @Override
  public AiSessionContext newAiSessionContext(AiSessionKey aiSessionKey) {
    HelpDeskAiConfig config = helpDeskAiSession.getHelpDeskAiConfig();

    HelpDeskAiSessionContext sessionContext = new HelpDeskAiSessionContext();
    sessionContext.setHelpDeskAiSession(helpDeskAiSession);
    sessionContext.setAiSessionKey(aiSessionKey);
    sessionContext.setGroupId(config.getGroupId());

    SymphonyAiSessionKey sessionKey = (SymphonyAiSessionKey) aiSessionKey;
    Membership membership =
        helpDeskAiSession.getMembershipClient().getMembership(sessionKey.getUid());

    if ((membership != null) && (MembershipClient.MembershipType.AGENT.getType()
        .equals(membership.getType()))) {
      if (membership.getId().equals(config.getBotUserId())) {
        sessionContext.setSessionType(AGENT_SERVICE);
      } else {
        sessionContext.setSessionType(AGENT);
      }
    } else {
      sessionContext.setSessionType(HelpDeskAiSessionContext.SessionType.CLIENT);
    }

    return sessionContext;
  }

  public void updateBotUserId(Long userId) {
    helpDeskAiSession.getHelpDeskAiConfig().setBotUserId(userId);
  }


}
