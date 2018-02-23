package org.symphonyoss.symphony.bots.helpdesk.messageproxy.service;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.HelpDeskBotInfo;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.HelpDeskServiceInfo;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.InstructionalMessageConfig;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.message.ClaimMessageBuilder;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.message.InstructionalMessageBuilder;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.model.UserInfo;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.message.SymMessageUtil;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


/**
 * Created by rsanchez on 01/12/17.
 */
@Service
public class TicketService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TicketService.class);

  private static final String SERVICE_ROOM_WAS_NOT_CREATED =
      "There was a problem trying to create the service room. Please try again.";

  private final String agentStreamId;

  private final String claimHeader;

  private final String createTicketMessage;

  private final String serviceRoomWasNotCreated;

  private final TicketClient ticketClient;

  private final SymphonyClient symphonyClient;

  private final InstructionalMessageConfig instructionalMessageConfig;

  private final HelpDeskBotInfo helpDeskBotInfo;

  private final HelpDeskServiceInfo helpDeskServiceInfo;

  public TicketService(@Value("${agentStreamId}") String agentStreamId,
      @Value("${claimEntityHeader}") String claimHeader,
      @Value("${createTicketMessage}") String createTicketMessage,
      @Value("${serviceRoomWasNotCreated}") String serviceRoomWasNotCreated,
      TicketClient ticketClient, SymphonyClient symphonyClient,
      InstructionalMessageConfig instructionalMessageConfig, HelpDeskBotInfo helpDeskBotInfo,
      HelpDeskServiceInfo helpDeskServiceInfo) {
    this.agentStreamId = agentStreamId;
    this.ticketClient = ticketClient;
    this.claimHeader = claimHeader;
    this.symphonyClient = symphonyClient;
    this.helpDeskBotInfo = helpDeskBotInfo;
    this.helpDeskServiceInfo = helpDeskServiceInfo;
    this.createTicketMessage = createTicketMessage;
    this.serviceRoomWasNotCreated = serviceRoomWasNotCreated;
    this.instructionalMessageConfig = instructionalMessageConfig;
  }

  public Ticket createTicket(String ticketId, SymMessage message, Room serviceStream) {
    UserInfo client = null;

    try {
      SymUser symUser = symphonyClient.getUsersClient().getUserFromId(message.getFromUserId());

      client = new UserInfo();
      client.setUserId(symUser.getId());
      client.setDisplayName(symUser.getDisplayName());
    } catch (UsersClientException e) {
      LOGGER.error("Could not get symphony user when creating ticket: ", e);
    }

    Boolean viewHistory = serviceStream.getRoomDetail().getRoomAttributes().getViewHistory();
    Ticket ticket =
        ticketClient.createTicket(ticketId, message.getStreamId(), serviceStream.getId(),
            Long.valueOf(message.getTimestamp()), client, viewHistory, message.getId());
    sendTicketMessageToAgentStreamId(ticket, message);

    SymMessage symMessage = new SymMessage();
    symMessage.setMessageText(createTicketMessage);
    sendClientMessageToServiceStreamId(message.getStreamId(), symMessage);

    InstructionalMessageBuilder messageBuilder = new InstructionalMessageBuilder()
        .message(instructionalMessageConfig.getMessage())
        .command(instructionalMessageConfig.getCommand())
        .mentionUserId(symphonyClient.getLocalUser().getId());
    sendClientMessageToServiceStreamId(ticket.getServiceStreamId(), messageBuilder.build());

    return ticket;
  }

  public Ticket getUnresolvedTicket(String streamId) {
    return ticketClient.getUnresolvedTicketByClientStreamId(streamId);
  }

  public Ticket getTicketByServiceStreamId(String streamId) {
    return ticketClient.getTicketByServiceStreamId(streamId);
  }

  public void sendTicketMessageToAgentStreamId(Ticket ticket, SymMessage message) {
    SymStream stream = new SymStream();
    stream.setStreamId(agentStreamId);

    String safeAgentStreamId = Base64.encodeBase64String(Base64.decodeBase64(agentStreamId));

    ClaimMessageBuilder builder = new ClaimMessageBuilder();

    builder.botHost(helpDeskBotInfo.getUrl());
    builder.serviceHost(helpDeskServiceInfo.getUrl());

    builder.ticketId(ticket.getId());
    builder.ticketState(ticket.getState());
    builder.streamId(safeAgentStreamId);

    builder.header(claimHeader);

    try {
      SymUser symUser = symphonyClient.getUsersClient().getUserFromId(message.getFromUserId());
      String username = symUser.getDisplayName();
      builder.username(username);
      builder.company(symUser.getCompany());
      builder.question(getQuestionFromMessage(message, username));
    } catch (UsersClientException e) {
      LOGGER.error("Could not get user info: ", e);
      builder.question(getQuestionFromMessage(message, "Client"));
    }

    try {
      symphonyClient.getMessagesClient().sendMessage(stream, builder.build());
    } catch (MessagesException e) {
      LOGGER.error("Could not send ticket message to agent stream ID: ", e);
    }
  }

  private String getQuestionFromMessage(SymMessage message, String username) {
    if (SymMessageUtil.isChime(message)) {
      return username + " sent a chime!";
    }

    if (SymMessageUtil.hasAttachment(message) && message.getMessageText().equals("")) {
      return username + " sent an attachment!";
    }

    if (SymMessageUtil.hasTable(message)) {
      return username + " sent a table!";
    }

    return message.getMessageText();
  }

  public void sendClientMessageToServiceStreamId(String streamId, SymMessage message) {
    SymStream stream = new SymStream();
    stream.setStreamId(streamId);

    try {
      symphonyClient.getMessagesClient().sendMessage(stream, message);
    } catch (MessagesException e) {
      LOGGER.error("Could not send ticket message to agent stream ID: ", e);
    }
  }

  public void sendIdleMessageToAgentStreamId(SymMessage message) {
    SymStream stream = new SymStream();
    stream.setStreamId(agentStreamId);

    try {
      symphonyClient.getMessagesClient().sendMessage(stream, message);
    } catch (MessagesException e) {
      LOGGER.error("Could not send ticket message to agent stream ID: ", e);
    }
  }

  /**
   * This method is responsible to send message to room when the service room was not created.
   * @param symMessage The message to be sent to room.
   */
  public void sendMessageWhenRoomCreationFails(SymMessage symMessage) {
    symMessage.setMessageText(serviceRoomWasNotCreated);

    try {
      symphonyClient.getMessagesClient().sendMessage(symMessage.getStream(), symMessage);
    } catch (MessagesException e) {
      LOGGER.error("Could not send message to client stream ID: ", e);
    }
  }
}
