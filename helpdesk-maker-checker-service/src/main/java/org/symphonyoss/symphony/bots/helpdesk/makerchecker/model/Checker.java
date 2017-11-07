package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model;

import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Created by nick.tarsillo on 9/27/17.
 * Represents a check, used by the maker checker.
 */
public abstract class Checker {
  private String checkFailureMessage;

  /**
   * If check fails, verification will be required from another user.
   * @param message the message to validate
   * @return if the check passed
   */
   public abstract boolean check(SymMessage message);

  public String getCheckFailureMessage() {
    return checkFailureMessage;
  }

  public void setCheckFailureMessage(String checkFailureMessage) {
    this.checkFailureMessage = checkFailureMessage;
  }
}
