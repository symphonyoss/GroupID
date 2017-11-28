package org.symphonyoss.symphony.bots.helpdesk.service.ticket.exception;

/**
 * Created by rsanchez on 22/11/17.
 */
public class DeleteTicketException extends RuntimeException {

  public DeleteTicketException(String id, Throwable cause) {
    super("Failed to delete ticket. Ticket: " + id, cause);
  }

}
