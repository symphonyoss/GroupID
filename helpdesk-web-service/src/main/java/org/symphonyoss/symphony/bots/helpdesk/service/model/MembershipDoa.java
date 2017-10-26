package org.symphonyoss.symphony.bots.helpdesk.service.model;

/**
 * Created by nick.tarsillo on 9/25/17.
 * Doa for membership.
 */
public interface MembershipDoa {
  MembershipResponse createMembership(Membership membership);
  Membership deleteMembership(String id, String groupId);
  MembershipResponse getMembership(String id, String groupId);
  MembershipResponse updateMembership(String id, String groupId, Membership ticket);
}
