package org.symphonyoss.symphony.bots.helpdesk.messageproxy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.HelpDeskServiceInfo;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.message.TicketMessageBuilder;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.clients.MessagesClient;
import org.symphonyoss.symphony.clients.UsersClient;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;

/**
 * Created by rsanchez on 01/12/17.
 */
@Service
public class TicketService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TicketService.class);

  private static final String HELPDESK_BOT_CONTEXT = "helpdesk-bot";

  private static final String TICKET_HEADER = "Equities Desk Bot";

  private final String agentStreamId;

  private final TicketClient ticketClient;

  private final MessagesClient messagesClient;

  private final UsersClient usersClient;

  private final HelpDeskServiceInfo helpDeskServiceInfo;

  public TicketService(@Value("${agentStreamId}") String agentStreamId, TicketClient ticketClient,
      SymphonyClient symphonyClient, HelpDeskServiceInfo helpDeskServiceInfo) {
    this.agentStreamId = agentStreamId;
    this.ticketClient = ticketClient;
    this.helpDeskServiceInfo = helpDeskServiceInfo;
    this.messagesClient = symphonyClient.getMessagesClient();
    this.usersClient = symphonyClient.getUsersClient();
  }

  public Ticket createTicket(String ticketId, SymMessage message, String serviceStreamId) {
    Ticket ticket = ticketClient.createTicket(ticketId, message.getStreamId(), serviceStreamId);
    sendTicketMessageToAgentStreamId(ticket, message);

    return ticket;
  }

  public Ticket getUnresolvedTicket(String streamId) {
    return ticketClient.getUnresolvedTicketByClientStreamId(streamId);
  }

  private void sendTicketMessageToAgentStreamId(Ticket ticket, SymMessage message) {
    SymStream stream = new SymStream();
    stream.setStreamId(agentStreamId);

    TicketMessageBuilder builder = new TicketMessageBuilder();

    builder.host(helpDeskServiceInfo.getUrl(HELPDESK_BOT_CONTEXT));
    builder.ticketId(ticket.getId());
    builder.ticketState(ticket.getState());

    builder.header(TICKET_HEADER);

    try {
      SymUser symUser = usersClient.getUserFromId(message.getFromUserId());
      builder.username(symUser.getDisplayName());
      builder.company(symUser.getCompany());
    } catch (UsersClientException e) {
      LOGGER.error("Could not get user info: ", e);
    }

    builder.question(message.getMessageText());

    try {
      messagesClient.sendMessage(stream, builder.build());
    } catch (MessagesException e) {
      LOGGER.error("Could not send ticket message to agent stream ID: ", e);
    }
  }

  public void sendClientMessageToServiceStreamId() {
    // TODO
  }
}
