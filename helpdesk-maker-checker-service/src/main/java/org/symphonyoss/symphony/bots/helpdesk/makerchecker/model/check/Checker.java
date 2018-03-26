package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.check;

import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client.MakercheckerClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Makerchecker;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.Optional;
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

  /**
   * Builds checker messages.
   * @param symMessage Symphony message received
   * @param proxyToIds Data used by checker to mount the checker messages
   * @return Checker messages
   */
  Set<SymMessage> buildSymCheckerMessages(SymMessage symMessage, Set<String> proxyToIds);

  boolean isCheckerType(MakerCheckerMessage makerCheckerMessage);

  Set<SymMessage> makeApprovedMessages(MakerCheckerMessage makerCheckerMessage, SymMessage symMessage);

  void afterSendApprovedMessage(SymMessage symMessage);

  SymMessage getActionMessage(Makerchecker makerchecker, MakercheckerClient.AttachmentStateType attachmentState);

}
