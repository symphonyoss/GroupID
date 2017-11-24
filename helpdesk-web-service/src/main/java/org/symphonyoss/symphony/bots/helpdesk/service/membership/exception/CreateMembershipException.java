package org.symphonyoss.symphony.bots.helpdesk.service.membership.exception;

/**
 * Created by rsanchez on 22/11/17.
 */
public class CreateMembershipException extends RuntimeException {

  public CreateMembershipException(String groupId, Long id, Throwable cause) {
    super("Failed to create new membership. GroupId: " + groupId + ", userId: " + id, cause);
  }

}
