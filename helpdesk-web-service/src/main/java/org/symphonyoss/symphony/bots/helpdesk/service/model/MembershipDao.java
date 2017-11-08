package org.symphonyoss.symphony.bots.helpdesk.service.model;

import org.symphonyoss.symphony.bots.helpdesk.service.model.health.HealthCheckFailedException;

/**
 * Created by nick.tarsillo on 9/25/17.
 * Doa for membership.
 */
public interface MembershipDao {
  MembershipResponse createMembership(Membership membership);
  Membership deleteMembership(String id, String groupId);
  MembershipResponse getMembership(String id, String groupId);
  MembershipResponse updateMembership(String id, String groupId, Membership ticket);
  void healthcheck() throws HealthCheckFailedException;
}
