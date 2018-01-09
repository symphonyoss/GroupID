package org.symphonyoss.symphony.apps.authentication.jwt.exception;

/**
 * Exception to report JWT processing failures.
 *
 * Created by rsanchez on 09/01/18.
 */
public class JwtProcessingException extends Exception {

  public JwtProcessingException(String message, Throwable cause) {
    super(message, cause);
  }

}
