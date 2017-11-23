package org.symphonyoss.symphony.bots.helpdesk.service.membership.exception;

/**
 * Created by rsanchez on 22/11/17.
 */
public class DeleteMembershipException extends RuntimeException {

  public DeleteMembershipException(String groupId, Long id, Throwable cause) {
    super("Failed to delete membership. GroupId: " + groupId + ", userId: " + id, cause);
  }

}
