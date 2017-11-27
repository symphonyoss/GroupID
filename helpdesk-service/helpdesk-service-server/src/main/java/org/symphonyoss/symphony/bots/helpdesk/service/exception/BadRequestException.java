package org.symphonyoss.symphony.bots.helpdesk.service.exception;

/**
 * Created by rsanchez on 10/11/17.
 */
public class BadRequestException extends RuntimeException {

  public BadRequestException(String message) {
    super(message);
  }

}
