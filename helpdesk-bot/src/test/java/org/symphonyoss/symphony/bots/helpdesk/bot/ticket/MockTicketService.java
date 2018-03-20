package org.symphonyoss.symphony.bots.helpdesk.bot.ticket;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.TicketResponse;
import org.symphonyoss.symphony.bots.helpdesk.bot.util.ValidateMembershipService;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.validation.SymphonyValidationUtil;
import org.symphonyoss.symphony.clients.model.SymUser;

/**
 * Created by rsanchez on 19/12/17.
 */
public class MockTicketService extends TicketService {

  private static final String SUCCESS_RESPONSE = "Success";

  public MockTicketService(SymphonyValidationUtil symphonyValidationUtil,
      SymphonyClient symphonyClient, HelpDeskBotConfig helpDeskBotConfig, TicketClient ticketClient,
      ValidateMembershipService validateMembershipService) {
    super(symphonyValidationUtil, symphonyClient, helpDeskBotConfig, ticketClient,
        validateMembershipService);
  }

  @Override
  protected TicketResponse execute(Ticket ticket, SymUser agentUser) {
    return buildResponse(ticket, agentUser, SUCCESS_RESPONSE);
  }

}
