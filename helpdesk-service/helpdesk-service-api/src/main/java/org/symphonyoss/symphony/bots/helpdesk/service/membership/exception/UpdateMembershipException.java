package org.symphonyoss.symphony.bots.helpdesk.service.membership.exception;

/**
 * Created by rsanchez on 22/11/17.
 */
public class UpdateMembershipException extends RuntimeException {

  public UpdateMembershipException(String groupId, Long id, Throwable cause) {
    super("Failed to update membership. Group Id: " + groupId + ", userId: " + id, cause);
  }

}
