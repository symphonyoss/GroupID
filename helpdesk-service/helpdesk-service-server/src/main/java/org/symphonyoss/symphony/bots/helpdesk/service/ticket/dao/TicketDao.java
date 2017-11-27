package org.symphonyoss.symphony.bots.helpdesk.service.ticket.dao;

import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;

import java.util.List;

/**
 * Created by nick.tarsillo on 9/25/17.
 *
 * DAO for tickets.
 */
public interface TicketDao {

  /**
   * Inserts a new ticket into database.
   *
   * @param ticket the ticket to insert.
   * @return success response with created ticket.
   */
  Ticket createTicket(Ticket ticket);

  /**
   * Deletes a ticket from database.
   *
   * @param id the ticket id.
   */
  void deleteTicket(String id);

  /**
   * Gets a ticket from database.
   *
   * @param id the ticket id.
   * @return success response with retrieved ticket.
   */
  Ticket getTicket(String id);

  /**
   * Searches for a ticket in the database.
   *
   * @param groupId GroupId
   * @param serviceStreamId Service Stream ID
   * @param clientStreamId Client Stream ID
   * @return success response with a list of tickets
   */
  List<Ticket> searchTicket(String groupId, String serviceStreamId, String clientStreamId);

  /**
   * Update a ticket in the database.
   *
   * @param id ticket id
   * @param ticket the ticket data to update with.
   * @return success response with updated ticket.
   */
  Ticket updateTicket(String id, Ticket ticket);

}
