package org.symphonyoss.symphony.bots.helpdesk.bot.ticket;

import org.apache.commons.lang3.StringUtils;
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
import org.symphonyoss.symphony.bots.helpdesk.service.client.StringUtil;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.model.UserInfo;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.validation.SymphonyValidationUtil;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
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
        validateMembershipService, helpDeskAi);
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
        Long agentId = agent.getId();
        updateMembership(agentId);
        addAgentToServiceStream(ticket, agentId);
        sendTicketHistory(ticket, agentId);
        sendAcceptMessageToClient(ticket, agentId);
        sendAcceptMessageToAgents(ticket, agent, TicketClient.TicketStateType.UNRESOLVED);
        updateTicketState(ticket, agent, TicketClient.TicketStateType.UNRESOLVED);

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

  private void sendAcceptMessageToAgents(Ticket ticket, SymUser agent, TicketClient.TicketStateType ticketState) {
    SymStream stream = new SymStream();
    stream.setStreamId(helpDeskBotConfig.getAgentStreamId());

    SymMessage acceptMessage = getAcceptMessage(ticket, agent, ticketState);

    try {
      symphonyClient.getMessagesClient().sendMessage(stream, acceptMessage);
    } catch (MessagesException e) {
      LOG.error("Fail to send accepted ticket (claimed) message", e);
      throw new InternalServerErrorException(e);
    }
  }

  private SymMessage getAcceptMessage(Ticket ticket, SymUser agent, TicketClient.TicketStateType ticketState) {
    UserInfo userInfo = new UserInfo();
    userInfo.setUserId(agent.getId());
    userInfo.setDisplayName(agent.getDisplayName());

    SymMessage acceptMessage = new AcceptMessageBuilder()
        .agent(userInfo)
        .ticketState(ticketState.getState())
        .ticketId(ticket.getId())
        .build();

    return acceptMessage;
  }

  protected void sendTicketHistory(Ticket ticket, Long agentId) {
    if (Boolean.FALSE.equals(ticket.getShowHistory())) {
      SymStream serviceStream = new SymStream();
      serviceStream.setStreamId(ticket.getServiceStreamId());

      try {
        List<SymMessage> messages = symphonyClientUtil.getSymMessages(serviceStream,
            ticket.getQuestionTimestamp(), 100);

        Long firstTimeStamp = Long.valueOf(messages.get(0).getTimestamp());

        messages.stream()
            .filter(symMessage -> !Long.valueOf(symMessage.getTimestamp()).equals(ticket.getQuestionTimestamp()))
            .sorted(Comparator.comparing(SymMessage::getTimestamp))
            .forEach(symMessage -> sendMessage(symMessage, agentId));

        ticket.setQuestionTimestamp(firstTimeStamp);
        ticketClient.updateTicket(ticket);
      } catch (MessagesException e) {
        LOG.error("Could not send message to service room: ", e);
        throw new InternalServerErrorException();
      }
    }
  }

  private void sendMessage(SymMessage symMessage, Long agentId) {
    AiSessionKey sessionKey = helpDeskAi.getSessionKey(agentId, symMessage.getStreamId());
    SymphonyAiMessage symphonyAiMessage = new SymphonyAiMessage(symMessage);

    Set<AiResponseIdentifier> responseIdentifierSet = new HashSet<>();
    responseIdentifierSet.add(new AiResponseIdentifierImpl(symMessage.getStreamId()));

    helpDeskAi.sendMessage(symphonyAiMessage, responseIdentifierSet, sessionKey);
  }
}
