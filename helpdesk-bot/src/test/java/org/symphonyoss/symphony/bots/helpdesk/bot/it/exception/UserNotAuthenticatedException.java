package org.symphonyoss.symphony.bots.helpdesk.bot.it.exception;

/**
 * Exception to report the given user is not authenticated.
 *
 * Created by rsanchez on 23/02/18.
 */
public class UserNotAuthenticatedException extends RuntimeException {

  public UserNotAuthenticatedException(String message) {
    super(message);
  }

}
