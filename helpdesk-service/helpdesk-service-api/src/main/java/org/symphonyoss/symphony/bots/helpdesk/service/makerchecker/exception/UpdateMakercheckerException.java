package org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.exception;

/**
 * Created by alexandre-silva-daitan on 04/12/17.
 */
public class UpdateMakercheckerException extends RuntimeException {
  public UpdateMakercheckerException(String id, Exception cause) {
    super("Failed to update makerchecker. Id: " + id, cause);
  }
}
