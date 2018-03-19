package org.symphonyoss.symphony.bots.helpdesk.service.ticket.exception;

/**
 * Created by rsanchez on 22/11/17.
 */
public class DuplicateTicketException extends RuntimeException {

  public DuplicateTicketException(String id, Throwable cause) {
    super("Ticket already exists. TicketId: " + id, cause);
  }

}
