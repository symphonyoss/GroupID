package org.symphonyoss.symphony.bots.helpdesk.service.membership.exception;

/**
 * Created by rsanchez on 22/11/17.
 */
public class GetMembershipException extends RuntimeException {

  public GetMembershipException(String groupId, Long userId, Throwable cause) {
    super("Failed to get membership. GroupId: " + groupId + ", userId: " + userId, cause);
  }

}
