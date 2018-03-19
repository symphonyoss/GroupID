package org.symphonyoss.symphony.bots.helpdesk.service.authentication.exception;

/**
 * Created by rsanchez on 12/03/17.
 */
public class SaveTokensException extends RuntimeException {

  public SaveTokensException(String appId, Exception cause) {
    super("Failed to save app token. App ID: " + appId, cause);
  }

}
