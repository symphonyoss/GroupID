package org.symphonyoss.symphony.bots.helpdesk.service.authentication.exception;

/**
 * Created by rsanchez on 12/03/17.
 */
public class RetrieveTokensException extends RuntimeException {

  public RetrieveTokensException(String appToken, Exception cause) {
    super("Failed to get app token. App token: " + appToken, cause);
  }

}
