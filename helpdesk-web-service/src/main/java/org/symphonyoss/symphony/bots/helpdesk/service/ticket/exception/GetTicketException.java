package org.symphonyoss.symphony.bots.helpdesk.service.ticket.exception;

/**
 * Created by rsanchez on 22/11/17.
 */
public class GetTicketException extends RuntimeException {

  public GetTicketException(String id, Throwable cause) {
    super("Failed to get ticket. Ticket: " + id, cause);
  }

}
