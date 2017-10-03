package com.symphony.bots.helpdesk.service.membership;

import com.symphony.api.helpdesk.service.api.MembershipApi;
import com.symphony.api.helpdesk.service.client.ApiClient;
import com.symphony.api.helpdesk.service.client.ApiException;
import com.symphony.api.helpdesk.service.client.Configuration;
import com.symphony.api.helpdesk.service.model.Membership;
import com.symphony.bots.helpdesk.model.HelpDeskBotSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nick.tarsillo on 9/26/17.
 * Service for managing and getting memberships of users with bot.
 */
public class MembershipService {
  private static final Logger LOG = LoggerFactory.getLogger(MembershipService.class);

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

  public MembershipService(HelpDeskBotSession helpDeskSession, String memberServiceUrl) {
    ApiClient apiClient = Configuration.getDefaultApiClient();
    apiClient.setBasePath(memberServiceUrl);
    membershipApi = new MembershipApi(apiClient);
    this.groupId = helpDeskSession.getGroupId();
  }

  /**
   * Get the membership of a user.
   * @param userId the user id of the user.
   * @return
   */
  public Membership getMembership(String userId) {
    try {
      return membershipApi.v1MembershipIdGroupIdGetGet(userId, groupId).getMembership();
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
  public Membership newMembership(String userId) {
    Membership membership = new Membership();
    membership.setId(userId);
    membership.setGroupId(groupId);
    membership.setType(MembershipType.CLIENT.getType());
    try {
      Membership newMembership = membershipApi.v1MembershipCreatePost(membership).getMembership();
      LOG.info("Created new client membership for userid: " + userId);
      return newMembership;
    } catch (ApiException e) {
      LOG.error("Failed to create new membership for user: ", e);
    }
    return membership;
  }

  /**
   * Promotes a user to have an AGENT level membership.
   * @param userId the user id to promote.
   * @return the updated membership
   */
  public Membership promoteToAgent(String userId) {
    Membership membership = getMembership(userId);
    membership.setType(MembershipType.AGENT.getType());
    try {
      Membership updateMembership = membershipApi.v1MembershipIdGroupIdUpdatePost(userId, groupId, membership).getMembership();
      LOG.info("Promoted client to agent for userid: " + userId);
      return updateMembership;
    } catch (ApiException e) {
      LOG.error("Could not update membership", e);
    }

    return null;
  }
}
