package org.symphonyoss.symphony.bots.helpdesk.service.membership.exception;

/**
 * Created by rsanchez on 22/11/17.
 */
public class MembershipNotFoundException extends RuntimeException {

  public MembershipNotFoundException(String groupId, Long id) {
    super("Membership not found. Group Id: " + groupId + ", userId: " + id);
  }

}
