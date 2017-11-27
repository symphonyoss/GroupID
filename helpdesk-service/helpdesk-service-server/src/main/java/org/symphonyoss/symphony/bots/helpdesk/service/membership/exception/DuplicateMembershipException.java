package org.symphonyoss.symphony.bots.helpdesk.service.membership.exception;

/**
 * Created by rsanchez on 22/11/17.
 */
public class DuplicateMembershipException extends RuntimeException {

  public DuplicateMembershipException(String groupId, Long id, Throwable cause) {
    super("Membership already exists. GroupId: " + groupId + ", userId: " + id, cause);
  }

}
