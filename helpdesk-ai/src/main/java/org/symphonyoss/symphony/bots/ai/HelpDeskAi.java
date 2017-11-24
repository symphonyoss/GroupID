package org.symphonyoss.symphony.bots.ai;

import org.symphonyoss.symphony.bots.ai.impl.SymphonyAi;
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
    super(helpDeskAiSession.getSymphonyClient(), helpDeskAiSession.getHelpDeskAiConfig().isSuggestCommands());

    this.helpDeskAiSession = helpDeskAiSession;
  }

  @Override
  public AiSessionContext newAiSessionContext(AiSessionKey aiSessionKey) {
    HelpDeskAiSessionContext sessionContext = new HelpDeskAiSessionContext();
    sessionContext.setHelpDeskAiSession(helpDeskAiSession);
    sessionContext.setAiSessionKey(aiSessionKey);
    sessionContext.setGroupId(helpDeskAiSession.getHelpDeskAiConfig().getGroupId());

    SymphonyAiSessionKey sessionKey = (SymphonyAiSessionKey) aiSessionKey;
    Membership membership =
        helpDeskAiSession.getMembershipClient().getMembership(sessionKey.getUid());
    if (membership.getType().equals(MembershipClient.MembershipType.AGENT.getType())) {
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
