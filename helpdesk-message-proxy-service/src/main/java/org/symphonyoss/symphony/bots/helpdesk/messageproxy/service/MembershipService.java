package org.symphonyoss.symphony.bots.helpdesk.messageproxy.service;

import static org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient
    .MembershipType.AGENT;
import static org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient
    .MembershipType.CLIENT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient
    .MembershipType;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Created by rsanchez on 01/12/17.
 */
@Service
public class MembershipService {

  private static final Logger LOGGER = LoggerFactory.getLogger(MembershipService.class);

  private final MembershipClient membershipClient;

  private final String agentStreamId;

  public MembershipService(MembershipClient membershipClient,
      @Value("agentStreamId") String agentStreamId) {
    this.membershipClient = membershipClient;
    this.agentStreamId = agentStreamId;
  }

  public Membership updateMembership(SymMessage symMessage, String type) {
    Long userId = symMessage.getFromUserId();

    Membership membership = getMembership(userId);

    if (AGENT.getType().equals(type)) {
      if (membership == null) {
        membership = createAgentMembership(userId);
      } else {
        membership = updateAgentMembership(membership);
      }
    }

    if (CLIENT.getType().equals(type) && membership == null) {
      membership = createClientMembership(userId);
    }

    return membership;
  }

  public Membership getMembership(Long userId) {
    Membership membership = membershipClient.getMembership(userId);

    if (membership != null) {
      LOGGER.info("Found membership: " + membership.toString());
    }

    return membership;
  }

  public Membership createAgentMembership(Long userId) {
    Membership membership = createMembership(userId, AGENT);

    LOGGER.info("Created new agent membership for userid: " + userId);

    return membership;
  }

  public Membership createClientMembership(Long userId) {
    Membership membership = createMembership(userId, CLIENT);

    LOGGER.info("Created new client membership for userid: " + userId);

    return membership;
  }

  public Membership updateAgentMembership(Membership membership) {
    membership.setType(AGENT.getType());
    Membership updatedMembership = updateMembership(membership);

    LOGGER.info("Updated agent membership for userid: " + membership.getId());

    return updatedMembership;
  }

  private Membership createMembership(Long userId, MembershipType type) {
    return membershipClient.newMembership(userId, type);
  }

  private Membership updateMembership(Membership membership) {
    return membershipClient.updateMembership(membership);
  }

}
