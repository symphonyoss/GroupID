package org.symphonyoss.symphony.bots.helpdesk.service.membership.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.bots.helpdesk.service.api.MembershipApi;
import org.symphonyoss.symphony.bots.helpdesk.service.client.ApiClient;
import org.symphonyoss.symphony.bots.helpdesk.service.client.ApiException;
import org.symphonyoss.symphony.bots.helpdesk.service.client.Configuration;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;

/**
 * Created by nick.tarsillo on 9/26/17.
 * Service for managing and getting memberships of users with bot.
 */
public class MembershipClient {
  private static final Logger LOG = LoggerFactory.getLogger(MembershipClient.class);

  public enum MembershipType {
    CLIENT("CLIENT"),
    AGENT("AGENT");

    private String type;
    MembershipType(String type) {
      this.type = type;
    }

    public String getType() {
      return type;
    }
  }

  private MembershipApi membershipApi;
  private String groupId;

  public MembershipClient(String groupId, String memberServiceUrl) {
    ApiClient apiClient = Configuration.getDefaultApiClient();
    apiClient.setBasePath(memberServiceUrl);
    membershipApi = new MembershipApi(apiClient);
    this.groupId = groupId;
  }

  /**
   * Get the membership of a user.
   * @param userId the user id of the user.
   * @return
   */
  public Membership getMembership(Long userId) {
    try {
      return membershipApi.getMembership(groupId, userId);
    } catch (ApiException e) {
      LOG.error("Failed to get membership: ", e);
    }

    return null;
  }

  /**
   * Create a new membership for a user. (CLIENT by default.)
   * @param userId the user id to create a membership for.
   * @return the new membership
   */
  public Membership newMembership(Long userId, MembershipType membershipType) {
    Membership membership = new Membership();
    membership.setId(userId);
    membership.setGroupId(groupId);
    membership.setType(membershipType.getType());

    try {
      Membership newMembership = membershipApi.createMembership(membership);
      return newMembership;
    } catch (ApiException e) {
      LOG.error("Failed to create new membership for user: ", e);
    }

    return membership;
  }

  /**
   * Promotes a user to have an AGENT level membership.
   * @param membership the membership to update.
   * @return the updated membership
   */
  public Membership updateMembership(Membership membership) {
    try {
      Membership updateMembership = membershipApi.updateMembership(groupId, membership.getId(), membership);
      LOG.info("Promoted client to agent for userid: " + membership.getId());
      return updateMembership;
    } catch (ApiException e) {
      LOG.error("Could not update membership", e);
    }

    return null;
  }
}
