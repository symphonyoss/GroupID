package org.symphonyoss.symphony.bots.helpdesk.service.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.symphonyoss.symphony.bots.helpdesk.service.api.TicketApi;
import org.symphonyoss.symphony.bots.helpdesk.service.config.HelpDeskServiceConfig;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;

import java.util.List;

/**
 * Created by nick.tarsillo on 9/26/17.
 * The ticket service manages and creates help desk tickets.
 */
public class TicketClient {
  private static final Logger LOG = LoggerFactory.getLogger(TicketClient.class);

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
      return ticketApi.getTicket(ticketId).getTicket();
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
  public Ticket createTicket(String ticketId, String clientStreamId, String newServiceStream, String transcript) {
    Ticket ticket = new Ticket();
    ticket.setId(ticketId);
    ticket.setGroupId(groupId);
    ticket.setClientStreamId(clientStreamId);
    ticket.setServiceStreamId(newServiceStream);
    ticket.setState(TicketStateType.UNSERVICED.getState());
    ticket.addTranscriptItem(transcript);
    try {
      return ticketApi.createTicket(ticket).getTicket();
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
  public Ticket getTicketByServiceStreamId(String serviceStreamId) {
    try {
      List<Ticket> ticketList = ticketApi.searchTicket(null, groupId, serviceStreamId, null).getTicketList();
      if(!ticketList.isEmpty()) {
        return ticketList.get(0);
      }
    } catch (ApiException e) {
      LOG.error("Failed to search for room: ", e);
    }

    return null;
  }

  /**
   * Gets a ticket by it's client stream Id
   * @param clientStreamId the stream of the client stream id
   * @return the ticket
   */
  public Ticket getTicketByClientStreamId(String clientStreamId) {
    try {
      List<Ticket> ticketList = ticketApi.searchTicket(null, groupId, null, clientStreamId).getTicketList();
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
      return ticketApi.updateTicket(ticket.getId(), ticket).getTicket();
    } catch (ApiException e) {
      LOG.error("Updating ticket failed: ", e);
    }

    return null;
  }

}
