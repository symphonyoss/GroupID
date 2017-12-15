package org.symphonyoss.symphony.bots.helpdesk.bot;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.clients.model.SymUser;

import javax.annotation.PostConstruct;

/**
 * Created by nick.tarsillo on 9/26/17.
 */
@Component
public class HelpDeskBot {

  private final HelpDeskBotConfig configuration;

  private final SymphonyClient symphonyClient;

  private final MembershipClient membershipClient;

  private boolean ready;

  /**
   * Constructor to inject dependencies.
   * @param configuration a configuration for the help desk bot.
   * @param symphonyClient
   * @param membershipClient
   */
  public HelpDeskBot(HelpDeskBotConfig configuration, SymphonyClient symphonyClient,
      MembershipClient membershipClient) {
    this.configuration = configuration;
    this.symphonyClient = symphonyClient;
    this.membershipClient = membershipClient;
  }

  @PostConstruct
  public void validateGroupId() {
    String groupId = configuration.getGroupId();

    if (StringUtils.isBlank(groupId)) {
      throw new IllegalStateException("GroupId were not provided");
    }
  }

  /**
   * Register bot user as an agent member.
   */
  public void registerDefaultAgent() {
    SymUser localUser = symphonyClient.getLocalUser();
    Membership membership = membershipClient.getMembership(localUser.getId());

    if(membership == null) {
      membershipClient.newMembership(localUser.getId(), MembershipClient.MembershipType.AGENT);
    } else if(!MembershipClient.MembershipType.AGENT.getType().equals(membership.getType())){
      membership.setType(MembershipClient.MembershipType.AGENT.getType());
      membershipClient.updateMembership(membership);
    }
  }

  public void ready() {
    this.ready = true;
  }

  public boolean isReady() {
    return this.ready;
  }

}
