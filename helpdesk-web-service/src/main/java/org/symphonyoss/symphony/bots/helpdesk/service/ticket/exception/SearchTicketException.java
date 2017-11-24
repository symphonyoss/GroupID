package org.symphonyoss.symphony.bots.helpdesk.service.ticket.exception;

/**
 * Created by rsanchez on 22/11/17.
 */
public class SearchTicketException extends RuntimeException {

  public SearchTicketException(Throwable cause) {
    super("Failed to search ticket", cause);
  }

}
