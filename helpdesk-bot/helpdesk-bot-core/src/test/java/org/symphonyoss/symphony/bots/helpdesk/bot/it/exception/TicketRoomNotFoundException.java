package org.symphonyoss.symphony.bots.helpdesk.bot.it.exception;

/**
 * Exception to report the ticket room not found.
 *
 * Created by rsanchez on 23/02/18.
 */
public class TicketRoomNotFoundException extends RuntimeException {

  public TicketRoomNotFoundException(String message) {
    super(message);
  }

}
