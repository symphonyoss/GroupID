package com.symphony.helpdesk.service.model;


import com.symphony.api.helpdesk.service.model.Ticket;
import com.symphony.api.helpdesk.service.model.TicketResponse;
import com.symphony.api.helpdesk.service.model.TicketSearchResponse;

/**
 * Created by nick.tarsillo on 9/25/17.
 * Doa for tickets.
 */
public interface TicketDoa {
  TicketResponse createTicket(Ticket ticket);
  Ticket deleteTicket(String id);
  TicketResponse getTicket(String id);
  TicketSearchResponse searchTicket(String id, String groupId, String serviceRoomId);
  TicketResponse updateTicket(String id, Ticket ticket);
}
