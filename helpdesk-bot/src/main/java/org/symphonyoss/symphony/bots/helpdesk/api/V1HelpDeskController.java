package org.symphonyoss.symphony.bots.helpdesk.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.model.HealthcheckResponse;
import org.symphonyoss.symphony.bots.helpdesk.model.health.HealthCheckFailedException;
import org.symphonyoss.symphony.bots.helpdesk.model.health.HealthcheckHelper;
import org.symphonyoss.symphony.bots.helpdesk.model.session.HelpDeskBotSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.bots.helpdesk.model.session.HelpDeskBotSessionManager;
import org.symphonyoss.symphony.bots.helpdesk.model.MakerCheckerMessageDetail;
import org.symphonyoss.symphony.bots.helpdesk.model.SuccessResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.client.TicketClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.clients.model.SymMessage;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;

/**
 * Created by nick.tarsillo on 9/25/17.
 */
@RestController
public class V1HelpDeskController extends V1ApiController {
  private static final Logger LOG = LoggerFactory.getLogger(V1HelpDeskController.class);
  private static final String HELPDESKBOT_NOT_FOUND = "Help desk bot not found.";

  @Autowired
  private TicketClient ticketClient;

  @Override
  public SuccessResponse acceptTicket(String ticketId, String agentId) {
    Ticket ticket = ticketClient.getTicket(ticketId);
    HelpDeskBotSessionManager sessionManager = HelpDeskBotSessionManager.getDefaultSessionManager();
    HelpDeskBotSession helpDeskBotSession = sessionManager.getSession(ticket.getGroupId());

    try {
      helpDeskBotSession.getSymphonyClient().getRoomMembershipClient().addMemberToRoom(
          ticket.getServiceStreamId(), Long.parseLong(agentId));
      ticket.setState(TicketClient.TicketStateType.UNRESOLVED.getState());
      helpDeskBotSession.getTicketClient().updateTicket(ticket);

      Chat clientChat = helpDeskBotSession.getSymphonyClient().getChatService().getChatByStream(ticket.getClientStreamId());
      Chat agentChat = helpDeskBotSession.getSymphonyClient().getChatService().getChatByStream(ticket.getServiceStreamId());

      SymMessage symMessage = new SymMessage();
      symMessage.setMessage(helpDeskBotSession.getHelpDeskBotConfig().getAcceptTicketClientSuccessResponse());
      helpDeskBotSession.getSymphonyClient().getMessageService().sendMessage(clientChat, symMessage);

      symMessage.setMessage(helpDeskBotSession.getHelpDeskBotConfig().getAcceptTicketAgentSuccessResponse());
      helpDeskBotSession.getSymphonyClient().getMessageService().sendMessage(agentChat, symMessage);

      return new SuccessResponse();
    } catch (SymException e) {
      LOG.error("Could not accept ticket: ", e);
      throw new InternalServerErrorException();
    }
  }

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

  @Override
  public SuccessResponse acceptMakerCheckerMessage(MakerCheckerMessageDetail detail) {
    MakerCheckerMessage makerCheckerMessage = new MakerCheckerMessage(detail.getStreamId(),
        detail.getProxyToStreamIds(), detail.getTimeStamp(), detail.getMessageId(), detail.getGroupId());
    HelpDeskBotSessionManager sessionManager = HelpDeskBotSessionManager.getDefaultSessionManager();
    HelpDeskBotSession helpDeskBotSession = sessionManager.getSession(makerCheckerMessage.getGroupId());

    helpDeskBotSession.getAgentMakerCheckerService().acceptMakerCheckerMessage(makerCheckerMessage);
    return new SuccessResponse();
  }
}
