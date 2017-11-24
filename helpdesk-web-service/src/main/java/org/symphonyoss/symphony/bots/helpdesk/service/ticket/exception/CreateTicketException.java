package org.symphonyoss.symphony.bots.helpdesk.service.ticket.exception;

/**
 * Created by rsanchez on 22/11/17.
 */
public class CreateTicketException extends RuntimeException {

  public CreateTicketException(Throwable cause) {
    super("Failed to create ticket", cause);
  }

}
