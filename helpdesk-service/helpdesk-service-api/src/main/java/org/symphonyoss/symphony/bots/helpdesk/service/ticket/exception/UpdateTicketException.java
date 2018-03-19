package org.symphonyoss.symphony.bots.helpdesk.service.ticket.exception;

/**
 * Created by rsanchez on 22/11/17.
 */
public class UpdateTicketException extends RuntimeException {

  public UpdateTicketException(String id, Throwable cause) {
    super("Failed to update ticket. Ticket: " + id, cause);
  }

}
