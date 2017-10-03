package com.symphony.bots.helpdesk.service.ticket;

import com.symphony.api.helpdesk.service.api.TicketApi;
import com.symphony.api.helpdesk.service.client.ApiClient;
import com.symphony.api.helpdesk.service.client.ApiException;
import com.symphony.api.helpdesk.service.client.Configuration;
import com.symphony.api.helpdesk.service.model.Ticket;
import com.symphony.bots.helpdesk.model.HelpDeskBotSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.exceptions.RoomException;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.client.services.ChatService;
import org.symphonyoss.client.services.RoomService;
import org.symphonyoss.symphony.clients.model.SymRoomAttributes;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.List;

/**
 * Created by nick.tarsillo on 9/26/17.
 * The ticket service manages and creates help desk tickets.
 */
public class TicketService {
  private static final Logger LOG = LoggerFactory.getLogger(TicketService.class);

  public enum TicketStateType {
    UNSERVICED("UNSERVICED"),
    UNRESOLVED("UNRESOLVED"),
    RESOLVED("RESOLVED");

    private String state;

    TicketStateType(String state) {
      this.state = state;
    }

    public String getState() {
      return state;
    }
  }

  private TicketApi ticketApi;
  private String groupId;

  private SymUser botUser;
  private RoomService roomService;
  private ChatService chatService;

  public TicketService(HelpDeskBotSession helpDeskSession, String ticketServiceUrl) {
    ApiClient apiClient = Configuration.getDefaultApiClient();
    apiClient.setBasePath(ticketServiceUrl);
    ticketApi = new TicketApi(apiClient);
    this.groupId = helpDeskSession.getGroupId();
    this.roomService = helpDeskSession.getSymphonyClient().getRoomService();
    this.chatService = helpDeskSession.getSymphonyClient().getChatService();
    this.botUser = helpDeskSession.getBotUser();
  }

  /**
   * Gets a ticket by ID.
   * @param ticketId the id of the ticket to get
   * @return the ticket
   */
  public Ticket getTicket(String ticketId) {
    try {
      return ticketApi.v1TicketIdGetGet(ticketId).getTicket();
    } catch (ApiException e) {
      LOG.error("Get ticket failed: ", e);
    }

    return null;
  }

  /**
   * Create a help desk ticket.
   * @param ticketId the ticket Id to use to create the ticket
   * @param clientStreamId the stream id of the client room
   * @param transcript the help transcript to use to create the ticket
   * @return the ticket
   */
  public Ticket createTicket(String ticketId, String clientStreamId, String transcript) {
    Ticket ticket = new Ticket();
    ticket.setId(ticketId);
    ticket.setGroupId(groupId);
    ticket.setClientStreamId(clientStreamId);
    ticket.setServiceStreamId(newServiceStream(ticketId, clientStreamId));
    ticket.setState(TicketStateType.UNSERVICED.getState());
    ticket.addTranscriptItem(transcript);
    try {
      return ticketApi.v1TicketCreatePost(ticket).getTicket();
    } catch (ApiException e) {
      LOG.error("Creating ticket failed: ", e);
    }

    return null;
  }

  /**
   * Gets a ticket by it's service stream Id
   * @param serviceStreamId the stream of the client service room
   * @return the ticket
   */
  public Ticket getTicketByServiceRoomId(String serviceStreamId) {
    try {
      List<Ticket> ticketList = ticketApi.v1TicketSearchGet(null, groupId, serviceStreamId).getTicketList();
      if(!ticketList.isEmpty()) {
        return ticketList.get(0);
      }
    } catch (ApiException e) {
      LOG.error("Failed to search for room: ", e);
    }

    return null;
  }

  /**
   * Updates a ticket.
   * @param ticket the ticket to update.
   * @return the new ticket.
   */
  public Ticket updateTicket(Ticket ticket) {
    try {
      return ticketApi.v1TicketIdUpdatePost(ticket.getId(), ticket).getTicket();
    } catch (ApiException e) {
      LOG.error("Updating ticket failed: ", e);
    }

    return null;
  }

  /**
   * Creates a new service stream for a ticket.
   * @param ticketId the ticket ID to create the service stream for
   * @param streamId the clients stream ID
   * @return the stream ID for the new service stream
   */
  private String newServiceStream(String ticketId, String streamId) {
    SymRoomAttributes roomAttributes = new SymRoomAttributes();
    roomAttributes.setCreatorUser(botUser);

    String users = "";
    for(SymUser symUser: chatService.getChatByStream(streamId).getRemoteUsers()) {
      users += symUser.getFirstName() + " " + symUser.getLastName() + ", ";
    }
    users = users.substring(0, users.length() - 3);

    roomAttributes.setDescription("Service room for users " + users + ".");
    roomAttributes.setDiscoverable(false);
    roomAttributes.setMembersCanInvite(true);
    roomAttributes.setName(groupId + " Ticket Room (" + ticketId + ")");
    roomAttributes.setReadOnly(false);
    roomAttributes.setPublic(false);

    Room room = null;
    try {
      room = roomService.createRoom(roomAttributes);
    } catch (RoomException e) {
      LOG.error("Create room failed: ", e);
    }

    return room.getStreamId();
  }

}
