package org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.exception;

/**
 * Created by alexandre-silva-daitan on 04/12/17.
 */
public class CreateMakercheckerExcpetion extends RuntimeException {
  public CreateMakercheckerExcpetion(String id, Exception cause) {
    super("Failed to create new makerchecker. Id: " + id, cause);
  }
}
