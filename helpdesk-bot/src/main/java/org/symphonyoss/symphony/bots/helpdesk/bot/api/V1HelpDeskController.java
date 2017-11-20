package org.symphonyoss.symphony.bots.helpdesk.bot.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.symphony.bots.ai.AiResponseIdentifier;
import org.symphonyoss.symphony.bots.ai.HelpDeskAi;
import org.symphonyoss.symphony.bots.ai.impl.AiResponseIdentifierImpl;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.HealthcheckResponse;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.MakerCheckerMessageDetail;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.MakerCheckerResponse;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.TicketResponse;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.User;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.health.HealthCheckFailedException;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.health.HealthcheckHelper;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.session.HelpDeskBotSession;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.session.HelpDeskBotSessionManager;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.service.client.TicketClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.utility.validation.SymphonyValidationUtil;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;

/**
 * Created by nick.tarsillo on 9/25/17.
 */
@RestController
public class V1HelpDeskController extends V1ApiController {
  private static final Logger LOG = LoggerFactory.getLogger(V1HelpDeskController.class);
  private static final String MAKER_CHECKER_SUCCESS_RESPONSE = "Maker checker message accepted.";
  private static final String TICKET_SUCCESS_RESPONSE = "Ticket accepted.";
  private static final String TICKET_NOT_FOUND = "Ticket not found.";
  private static final String HELPDESKBOT_NOT_FOUND = "Help desk bot not found.";

  @Autowired
  private TicketClient ticketClient;
  @Autowired
  private SymphonyValidationUtil symphonyValidationUtil;

  /**
   * Accepts a ticket.
   * Sends a message to the agent denoting that the ticket has successfully been accepted.
   * Sends a message to the client, notifying them that they are now being serviced by a agent,
   *    if the client was not being serviced prior.
   * Add agent to service room.
   * Change ticket state.
   *
   * @param ticketId the ticket id to accept
   * @param agentId the user id of the agent accepting the ticket
   * @return the ticket responses
   */
  @Override
  public TicketResponse acceptTicket(String ticketId, String agentId) {
    Ticket ticket = ticketClient.getTicket(ticketId);
    if(ticket == null) {
      throw new BadRequestException(TICKET_NOT_FOUND);
    }

    symphonyValidationUtil.validateStream(ticket.getServiceStreamId());
    symphonyValidationUtil.validateStream(ticket.getClientStreamId());

    SymUser agentUser = symphonyValidationUtil.validateUserId(agentId);

    HelpDeskBotSessionManager sessionManager = HelpDeskBotSessionManager.getDefaultSessionManager();
    HelpDeskBotSession helpDeskBotSession = sessionManager.getSession(ticket.getGroupId());

    try {
      SymphonyClient symphonyClient = helpDeskBotSession.getSymphonyClient();
      HelpDeskAi helpDeskAi = helpDeskBotSession.getHelpDeskAi();
      AiSessionKey sessionKey = helpDeskAi.getSessionKey(agentId, ticket.getServiceStreamId());

      symphonyClient.getRoomMembershipClient().addMemberToRoom(ticket.getServiceStreamId(), agentUser.getId());

      SymphonyAiMessage symphonyAiMessage = new SymphonyAiMessage(
          helpDeskBotSession.getHelpDeskBotConfig().getAcceptTicketClientSuccessResponse());
      Set<AiResponseIdentifier> responseIdentifierSet = new HashSet<>();
      responseIdentifierSet.add(new AiResponseIdentifierImpl(ticket.getClientStreamId()));
      if(ticket.getState().equals(TicketClient.TicketStateType.UNSERVICED.getState())) {
        helpDeskAi.sendMessage(symphonyAiMessage, responseIdentifierSet, sessionKey);
      }

      String agentStreamId = symphonyClient.getStreamsClient().getStream(agentUser).getStreamId();
      symphonyAiMessage = new SymphonyAiMessage(
          helpDeskBotSession.getHelpDeskBotConfig().getAcceptTicketAgentSuccessResponse());
      responseIdentifierSet = new HashSet<>();
      responseIdentifierSet.add(new AiResponseIdentifierImpl(agentStreamId));
      helpDeskAi.sendMessage(symphonyAiMessage, responseIdentifierSet, sessionKey);

      ticket.setState(TicketClient.TicketStateType.UNRESOLVED.getState());
      helpDeskBotSession.getTicketClient().updateTicket(ticket);

      TicketResponse ticketResponse = new TicketResponse();
      ticketResponse.setMessage(TICKET_SUCCESS_RESPONSE);
      ticketResponse.setState(ticket.getState());
      ticketResponse.setTicketId(ticket.getId());

      User user = new User();
      user.setDisplayName(agentUser.getDisplayName());
      user.setUserId(agentId);

      ticketResponse.setUser(user);

      return ticketResponse;
    } catch (SymException e) {
      LOG.error("Could not accept ticket: ", e);
      throw new InternalServerErrorException();
    }
  }

