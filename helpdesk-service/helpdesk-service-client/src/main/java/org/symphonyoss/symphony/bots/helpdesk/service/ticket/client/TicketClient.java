package org.symphonyoss.symphony.bots.helpdesk.service.ticket.client;

import org.symphonyoss.symphony.bots.helpdesk.service.HelpDeskApiException;
import org.symphonyoss.symphony.bots.helpdesk.service.api.TicketApi;
import org.symphonyoss.symphony.bots.helpdesk.service.client.ApiClient;
import org.symphonyoss.symphony.bots.helpdesk.service.client.ApiException;
import org.symphonyoss.symphony.bots.helpdesk.service.client.Configuration;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.model.UserInfo;

import java.util.List;

/**
 * Created by nick.tarsillo on 9/26/17.
 * The ticket service manages and creates help desk tickets.
 */
public class TicketClient {

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

  public TicketClient(String groupId, String ticketServiceUrl) {
    ApiClient apiClient = Configuration.getDefaultApiClient();
    apiClient.setBasePath(ticketServiceUrl);
    ticketApi = new TicketApi(apiClient);
    this.groupId = groupId;
  }

  /**
   * Gets a ticket by ID.
   * @param ticketId the id of the ticket to get
   * @return the ticket
   */
  public Ticket getTicket(String ticketId) {
    try {
      return ticketApi.getTicket(ticketId);
    } catch (ApiException e) {
      throw new HelpDeskApiException("Get ticket failed: " + ticketId, e);
    }
  }

  /**
   * Create a help desk ticket.
   * @param ticketId the ticket Id to use to create the ticket
   * @param clientStreamId the stream id of the client room
   * @return the ticket
   */
  public Ticket createTicket(String ticketId, String clientStreamId, String serviceStream,
      Long timestamp, UserInfo client, Boolean showHistory, String conversationId, Long timeStamp) {
    Ticket ticket = new Ticket();
    ticket.setId(ticketId);
    ticket.setGroupId(groupId);
    ticket.setClientStreamId(clientStreamId);
    ticket.setServiceStreamId(serviceStream);
    ticket.setState(TicketStateType.UNSERVICED.getState());
    ticket.setClient(client);
    ticket.setQuestionTimestamp(timestamp);
    ticket.setShowHistory(showHistory);
    ticket.setConversationID(conversationId);
    ticket.setTimeStamp(timeStamp);

    try {
      return ticketApi.createTicket(ticket);
    } catch (ApiException e) {
      throw new HelpDeskApiException("Creating ticket failed: " + ticketId, e);
    }
  }

  /**
   * Gets a ticket by it's service stream Id
   * @param serviceStreamId the stream of the client service room
   * @return the ticket
   */
  public Ticket getTicketByServiceStreamId(String serviceStreamId) {
    try {
      List<Ticket> ticketList = ticketApi.searchTicket(groupId, serviceStreamId, null);

      if ((ticketList != null) && (!ticketList.isEmpty())) {
        return ticketList.get(0);
      }

      return null;
    } catch (ApiException e) {
      throw new HelpDeskApiException("Failed to search for room: " + serviceStreamId, e);
    }
  }

  /**
   * Gets a unresolved ticket by it's client stream Id
   * @param clientStreamId the stream of the client stream id
   * @return the ticket
   */
  public Ticket getUnresolvedTicketByClientStreamId(String clientStreamId) {
    try {
      List<Ticket> ticketList = ticketApi.searchTicket(groupId, null, clientStreamId);

      if ((ticketList != null) && (!ticketList.isEmpty())) {
        return ticketList.stream()
            .filter((ticket) -> !ticket.getState().equals(TicketStateType.RESOLVED.getState()))
            .findFirst()
            .orElse(null);
      }

      return null;
    } catch (ApiException e) {
      throw new HelpDeskApiException("Failed to search for room: " + clientStreamId, e);
    }
  }

  /**
   * Updates a ticket.
   * @param ticket the ticket to update.
   * @return the new ticket.
   */
  public Ticket updateTicket(Ticket ticket) {
    try {
      return ticketApi.updateTicket(ticket.getId(), ticket);
    } catch (ApiException e) {
      throw new HelpDeskApiException("Updating ticket failed: " + ticket.getId(), e);
    }
  }

}
