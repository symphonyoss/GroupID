package org.symphonyoss.symphony.bots.helpdesk.messageproxy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.model.NodeTypes;
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
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by rsanchez on 01/12/17.
 */
@Service
public class TicketService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TicketService.class);

  private static final String SERVICE_ROOM_WAS_NOT_CREATED =
      "There was a problem trying to create the service room. Please try again.";

  private static final String DEFAULT_CLIENT_NAME = "Client";

  private static final String CHIME_MESSAGE = "%s sent a chime!";

  private static final String ATTACHMENT_MESSAGE = "%s sent an attachment!";

  private static final String TABLE_MESSAGE = "%s sent a table!";

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
      builder.question(getQuestionFromMessage(message, DEFAULT_CLIENT_NAME));
    }

    try {
      symphonyClient.getMessagesClient().sendMessage(stream, builder.build());
    } catch (MessagesException e) {
      LOGGER.error("Could not send ticket message to agent stream ID: ", e);
    }
  }

  /**
   * Return a string that will compose the question field of the ticket card
   * @param message The original message sent by the client
   * @param username The client username
   * @return String that will compose the question field of the ticket card
   */
  private String getQuestionFromMessage(SymMessage message, String username) {
    if (SymMessageUtil.isChime(message)) {
      return String.format(CHIME_MESSAGE, username);
    }

    if (SymMessageUtil.hasAttachment(message) && StringUtils.isEmpty(message.getMessageText())) {
      return String.format(ATTACHMENT_MESSAGE, username);
    }

    if (SymMessageUtil.hasTable(message)) {
      return String.format(TABLE_MESSAGE, username);
    }

    if (message.getMessage() != null) {
      return parseTicketMessage(message);
    } else {
      return message.getMessageText();
    }
  }

  /**
   * Parse the received PresentationML formatted message into a valid MessageML fragment that
   * will compose the "Question" field of the ticket
   * @param message The message to be processed
   * @return String containing the valid MessageML fragment
   */
  private String parseTicketMessage(SymMessage message) {
    Element elementMessageML;
    StringBuilder textDoc = new StringBuilder("");

    Document doc = Jsoup.parse(message.getMessage());

    doc.select("errors").remove();
    elementMessageML = doc.select("messageML").first();
    if (elementMessageML == null) {
      elementMessageML = doc.select("div").first();
    }

    if (elementMessageML != null) {
      for (Node node : elementMessageML.childNodes()) {
        if (node.nodeName().equalsIgnoreCase("span")) {
          if (node.attributes().get("class").equalsIgnoreCase("entity")) {
            String value = node.childNodes().get(0).toString();
            String statement = "";
            if (value.startsWith("#")) {
              statement =
                  "<" + NodeTypes.HASHTAG.toString() + " tag=\"" + value.substring(1) + "\" />";
            } else if (value.startsWith("$")) {
              statement =
                  "<" + NodeTypes.CASHTAG.toString() + " tag=\"" + value.substring(1) + "\" />";
            } else if (value.startsWith("@")) {
              try {
                LinkedHashMap result = (LinkedHashMap)
                    new ObjectMapper().readValue(message.getEntityData(), HashMap.class)
                        .get(node.attributes().get("data-entity-id"));
                ArrayList<LinkedHashMap> ids = (ArrayList) result.get("id");
                for (LinkedHashMap id : ids) {
                  statement +=
                      "<" + NodeTypes.MENTION.toString() + " uid=\"" + id.get("value").toString()
                          + "\" />";
                }
              } catch (IOException e) {
                LOGGER.error("Could not get entity data: ", e);
              }
            }
            textDoc.append(statement);
          }
        } else if (node.nodeName().equalsIgnoreCase("<br>")) {
          textDoc.append("<br/>");
        } else {
          textDoc.append(node.toString());
        }
      }
    }

    return textDoc.toString();
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
