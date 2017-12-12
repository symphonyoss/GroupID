package org.symphonyoss.symphony.bots.helpdesk.messageproxy.service;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.HelpDeskBotInfo;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.HelpDeskServiceInfo;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.message.TicketMessageBuilder;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.model.UserInfo;
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

  private final String agentStreamId;

  private final String claimHeader;

  private final TicketClient ticketClient;

  private final MessagesClient messagesClient;

  private final UsersClient usersClient;

  private final HelpDeskBotInfo helpDeskBotInfo;

  private final HelpDeskServiceInfo helpDeskServiceInfo;

  public TicketService(@Value("${agentStreamId}") String agentStreamId,
      @Value("${claimEntityHeader}") String claimHeader,
      TicketClient ticketClient, SymphonyClient symphonyClient,
      HelpDeskBotInfo helpDeskBotInfo, HelpDeskServiceInfo helpDeskServiceInfo) {
    this.agentStreamId = agentStreamId;
    this.ticketClient = ticketClient;
    this.claimHeader = claimHeader;
    this.helpDeskBotInfo = helpDeskBotInfo;
    this.helpDeskServiceInfo = helpDeskServiceInfo;
    this.messagesClient = symphonyClient.getMessagesClient();
    this.usersClient = symphonyClient.getUsersClient();
  }

  public Ticket createTicket(String ticketId, SymMessage message, String serviceStreamId) {
    UserInfo client = null;

    try {
      SymUser symUser = usersClient.getUserFromId(message.getFromUserId());

      client = new UserInfo();
      client.setUserId(symUser.getId());
      client.setDisplayName(symUser.getDisplayName());
    } catch (UsersClientException e) {
      LOGGER.error("Could not get symphony user when creating ticket: ", e);
    }

    Ticket ticket = ticketClient.createTicket(ticketId, message.getStreamId(), serviceStreamId,
        Long.valueOf(message.getTimestamp()), client);
    sendTicketMessageToAgentStreamId(ticket, message);

    return ticket;
  }

  public Ticket getUnresolvedTicket(String streamId) {
    return ticketClient.getUnresolvedTicketByClientStreamId(streamId);
  }

  public Ticket getTicketByServiceStreamId(String streamId) {
    return ticketClient.getTicketByServiceStreamId(streamId);
  }

  private void sendTicketMessageToAgentStreamId(Ticket ticket, SymMessage message) {
    SymStream stream = new SymStream();
    stream.setStreamId(agentStreamId);

    String safeAgentStreamId = Base64.encodeBase64String(Base64.decodeBase64(agentStreamId));

    TicketMessageBuilder builder = new TicketMessageBuilder();

    builder.botHost(helpDeskBotInfo.getUrl());
    builder.serviceHost(helpDeskServiceInfo.getUrl());

    builder.ticketId(ticket.getId());
    builder.ticketState(ticket.getState());
    builder.streamId(safeAgentStreamId);

    builder.header(claimHeader);

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

  public void sendIdleMessageToAgentStreamId(SymMessage message) {
    SymStream stream = new SymStream();
    stream.setStreamId(agentStreamId);

    try {
      messagesClient.sendMessage(stream, message);
    } catch (MessagesException e) {
      LOGGER.error("Could not send ticket message to agent stream ID: ", e);
    }
  }
}
