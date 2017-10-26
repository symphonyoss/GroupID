package org.symphonyoss.symphony.bots.helpdesk.bot;

import org.symphonyoss.symphony.bots.helpdesk.config.BotStartupConfiguration;
import org.symphonyoss.symphony.bots.helpdesk.config.DefaultBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.model.HelpDeskBotSession;
import org.symphonyoss.symphony.bots.helpdesk.model.ai.HelpDeskAi;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.MakerCheckerService;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.model.AgentExternalCheck;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.MembershipService;
import org.symphonyoss.symphony.bots.helpdesk.service.messageproxy.MessageProxyService;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.TicketService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.impl.SymphonyBasicClient;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.clients.AuthenticationClient;

/**
 * Created by nick.tarsillo on 9/26/17.
 */
public class HelpDeskBot {
  private static final Logger LOG = LoggerFactory.getLogger(HelpDeskBot.class);

  /**
   * Initializes the help desk bot. This includes:
   *    Authenticating with pod.
   *    Initializing the help desk ai. (Handles command line commands and contextual conversations with bot)
   *    Initializing the member service. (Handles checking a users membership by UID)
   *    Initializing the ticket service. (Manages and stores tickets.)
   *    Initializing the maker checker services. (Validates messages, and requests validation from another agent if needed.)
   *    Initializing the message proxy service. (Handles the proxying of client/agent messages.)
   * @param configuration
   */
  public HelpDeskBot(String groupId, BotStartupConfiguration configuration) {
    SymphonyClient symClient = new SymphonyBasicClient();

    AuthenticationClient authClient = new AuthenticationClient(configuration.getSessionAuthUrl(),
        configuration.getKeyAuthUrl());

    LOG.info("Setting up auth http client for help desk bot with group id: " + groupId);
    try {
      authClient.setKeystores(configuration.getTrustStoreFile(),
          configuration.getTrustStorePassword(),
          configuration.getKeyStoreFile(),
          configuration.getKeyStorePassword());
    } catch (Exception e) {
      LOG.error("Could not create HTTP Client for authentication: ", e);
    }
    LOG.info("Attempting bot auth for help desk bot with group id: " + groupId);
    try {
      SymAuth symAuth = authClient.authenticate();

      symClient.init(symAuth,
          configuration.getEmail(),
          configuration.getAgentUrl(),
          configuration.getPodUrl());
    } catch (Exception e) {
      LOG.error("Authentication failed for bot: ", e);
    }

    HelpDeskBotConfig helpDeskBotConfig = HelpDeskBotConfig.getConfig(groupId);

    HelpDeskBotSession helpDeskSession = new HelpDeskBotSession();
    helpDeskSession.setGroupId(groupId);
    helpDeskSession.setSymphonyClient(symClient);
    helpDeskSession.setHelpDeskBotConfig(helpDeskBotConfig);

    try {
      helpDeskSession.setBotUser(symClient.getUsersClient().getUserFromEmail(configuration.getEmail()));
    } catch (UsersClientException e) {
      LOG.error("Failed to retrieve bot user: ", e);
    }

    HelpDeskAi helpDeskAi =
        new HelpDeskAi(helpDeskSession.getSymphonyClient().getMessagesClient(), true,
            System.getProperty(DefaultBotConfig.SESSION_CONTEXT_DIR), helpDeskSession);
    helpDeskSession.setHelpDeskAi(helpDeskAi);

    MakerCheckerService agentMakerCheckerService = new MakerCheckerService(helpDeskSession);
    agentMakerCheckerService.addCheck(new AgentExternalCheck(helpDeskSession));
    helpDeskSession.setAgentMakerCheckerService(agentMakerCheckerService);

    MakerCheckerService clientMakerCheckerService = new MakerCheckerService(helpDeskSession);
    helpDeskSession.setClientMakerCheckerService(clientMakerCheckerService);

    MembershipService membershipService =
        new MembershipService(helpDeskSession, System.getProperty(DefaultBotConfig.MEMBER_SERVICE_URL));
    helpDeskSession.setMembershipService(membershipService);

    TicketService ticketService =
        new TicketService(helpDeskSession, System.getProperty(DefaultBotConfig.TICKET_SERVICE_URL));
    helpDeskSession.setTicketService(ticketService);

    MessageProxyService messageProxyService = new MessageProxyService(helpDeskSession);
    helpDeskSession.setMessageProxyService(messageProxyService);
  }
}
