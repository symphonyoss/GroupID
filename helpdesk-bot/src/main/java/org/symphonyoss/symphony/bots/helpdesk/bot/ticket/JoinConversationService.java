package org.symphonyoss.symphony.bots.helpdesk.bot.ticket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.TicketResponse;
import org.symphonyoss.symphony.bots.helpdesk.bot.util.ValidateMembershipService;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.validation.SymphonyValidationUtil;
import org.symphonyoss.symphony.clients.model.SymUser;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;

/**
 * Created by rsanchez on 19/12/17.
 */
@Service
public class JoinConversationService extends TicketService {

  private static final Logger LOG = LoggerFactory.getLogger(JoinConversationService.class);

  private static final String TICKET_WAS_NOT_CLAIMED = "Ticket wasn't claimed.";

  private static final String TICKET_SUCCESS_RESPONSE = "User joined the conversation.";

  public JoinConversationService(SymphonyValidationUtil symphonyValidationUtil,
      SymphonyClient symphonyClient, HelpDeskBotConfig helpDeskBotConfig, TicketClient ticketClient,
      ValidateMembershipService validateMembershipService) {
    super(symphonyValidationUtil, symphonyClient, helpDeskBotConfig, ticketClient,
        validateMembershipService);
  }

  /**
   * Joins the conversation. This method should update the group memberships if required and add
   * this agent to the service stream.
   * @param ticket Ticket info
   * @param agent User agent
   * @return Ticket API response
   */
  @Override
  protected TicketResponse execute(Ticket ticket, SymUser agent) {
    if (TicketClient.TicketStateType.UNRESOLVED.getState().equals(ticket.getState())) {

      try {
        updateMembership(agent.getId());
        addAgentToServiceStream(ticket, agent.getId());

        return buildResponse(ticket, agent, TICKET_SUCCESS_RESPONSE);
      } catch (SymException e) {
        LOG.error("Could not join the conversation: ", e);
        throw new InternalServerErrorException();
      }

    } else {
      throw new BadRequestException(TICKET_WAS_NOT_CLAIMED);
    }
  }

}
