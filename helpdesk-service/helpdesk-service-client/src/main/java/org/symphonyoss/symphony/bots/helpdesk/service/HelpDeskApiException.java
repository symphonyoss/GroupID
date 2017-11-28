package org.symphonyoss.symphony.bots.helpdesk.service;

import org.symphonyoss.symphony.bots.helpdesk.service.client.ApiException;

/**
 * Created by robson on 27/11/17.
 */
public class HelpDeskApiException extends RuntimeException {

  private int code;

  public HelpDeskApiException(String message, ApiException e) {
    super(message, e);
    this.code = e.getCode();
  }

  public int getCode() {
    return code;
  }
}
