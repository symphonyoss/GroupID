package org.symphonyoss.symphony.bots.helpdesk.messageproxy.service;

import static org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient.MembershipType.AGENT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.authenticator.model.Token;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient.MembershipType;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.utility.client.SymphonyClientUtil;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Created by rsanchez on 01/12/17.
 */
@Service
public class MembershipService {

  private static final Logger LOGGER = LoggerFactory.getLogger(MembershipService.class);

  private final MembershipClient membershipClient;

  private final SymphonyClientUtil symphonyClientUtil;

  public MembershipService(MembershipClient membershipClient, SymphonyClient symphonyClient) {
    this.membershipClient = membershipClient;
    this.symphonyClientUtil = new SymphonyClientUtil(symphonyClient);
  }

  public Membership updateMembership(SymMessage symMessage, MembershipType type) {
    Long userId = symMessage.getFromUserId();

    Membership membership = getMembership(userId);

    if (membership == null) {
      membership = createMembership(userId, type);
    } else if (AGENT.equals(type) && !type.toString().equals(membership.getType())) {
      membership.setType(AGENT.getType());
      membership = updateMembership(membership);
    }

    return membership;
  }

  public Membership getMembership(Long userId) {
    Membership membership = membershipClient.getMembership(symphonyClientUtil.getAuthToken(), userId);

    if (membership != null) {
      LOGGER.info("Found membership: " + membership.toString());
    }

    return membership;
  }

  private Membership createMembership(Long userId, MembershipType type) {
    return membershipClient.newMembership(symphonyClientUtil.getAuthToken(), userId, type);
  }

  private Membership updateMembership(Membership membership) {
    return membershipClient.updateMembership(symphonyClientUtil.getAuthToken(), membership);
  }

}
