package org.symphonyoss.symphony.bots.helpdesk.service.ticket.exception;

/**
 * Created by rsanchez on 22/11/17.
 */
public class TicketNotFoundException extends RuntimeException {

  public TicketNotFoundException(String id) {
    super("Ticket not found. Ticket: " + id);
  }

}
