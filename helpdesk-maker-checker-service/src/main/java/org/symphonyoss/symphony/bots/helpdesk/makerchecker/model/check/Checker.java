package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.check;

import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerServiceSession;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.Set;

/**
 * Created by nick.tarsillo on 9/27/17.
 * Represents a check, used by the maker checker.
 */
public interface Checker {
  /**
   * If check fails, data that caused the check to fail will be returned.
   * @param message the message to validate
   * @return the flagged data
   */
  Set<Object> check(SymMessage message);

  Set<SymMessage> buildSymCheckerMessages(Set<Object> flaggedData, SymMessage symMessage);

  Set<SymMessage> makeApprovedMessages(MakerCheckerMessage makerCheckerMessage, SymMessage symMessage);

  boolean isCheckerType(MakerCheckerMessage makerCheckerMessage);

  void setSession(MakerCheckerServiceSession makerCheckerServiceSession);
}
