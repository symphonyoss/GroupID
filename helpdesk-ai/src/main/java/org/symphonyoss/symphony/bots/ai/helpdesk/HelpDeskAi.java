package org.symphonyoss.symphony.bots.ai.helpdesk;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.helpdesk.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.ai.helpdesk.message.MessageProducer;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAi;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiEventListenerImpl;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.ai.model.SymphonyAiSessionKey;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;

/**
 * An extension of the Symphony AI, that supports help desk functions.
 * <p>
 * Created by nick.tarsillo on 9/28/17.
 */
public class HelpDeskAi extends SymphonyAi {

  private HelpDeskAiSession helpDeskAiSession;

  public HelpDeskAi(HelpDeskAiSession helpDeskAiSession) {
    super(helpDeskAiSession.getSymphonyClient());
    this.helpDeskAiSession = helpDeskAiSession;
  }

  /**
   * Initializes the HelpDeskAi based in configurations present in the HelpDeskAiConfig and
   * HelpDeskAiSession
   */
  public void init() {
    AiCommandInterpreter aiCommandInterpreter = new SymphonyAiCommandInterpreter(
        helpDeskAiSession.getSymphonyClient().getLocalUser());

    SymphonyClient symphonyClient = helpDeskAiSession.getSymphonyClient();

    MembershipClient membershipClient = helpDeskAiSession.getMembershipClient();

    MessageProducer messageProducer = new MessageProducer(membershipClient, symphonyClient);

    this.aiResponder = new HelpDeskAiResponder(symphonyClient, messageProducer);
    this.aiEventListener = new SymphonyAiEventListenerImpl(aiCommandInterpreter, aiResponder);
  }

  /**
   * Creates a new AiSessionContext
   * @param sessionKey The key for the session
   * @return the HelpDeskAiSessionContext created
   */
  @Override
  public AiSessionContext newAiSessionContext(SymphonyAiSessionKey sessionKey) {
    Long userId = sessionKey.getUid();
    String streamId = sessionKey.getStreamId();

    HelpDeskAiSessionContext sessionContext = new HelpDeskAiSessionContext(sessionKey, helpDeskAiSession);

    if (isAgentUser(userId)) {

      if (isTicketRoom(streamId)) {
        // Ticket room
        sessionContext.setSessionType(HelpDeskAiSessionContext.SessionType.AGENT_SERVICE);
      } else {
        // Agent room
        sessionContext.setSessionType(HelpDeskAiSessionContext.SessionType.AGENT);
      }

    } else {
      // Client room
      sessionContext.setSessionType(HelpDeskAiSessionContext.SessionType.CLIENT);
    }

    return sessionContext;
  }

  private boolean isAgentUser(Long userId) {
    Membership membership = helpDeskAiSession.getMembershipClient().getMembership(userId);
    return membership != null && MembershipClient.MembershipType.AGENT.getType()
        .equals(membership.getType());
  }

  private boolean isTicketRoom(String streamId) {
    Ticket ticket = helpDeskAiSession.getTicketClient().getTicketByServiceStreamId(streamId);
    return ticket != null;
  }

}
