package org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.exception;

/**
 * Created by alexandre-silva-daitan on 04/12/17.
 */
public class GetMakercheckerException extends RuntimeException {
  public GetMakercheckerException(String id, Exception cause) {
    super("Failed to get makerchecker. Id: " + id, cause);
  }
}
