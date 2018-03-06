package org.symphonyoss.symphony.bots.helpdesk.bot.it.exception;

/**
 * Exception to report the message not found.
 *
 * Created by crepache on 23/02/18.
 */
public class MessageNotFoundException extends RuntimeException {

  public MessageNotFoundException(String message) {
    super(message);
  }

}
