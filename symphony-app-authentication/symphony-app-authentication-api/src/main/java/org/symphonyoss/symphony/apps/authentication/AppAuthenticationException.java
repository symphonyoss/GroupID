package org.symphonyoss.symphony.apps.authentication;

/**
 * Exception to report failures during the app authentication.
 *
 * Created by rsanchez on 06/03/18.
 */
public class AppAuthenticationException extends Exception {

  public AppAuthenticationException(String message) {
    super(message);
  }

  public AppAuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }

}
