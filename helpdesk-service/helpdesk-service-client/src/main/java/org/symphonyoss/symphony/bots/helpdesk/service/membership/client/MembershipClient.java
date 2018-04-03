package org.symphonyoss.symphony.bots.helpdesk.service.membership.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.bots.helpdesk.service.BaseClient;
import org.symphonyoss.symphony.bots.helpdesk.service.HelpDeskApiException;
import org.symphonyoss.symphony.bots.helpdesk.service.api.MembershipApi;
import org.symphonyoss.symphony.bots.helpdesk.service.client.ApiClient;
import org.symphonyoss.symphony.bots.helpdesk.service.client.ApiException;
import org.symphonyoss.symphony.bots.helpdesk.service.client.Configuration;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;

/**
 * Created by nick.tarsillo on 9/26/17.
 * Service for managing and getting memberships of users with bot.
 */
public class MembershipClient extends BaseClient {

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
   *
   * @param jwt User JWT
   * @param userId the user id of the user.
   * @return
   */
  public Membership getMembership(String jwt, Long userId) {
    String authorization = getAuthorizationHeader(jwt);

    try {
      return membershipApi.getMembership(authorization, groupId, userId);
    } catch (ApiException e) {
      throw new HelpDeskApiException("Failed to get membership: " + userId, e);
    }
  }

  /**
   * Create a new membership for a user. (CLIENT by default.)
   *
   * @param jwt User JWT
   * @param userId the user id to create a membership for.
   * @param membershipType Membership type (CLIENT or AGENT)
   * @return the new membership
   */
  public Membership newMembership(String jwt, Long userId, MembershipType membershipType) {
    String authorization = getAuthorizationHeader(jwt);

    Membership membership = new Membership();
    membership.setId(userId);
    membership.setGroupId(groupId);
    membership.setType(membershipType.getType());

    try {
      return membershipApi.createMembership(authorization, membership);
    } catch (ApiException e) {
      throw new HelpDeskApiException("Failed to create new membership for user: " + userId, e);
    }
  }

  /**
   * Promotes a user to have an AGENT level membership.
   *
   * @param jwt User JWT
   * @param membership the membership to update.
   * @return the updated membership
   */
  public Membership updateMembership(String jwt, Membership membership) {
    String authorization = getAuthorizationHeader(jwt);

    try {
      Membership updateMembership =
          membershipApi.updateMembership(authorization, groupId, membership.getId(), membership);
      LOG.info("Updated membership for userid: " + membership.getId());
      return updateMembership;
    } catch (ApiException e) {
      throw new HelpDeskApiException("Could not update membership", e);
    }
  }
}
