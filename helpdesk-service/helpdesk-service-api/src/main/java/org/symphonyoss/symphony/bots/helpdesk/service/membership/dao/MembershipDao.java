package org.symphonyoss.symphony.bots.helpdesk.service.membership.dao;

import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;

/**
 * Created by nick.tarsillo on 9/25/17.
 *
 * DAO for membership.
 */
public interface MembershipDao {

  /**
   * Create a new membership in the database.
   *
   * @param membership Membership data
   * @return Created membership
   */
  Membership createMembership(Membership membership);

  /**
   * Deletes a membership from the database.
   *
   * @param groupId Group ID
   * @param id Membership ID
   */
  void deleteMembership(String groupId, Long id);

  /**
   * Gets a membership from the database.
   *
   * @param groupId Group ID
   * @param id Membership ID
   * @return Membership
   */
  Membership getMembership(String groupId, Long id);

  /**
   * Updates a membership in the database.
   *
   * @param groupId Group ID
   * @param id Membership ID
   * @param membership Membership data
   * @return Membership updated
   */
  Membership updateMembership(String groupId, Long id, Membership membership);

}