  /**
   * Check pod connectivity.
   * Check agent connectivity.
   * @param groupId the group id of the bot to perform the health check on.
   * @return the health check response
   */
  @Override
  public HealthcheckResponse healthcheck(String groupId) {
    HelpDeskBotSessionManager sessionManager = HelpDeskBotSessionManager.getDefaultSessionManager();
    HelpDeskBotSession helpDeskBotSession = sessionManager.getSession(groupId);

    if(helpDeskBotSession == null) {
      throw new BadRequestException(HELPDESKBOT_NOT_FOUND);
    }

    String agentUrl = helpDeskBotSession.getHelpDeskBotConfig().getAgentUrl();
    String podUrl = helpDeskBotSession.getHelpDeskBotConfig().getPodUrl();
    HealthcheckHelper healthcheckHelper = new HealthcheckHelper(podUrl, agentUrl);

    HealthcheckResponse response = new HealthcheckResponse();
    try {
      healthcheckHelper.checkPodConnectivity();
      response.setPodConnectivityCheck(true);
    } catch (HealthCheckFailedException e) {
      response.setPodConnectivityCheck(false);
      response.setPodConnectivityError(e.getMessage());
    }

    try {
      healthcheckHelper.checkAgentConnectivity();
      response.setAgentConnectivityCheck(true);
    } catch (HealthCheckFailedException e) {
      response.setAgentConnectivityCheck(false);
      response.setAgentConnectivityError(e.getMessage());
    }

    return response;
  }

  /**
   * Accept a maker checker message.
   * @param detail the maker checker message detail
   * @return a maker checker message response
   */
  @Override
  public MakerCheckerResponse acceptMakerCheckerMessage(MakerCheckerMessageDetail detail) {
    MakerCheckerMessage makerCheckerMessage = new MakerCheckerMessage(detail.getStreamId(),
        detail.getProxyToStreamIds(), detail.getTimeStamp(), detail.getMessageId(), detail.getGroupId());
    HelpDeskBotSessionManager sessionManager = HelpDeskBotSessionManager.getDefaultSessionManager();
    HelpDeskBotSession helpDeskBotSession = sessionManager.getSession(makerCheckerMessage.getGroupId());

    SymUser agentUser = symphonyValidationUtil.validateUserId(detail.getUserId());

    helpDeskBotSession.getAgentMakerCheckerService().acceptMakerCheckerMessage(makerCheckerMessage);

    MakerCheckerResponse makerCheckerResponse = new MakerCheckerResponse();
    makerCheckerResponse.setMessage(MAKER_CHECKER_SUCCESS_RESPONSE);
    makerCheckerResponse.setMakerCheckerMessageDetail(detail);

    User user = new User();
    user.setDisplayName(agentUser.getDisplayName());
    user.setUserId(detail.getUserId());

    makerCheckerResponse.setUser(user);

    return makerCheckerResponse;
  }
}
