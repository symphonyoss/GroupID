package com.symphony.bots.helpdesk.bot;

import com.symphony.bots.helpdesk.config.BotConfig;
import com.symphony.bots.helpdesk.config.BotStartupConfiguration;
import com.symphony.bots.helpdesk.model.HelpDeskBotSession;
import com.symphony.bots.helpdesk.model.ai.HelpDeskAi;
import com.symphony.bots.helpdesk.service.makerchecker.MakerCheckerService;
import com.symphony.bots.helpdesk.service.membership.MembershipService;
import com.symphony.bots.helpdesk.service.messageproxy.MessageProxyService;
import com.symphony.bots.helpdesk.service.ticket.TicketService;

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
  public HelpDeskBot(BotStartupConfiguration configuration) {
    SymphonyClient symClient = new SymphonyBasicClient();

    AuthenticationClient authClient = new AuthenticationClient(configuration.getSessionAuthUrl(),
        configuration.getKeyAuthUrl());

    LOG.info("Setting up auth http client for help desk bot with group id: " + configuration.getGroupId());
    try {
      authClient.setKeystores(configuration.getTrustStoreFile(),
          configuration.getTrustStorePassword(),
          configuration.getKeyStoreFile(),
          configuration.getKeyStorePassword());
    } catch (Exception e) {
      LOG.error("Could not create HTTP Client for authentication: ", e);
    }
    LOG.info("Attempting bot auth for help desk bot with group id: " + configuration.getGroupId());
    try {
      SymAuth symAuth = authClient.authenticate();

      symClient.init(symAuth,
          configuration.getEmail(),
          configuration.getAgentUrl(),
          configuration.getPodUrl());
    } catch (Exception e) {
      LOG.error("Authentication failed for bot: ", e);
    }

    HelpDeskBotSession helpDeskSession = new HelpDeskBotSession();
    helpDeskSession.setGroupId(configuration.getGroupId());
    helpDeskSession.setSymphonyClient(symClient);

    try {
      helpDeskSession.setBotUser(symClient.getUsersClient().getUserFromEmail(configuration.getEmail()));
    } catch (UsersClientException e) {
      LOG.error("Failed to retrieve bot user: ", e);
    }

    HelpDeskAi helpDeskAi =
        new HelpDeskAi(helpDeskSession.getSymphonyClient().getMessagesClient(), true,
            System.getProperty(BotConfig.SESSION_CONTEXT_DIR));
    helpDeskSession.setHelpDeskAi(helpDeskAi);

    MakerCheckerService agentMakerCheckerService = new MakerCheckerService(helpDeskSession);
    helpDeskSession.setAgentMakerCheckerService(agentMakerCheckerService);

    MakerCheckerService clientMakerCheckerService = new MakerCheckerService(helpDeskSession);
    helpDeskSession.setClientMakerCheckerService(clientMakerCheckerService);

    MembershipService membershipService =
        new MembershipService(helpDeskSession, System.getProperty(BotConfig.MEMBER_SERVICE_URL));
    helpDeskSession.setMembershipService(membershipService);

    TicketService ticketService =
        new TicketService(helpDeskSession, System.getProperty(BotConfig.TICKET_SERVICE_URL));
    helpDeskSession.setTicketService(ticketService);

    MessageProxyService messageProxyService = new MessageProxyService(helpDeskSession);
    helpDeskSession.setMessageProxyService(messageProxyService);
  }
}
