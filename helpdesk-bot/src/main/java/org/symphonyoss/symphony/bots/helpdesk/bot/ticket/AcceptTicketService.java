package org.symphonyoss.symphony.bots.helpdesk.bot.ticket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.symphony.bots.ai.AiResponseIdentifier;
import org.symphonyoss.symphony.bots.ai.HelpDeskAi;
import org.symphonyoss.symphony.bots.ai.impl.AiResponseIdentifierImpl;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.TicketResponse;
import org.symphonyoss.symphony.bots.helpdesk.bot.util.ValidateMembershipService;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.message.AcceptMessageBuilder;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.model.UserInfo;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.validation.SymphonyValidationUtil;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;

/**
 * Created by rsanchez on 19/12/17.
 */
@Service
public class AcceptTicketService extends TicketService {

  private static final Logger LOG = LoggerFactory.getLogger(AcceptTicketService.class);

  private static final String TICKET_WAS_CLAIMED = "Ticket was claimed.";

  private static final String TICKET_SUCCESS_RESPONSE = "Ticket accepted.";

  private final HelpDeskAi helpDeskAi;

  public AcceptTicketService(SymphonyValidationUtil symphonyValidationUtil,
      SymphonyClient symphonyClient, HelpDeskBotConfig helpDeskBotConfig, TicketClient ticketClient,
      HelpDeskAi helpDeskAi, ValidateMembershipService validateMembershipService) {
    super(symphonyValidationUtil, symphonyClient, helpDeskBotConfig, ticketClient,
        validateMembershipService);
    this.helpDeskAi = helpDeskAi;
  }

  /**
   * Accepts the ticket. This method should update the group memberships if required, add this
   * agent to the service stream, send the accept message to the client, and update the ticket
   * state to UNRESOLVED.
   * @param ticket Ticket info
   * @param agent User agent
   * @return Ticket API response
   */
  @Override
  protected TicketResponse execute(Ticket ticket, SymUser agent) {
    if (TicketClient.TicketStateType.UNSERVICED.getState().equals(ticket.getState())) {

      try {
        updateMembership(agent.getId());

        addAgentToServiceStream(ticket, agent.getId());

        sendAcceptMessageToClient(ticket, agent.getId());

        updateTicketState(ticket, agent, TicketClient.TicketStateType.UNRESOLVED);

        //sendAcceptMessageToAgents(ticket, agent);

        return buildResponse(ticket, agent, TICKET_SUCCESS_RESPONSE);
      } catch (SymException e) {
        LOG.error("Could not accept ticket: ", e);
        throw new InternalServerErrorException();
      }

    } else {
      throw new BadRequestException(TICKET_WAS_CLAIMED);
    }
  }

  /**
   * Sends accept message to client stream.
   * @param ticket Ticket info
   * @param agentId Agent user id
   */
  private void sendAcceptMessageToClient(Ticket ticket, Long agentId) {
    AiSessionKey sessionKey = helpDeskAi.getSessionKey(agentId, ticket.getServiceStreamId());
    SymphonyAiMessage symphonyAiMessage =
        new SymphonyAiMessage(helpDeskBotConfig.getAcceptTicketClientSuccessResponse());

    Set<AiResponseIdentifier> responseIdentifierSet = new HashSet<>();
    responseIdentifierSet.add(new AiResponseIdentifierImpl(ticket.getClientStreamId()));

    helpDeskAi.sendMessage(symphonyAiMessage, responseIdentifierSet, sessionKey);
  }

  /**
   * Update ticket status.
   * @param ticket Ticket info
   * @param agent Agent user
   * @param state Ticket state
   */
  private void updateTicketState(Ticket ticket, SymUser agent, TicketClient.TicketStateType state) {
    UserInfo agentUser = new UserInfo();
    agentUser.setUserId(agent.getId());
    agentUser.setDisplayName(agent.getDisplayName());
    ticket.setAgent(agentUser);

    ticket.setState(state.getState());
    ticketClient.updateTicket(ticket);
  }

  private void sendAcceptMessageToAgents(Ticket ticket, SymUser agent) {
    SymStream stream = new SymStream();
    stream.setStreamId(helpDeskBotConfig.getAgentStreamId());

    SymMessage acceptMessage = new AcceptMessageBuilder()
        .agent(agent.getDisplayName())
        .streamId(stream.getStreamId())
        .ticketId(ticket.getId())
        .build();

    try {
      symphonyClient.getMessageService().sendMessage(stream, acceptMessage);
    } catch (MessagesException e) {
      LOG.error("Fail to send accepted ticket (claimed) message", e);
      throw new InternalServerErrorException(e);
    }
  }
}
