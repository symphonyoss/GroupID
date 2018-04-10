package org.symphonyoss.symphony.bots.helpdesk.bot.util;

import static org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient.MembershipType.AGENT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.symphony.authenticator.model.Token;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.ticket.TicketService;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.utility.client.SymphonyClientUtil;
import org.symphonyoss.symphony.pod.model.MembershipList;

import javax.ws.rs.BadRequestException;

/**
 * Created by alexandre-silva-daitan on 20/12/17.
 */
@Component
public class ValidateMembershipService {

  private static final Logger LOG = LoggerFactory.getLogger(TicketService.class);

  private final MembershipClient membershipClient;

  private final SymphonyClient symphonyClient;

  private final HelpDeskBotConfig helpDeskBotConfig;

  private final SymphonyClientUtil symphonyClientUtil;

  public ValidateMembershipService(
      MembershipClient membershipClient, SymphonyClient symphonyClient,
      HelpDeskBotConfig helpDeskBotConfig) {
    this.membershipClient = membershipClient;
    this.symphonyClient = symphonyClient;
    this.helpDeskBotConfig = helpDeskBotConfig;
    this.symphonyClientUtil = new SymphonyClientUtil(symphonyClient);
  }

  /**
   * Update group memberships
   *
   * @param agentId User agent id
   * @throws SymException
   */
  public void updateMembership(Long agentId) throws SymException {
    String jwt = symphonyClientUtil.getAuthToken();

    Membership membership = membershipClient.getMembership(jwt, agentId);

    if (membership == null) {
      validateAgentStreamMemberships(agentId);

      membershipClient.newMembership(jwt, agentId, AGENT);

      LOG.info("Created new agent membership for userid: " + agentId);
    } else if (!AGENT.getType().equals(membership.getType())) {
      validateAgentStreamMemberships(agentId);

      membership.setType(AGENT.getType());
      membershipClient.updateMembership(jwt, membership);

      LOG.info("Updated agent membership for userid: " + agentId);
    }
  }

  /**
   * Validates if the user is a member of the agent stream.
   *
   * @param agentId User agent id
   * @throws SymException
   */
  public void validateAgentStreamMemberships(Long agentId) throws SymException {
    MembershipList agentMemberships = symphonyClient.getRoomMembershipClient()
        .getRoomMembership(helpDeskBotConfig.getAgentStreamId());

    long count = agentMemberships.stream()
        .filter(member -> member.getId().equals(agentId))
        .count();

    if (count == 0) {
      throw new BadRequestException("User is not an agent");
    }
  }

}
