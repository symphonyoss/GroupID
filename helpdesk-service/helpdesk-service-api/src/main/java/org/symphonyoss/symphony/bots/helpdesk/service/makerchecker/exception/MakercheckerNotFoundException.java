package org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.exception;

/**
 * Created by alexandre-silva-daitan on 04/12/17.
 */
public class MakercheckerNotFoundException extends RuntimeException {
  public MakercheckerNotFoundException(String id) {
    super("Makerchecker not found. Id: " + id);
  }
}
