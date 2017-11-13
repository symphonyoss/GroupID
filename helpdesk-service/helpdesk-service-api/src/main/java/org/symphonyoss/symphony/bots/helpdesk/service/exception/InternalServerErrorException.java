package org.symphonyoss.symphony.bots.helpdesk.service.exception;

/**
 * Created by rsanchez on 10/11/17.
 */
public class InternalServerErrorException extends RuntimeException {

  public InternalServerErrorException(String message) {
    super(message);
  }

}
