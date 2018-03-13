package org.symphonyoss.symphony.apps.authentication.servlets.model;

/**
 * Holds HTTP error content.
 *
 * Created by rsanchez on 06/03/18.
 */
public class ErrorResponse {

  private int code;

  private String message;

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
