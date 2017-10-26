package org.symphonyoss.symphony.bots.helpdesk.api;

import org.symphonyoss.symphony.bots.helpdesk.api.impl.V1AbstractHelpDeskApi;
import org.symphonyoss.symphony.bots.helpdesk.model.HelpDeskBotSession;
import org.symphonyoss.symphony.bots.helpdesk.model.MakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.model.SuccessResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.TicketService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.symphony.clients.model.SymMessage;

import javax.ws.rs.InternalServerErrorException;

/**
 * Created by nick.tarsillo on 9/25/17.
 */
public class V1HelpDeskApi extends V1AbstractHelpDeskApi {
  private static final Logger LOG = LoggerFactory.getLogger(V1HelpDeskApi.class);

  private HelpDeskBotSession helpDeskBotSession;

  public V1HelpDeskApi(HelpDeskBotSession helpDeskBotSession) {
    this.helpDeskBotSession = helpDeskBotSession;
  }

  @Override
  public SuccessResponse acceptTicket(String ticketId, String agentId) {
    Ticket ticket = helpDeskBotSession.getTicketService().getTicket(ticketId);

    try {
      helpDeskBotSession.getSymphonyClient().getRoomMembershipClient().addMemberToRoom(
          ticket.getServiceStreamId(), Long.parseLong(agentId));
      ticket.setState(TicketService.TicketStateType.UNRESOLVED.getState());
      helpDeskBotSession.getTicketService().updateTicket(ticket);

      Chat clientChat = helpDeskBotSession.getSymphonyClient().getChatService().getChatByStream(ticket.getClientStreamId());
      Chat agentChat = helpDeskBotSession.getSymphonyClient().getChatService().getChatByStream(ticket.getServiceStreamId());

      SymMessage symMessage = new SymMessage();
      symMessage.setMessage(helpDeskBotSession.getHelpDeskBotConfig().getClientServiceNotificationResponse());
      symMessage.setFormat(SymMessage.Format.MESSAGEML);
      helpDeskBotSession.getSymphonyClient().getMessageService().sendMessage(clientChat, symMessage);

      symMessage.setMessage(helpDeskBotSession.getHelpDeskBotConfig().getAgentServiceNotificationResponse());
      helpDeskBotSession.getSymphonyClient().getMessageService().sendMessage(agentChat, symMessage);

      return new SuccessResponse();
    } catch (SymException e) {
      LOG.error("Could not accept ticket: ", e);
      throw new InternalServerErrorException();
    }
  }

  @Override
  public SuccessResponse acceptMakerCheckerMessage(MakerCheckerMessage makerCheckerMessage) {
    helpDeskBotSession.getAgentMakerCheckerService().acceptMakerCheckerMessage(makerCheckerMessage);
    return new SuccessResponse();
  }
}
