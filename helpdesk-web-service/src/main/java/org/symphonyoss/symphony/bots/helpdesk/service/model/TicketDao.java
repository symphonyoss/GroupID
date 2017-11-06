package org.symphonyoss.symphony.bots.helpdesk.service.model;


/**
 * Created by nick.tarsillo on 9/25/17.
 * Doa for tickets.
 */
public interface TicketDao {
  TicketResponse createTicket(Ticket ticket);
  Ticket deleteTicket(String id);
  TicketResponse getTicket(String id);
  TicketSearchResponse searchTicket(String id, String groupId, String serviceRoomId, String clientStreamId);
  TicketResponse updateTicket(String id, Ticket ticket);
}
