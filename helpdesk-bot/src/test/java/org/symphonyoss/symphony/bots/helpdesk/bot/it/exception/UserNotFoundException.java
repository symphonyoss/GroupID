package org.symphonyoss.symphony.bots.helpdesk.bot.it.exception;

/**
 * Exception to report the given agent not found.
 *
 * Created by rsanchez on 23/02/18.
 */
public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String message) {
    super(message);
  }

}
