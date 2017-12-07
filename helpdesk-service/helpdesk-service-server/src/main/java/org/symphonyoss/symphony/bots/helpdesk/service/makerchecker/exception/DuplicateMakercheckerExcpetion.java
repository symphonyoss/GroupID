package org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.exception;

import com.mongodb.DuplicateKeyException;

/**
 * Created by alexandre-silva-daitan on 04/12/17.
 */
public class DuplicateMakercheckerExcpetion extends RuntimeException {
  public DuplicateMakercheckerExcpetion(String id, DuplicateKeyException cause) {
    super("Makerchecker already exists. Id: " + id, cause);
  }
}
