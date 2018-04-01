package org.symphonyoss.symphony.bots.ai.helpdesk;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.helpdesk.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.ai.helpdesk.conversation.IdleTimerManager;
import org.symphonyoss.symphony.bots.ai.helpdesk.menu.AgentCommandMenu;
import org.symphonyoss.symphony.bots.ai.helpdesk.menu.ClientCommandMenu;
import org.symphonyoss.symphony.bots.ai.helpdesk.menu.ServiceCommandMenu;
import org.symphonyoss.symphony.bots.ai.helpdesk.message.MessageProducer;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAi;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiEventListenerImpl;
import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;

/**
 * An extension of the Symphony AI, that supports help desk functions.
 * <p>
 * Created by nick.tarsillo on 9/28/17.
 */
public class HelpDeskAi extends SymphonyAi {

  private final HelpDeskAiConfig aiConfig;

  private final SymphonyClient symphonyClient;

  private final TicketClient ticketClient;

  private final MembershipClient membershipClient;

  private final IdleTimerManager timerManager;

  public HelpDeskAi(HelpDeskAiConfig aiConfig, SymphonyClient symphonyClient,
      TicketClient ticketClient, MembershipClient membershipClient, IdleTimerManager timerManager) {
    super(symphonyClient);
    this.aiConfig = aiConfig;
    this.symphonyClient = symphonyClient;
    this.ticketClient = ticketClient;
    this.membershipClient = membershipClient;
    this.timerManager = timerManager;
  }

  /**
   * Initializes the HelpDeskAi
   */
  public void init() {
    AiCommandInterpreter aiCommandInterpreter = new SymphonyAiCommandInterpreter(symphonyClient);

    MessageProducer messageProducer = new MessageProducer(membershipClient, symphonyClient);

    this.aiResponder = new HelpDeskAiResponder(messageProducer);
    this.aiEventListener = new SymphonyAiEventListenerImpl(aiCommandInterpreter, aiResponder);
  }

  @Override
  public AiCommandMenu newAiCommandMenu(AiSessionKey sessionKey) {
    Long userId = sessionKey.getUid();
    String streamId = sessionKey.getStreamId();

    if (isAgentUser(userId)) {

      if (isTicketRoom(streamId)) {
        // Ticket room
        return new ServiceCommandMenu(aiConfig, ticketClient, symphonyClient, timerManager);
      } else {
        // Agent room
        return new AgentCommandMenu();
      }

    } else {
      // Client room
      return new ClientCommandMenu();
    }
  }

  private boolean isAgentUser(Long userId) {
    Membership membership = membershipClient.getMembership(userId);
    return membership != null && MembershipClient.MembershipType.AGENT.getType()
        .equals(membership.getType());
  }

  private boolean isTicketRoom(String streamId) {
    Ticket ticket = ticketClient.getTicketByServiceStreamId(streamId);
    return ticket != null;
  }

}
