package org.symphonyoss.symphony.bots.helpdesk.bot.ticket;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.symphony.authenticator.model.Token;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.TicketResponse;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.User;
import org.symphonyoss.symphony.bots.helpdesk.bot.util.ValidateMembershipService;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.client.SymphonyClientUtil;
import org.symphonyoss.symphony.bots.utility.validation.SymphonyValidationUtil;
import org.symphonyoss.symphony.clients.model.SymUser;

import javax.ws.rs.BadRequestException;

/**
 * Created by rsanchez on 19/12/17.
 */
public abstract class TicketService {

  private static final String TICKET_NOT_FOUND = "Ticket not found.";

  private final SymphonyValidationUtil symphonyValidationUtil;

  protected final SymphonyClient symphonyClient;

  protected final HelpDeskBotConfig helpDeskBotConfig;

  protected final TicketClient ticketClient;

  protected final SymphonyClientUtil symphonyClientUtil;

  private final ValidateMembershipService validateMembershipService;

  public TicketService(SymphonyValidationUtil symphonyValidationUtil, SymphonyClient symphonyClient,
      HelpDeskBotConfig helpDeskBotConfig, TicketClient ticketClient,
      ValidateMembershipService validateMembershipService) {
    this.symphonyValidationUtil = symphonyValidationUtil;
    this.symphonyClient = symphonyClient;
    this.helpDeskBotConfig = helpDeskBotConfig;
    this.ticketClient = ticketClient;
    this.validateMembershipService = validateMembershipService;
    this.symphonyClientUtil = new SymphonyClientUtil(symphonyClient);
  }

  /**
   * Validates the input data and starts the process.
   *
   * @param ticketId the ticket id to accept
   * @param agentId the user id of the agent
   * @return the ticket responses
   */
  public TicketResponse execute(String ticketId, Long agentId) {
    Ticket ticket = ticketClient.getTicket(symphonyClientUtil.getAuthToken(), ticketId);

    if (ticket == null) {
      throw new BadRequestException(TICKET_NOT_FOUND);
    }

    symphonyValidationUtil.validateStream(ticket.getServiceStreamId());
    symphonyValidationUtil.validateStream(ticket.getClientStreamId());

    SymUser agentUser = symphonyValidationUtil.validateUserId(agentId);
    return execute(ticket, agentUser);
  }

  /**
   * Update group memberships
   *
   * @param agentId User agent id
   * @throws SymException
   */
  protected void updateMembership(Long agentId) throws SymException {
    validateMembershipService.updateMembership(agentId);
  }

  /**
   * Add agent user to service stream.
   *
   * @param ticket Ticket info
   * @param agentId Agent user id
   * @throws SymException
   */
  protected void addAgentToServiceStream(Ticket ticket, Long agentId) throws SymException {
    symphonyClient.getRoomMembershipClient().addMemberToRoom(ticket.getServiceStreamId(), agentId);
  }

  /**
   * Builds API response.
   *
   * @param ticket Ticket info
   * @param agent Agent user
   * @return Ticket API response
   */
  protected TicketResponse buildResponse(Ticket ticket, SymUser agent, String message) {
    TicketResponse ticketResponse = new TicketResponse();
    ticketResponse.setMessage(message);
    ticketResponse.setState(ticket.getState());
    ticketResponse.setTicketId(ticket.getId());

    User user = new User();
    user.setDisplayName(agent.getDisplayName());
    user.setUserId(agent.getId());

    ticketResponse.setUser(user);

    return ticketResponse;
  }

  protected abstract TicketResponse execute(Ticket ticket, SymUser agentUser);
}
