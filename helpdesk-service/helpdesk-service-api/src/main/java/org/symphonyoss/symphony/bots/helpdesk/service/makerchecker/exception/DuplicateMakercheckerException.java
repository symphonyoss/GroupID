package org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.exception;

/**
 * Created by alexandre-silva-daitan on 04/12/17.
 */
public class DuplicateMakercheckerException extends RuntimeException {

  public DuplicateMakercheckerException(String id, Exception cause) {
    super("Makerchecker already exists. Id: " + id, cause);
  }

}
