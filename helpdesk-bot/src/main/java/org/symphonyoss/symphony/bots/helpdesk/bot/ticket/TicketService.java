package org.symphonyoss.symphony.bots.helpdesk.bot.ticket;

import static org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient
    .MembershipType.AGENT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.TicketResponse;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.User;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.validation.SymphonyValidationUtil;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.MembershipList;

import javax.ws.rs.BadRequestException;

/**
 * Created by rsanchez on 19/12/17.
 */
public abstract class TicketService {

  private static final Logger LOG = LoggerFactory.getLogger(TicketService.class);

  private static final String TICKET_NOT_FOUND = "Ticket not found.";

  private final SymphonyValidationUtil symphonyValidationUtil;

  private final MembershipClient membershipClient;

  private final SymphonyClient symphonyClient;

  protected final HelpDeskBotConfig helpDeskBotConfig;

  protected final TicketClient ticketClient;

  public TicketService(SymphonyValidationUtil symphonyValidationUtil,
      MembershipClient membershipClient, SymphonyClient symphonyClient,
      HelpDeskBotConfig helpDeskBotConfig, TicketClient ticketClient) {
    this.symphonyValidationUtil = symphonyValidationUtil;
    this.membershipClient = membershipClient;
    this.symphonyClient = symphonyClient;
    this.helpDeskBotConfig = helpDeskBotConfig;
    this.ticketClient = ticketClient;
  }

  /**
   * Validates the input data and starts the process.
   *
   * @param ticketId the ticket id to accept
   * @param agentId the user id of the agent
   * @return the ticket responses
   */
  public TicketResponse execute(String ticketId, Long agentId) {
    Ticket ticket = ticketClient.getTicket(ticketId);

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
    Membership membership = membershipClient.getMembership(agentId);

    if (membership == null) {
      validateAgentStreamMemberships(agentId);

      membershipClient.newMembership(agentId, AGENT);

      LOG.info("Created new agent membership for userid: " + agentId);
    } else if (!AGENT.getType().equals(membership.getType())) {
      validateAgentStreamMemberships(agentId);

      membership.setType(AGENT.getType());
      membershipClient.updateMembership(membership);

      LOG.info("Updated agent membership for userid: " + agentId);
    }
  }

  /**
   * Validates if the user is a member of the agent stream.
   *
   * @param agentId User agent id
   * @throws SymException
   */
  private void validateAgentStreamMemberships(Long agentId) throws SymException {
    MembershipList agentMemberships = symphonyClient.getRoomMembershipClient()
        .getRoomMembership(helpDeskBotConfig.getAgentStreamId());

    long count = agentMemberships.stream()
        .filter(member -> member.getId().equals(agentId))
        .count();

    if (count == 0) {
      throw new BadRequestException("User is not an agent");
    }
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
