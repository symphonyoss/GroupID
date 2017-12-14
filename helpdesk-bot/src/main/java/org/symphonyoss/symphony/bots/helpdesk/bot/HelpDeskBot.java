package org.symphonyoss.symphony.bots.helpdesk.bot;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.InitException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.clients.UsersClient;
import org.symphonyoss.symphony.clients.model.SymUser;

import javax.annotation.PostConstruct;

/**
 * Created by nick.tarsillo on 9/26/17.
 */
@Component
public class HelpDeskBot {

  private static final Logger LOG = LoggerFactory.getLogger(HelpDeskBot.class);

  private final HelpDeskBotConfig configuration;

  private final SymphonyClient symphonyClient;

  private final MembershipClient membershipClient;

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

  /**
   * Initializes the help desk bot. This includes:
   *    Authenticating with pod.
   *    Initializing the help desk ai. (Handles command line commands and contextual conversations with bot)
   *    Initializing the member service. (Handles checking a users membership by UID)
   *    Initializing the ticket service. (Manages and stores tickets.)
   *    Initializing the maker checker services. (Validates messages, and requests validation from another agent if needed.)
   *    Initializing the message proxy service. (Handles the proxying of client/agent messages.)
   */
  @PostConstruct
  public void init() throws InitException {
    String groupId = configuration.getGroupId();

    if (groupId == null) {
      throw new IllegalStateException("GroupId were not provided");
    }

    registerDefaultAgent();

    LOG.info("Help Desk Bot startup complete fpr groupId: " + configuration.getGroupId());
  }

  private void registerDefaultAgent() {
    String email = configuration.getDefaultAgentEmail();

    if(!StringUtils.isBlank(email)) {
      UsersClient userClient = symphonyClient.getUsersClient();
      try {
        SymUser symUser = userClient.getUserFromEmail(email);
        Membership membership = membershipClient.getMembership(symUser.getId());

        if(membership == null) {
          membershipClient.newMembership(symUser.getId(), MembershipClient.MembershipType.AGENT);
        } else if(!MembershipClient.MembershipType.AGENT.getType().equals(membership.getType())){
          membership.setType(MembershipClient.MembershipType.AGENT.getType());
          membershipClient.updateMembership(membership);
        }
      } catch (UsersClientException e) {
        throw new IllegalStateException("Error registering default agent user: " + email, e);
      }
    } else {
      throw new IllegalStateException("Bot email address were not provided");
    }
  }

}
