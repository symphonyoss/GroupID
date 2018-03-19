package org.symphonyoss.symphony.bots.ai.helpdesk;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.helpdesk.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.ai.impl.AiEventListenerImpl;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAi;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiSessionKey;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;

/**
 * Created by nick.tarsillo on 9/28/17.
 * An extension of the Symphony Ai, that supports help desk functions.
 */
public class HelpDeskAi extends SymphonyAi {

  private HelpDeskAiSession helpDeskAiSession;

  public HelpDeskAi(HelpDeskAiSession helpDeskAiSession) {
    super(helpDeskAiSession.getSymphonyClient(),
        helpDeskAiSession.getHelpDeskAiConfig().isSuggestCommands());
    this.helpDeskAiSession = helpDeskAiSession;
  }

  /**
   * Initializes the HelpDeskAi based in configurations present in the HelpDeskAiConfig and
   * HelpDeskAiSession
   */
  public void init() {
    AiCommandInterpreter aiCommandInterpreter = new SymphonyAiCommandInterpreter(
        helpDeskAiSession.getSymphonyClient().getLocalUser());

    boolean suggestCommands = helpDeskAiSession.getHelpDeskAiConfig().isSuggestCommands();
    SymphonyClient symphonyClient = helpDeskAiSession.getSymphonyClient();

    MembershipClient membershipClient = helpDeskAiSession.getMembershipClient();

    this.aiResponder =
        new HelpDeskAiResponder(symphonyClient, membershipClient);
    this.aiEventListener =
        new AiEventListenerImpl(aiCommandInterpreter, aiResponder, suggestCommands);
  }

  /**
   * Creates a new AiSessionContext
   * @param aiSessionKey The key for the session
   * @return the HelpDeskAiSessionContext created
   */
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
      Ticket ticket =
          helpDeskAiSession.getTicketClient().getTicketByServiceStreamId(sessionKey.getStreamId());
      if (ticket != null) {
        sessionContext.setSessionType(HelpDeskAiSessionContext.SessionType.AGENT_SERVICE);
      } else {
        sessionContext.setSessionType(HelpDeskAiSessionContext.SessionType.AGENT);
      }
    } else {
      sessionContext.setSessionType(HelpDeskAiSessionContext.SessionType.CLIENT);
    }

    return sessionContext;
  }

}
