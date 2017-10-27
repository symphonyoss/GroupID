package org.symphonyoss.symphony.bots.helpdesk.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.impl.SymphonyBasicClient;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.bots.ai.HelpDeskAi;
import org.symphonyoss.symphony.bots.ai.HelpDeskAiSession;
import org.symphonyoss.symphony.bots.ai.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.helpdesk.config.BotStartupConfiguration;
import org.symphonyoss.symphony.bots.helpdesk.config.DefaultBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.MakerCheckerService;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.AgentExternalCheck;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.MessageProxyService;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.model.MessageProxyServiceSession;
import org.symphonyoss.symphony.bots.helpdesk.service.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.client.TicketClient;
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

    MembershipClient membershipClient = new MembershipClient(groupId, System.getProperty(DefaultBotConfig.TICKET_SERVICE_URL));
    TicketClient ticketClient = new TicketClient(groupId, System.getProperty(DefaultBotConfig.TICKET_SERVICE_URL));

    HelpDeskAiSession helpDeskAiSession = new HelpDeskAiSession();
    helpDeskAiSession.setMembershipClient(membershipClient);
    helpDeskAiSession.setTicketClient(ticketClient);
    helpDeskAiSession.setSymphonyClient(symClient);

    HelpDeskAiConfig helpDeskAiConfig = new HelpDeskAiConfig();
    helpDeskAiConfig.setCloseTicketSuccessResponse(helpDeskBotConfig.getCloseTicketSuccessResponse());
    helpDeskAiConfig.setAddMemberAgentSuccessResponse(helpDeskBotConfig.getAddMemberAgentSuccessResponse());
    helpDeskAiConfig.setAddMemberClientSuccessResponse(helpDeskBotConfig.getAddMemberClientSuccessResponse());
    helpDeskAiConfig.setAcceptTicketAgentSuccessResponse(helpDeskBotConfig.getAcceptTicketAgentSuccessResponse());
    helpDeskAiConfig.setAcceptTicketAgentSuccessResponse(helpDeskBotConfig.getAcceptTicketClientSuccessResponse());
    helpDeskAiConfig.setAcceptTicketCommand(helpDeskBotConfig.getAcceptTicketCommand());
    helpDeskAiConfig.setCloseTicketCommand(helpDeskBotConfig.getCloseTicketCommand());
    helpDeskAiConfig.setAddMemberCommand(helpDeskBotConfig.getAddMemberCommand());
    helpDeskAiConfig.setDefaultPrefix(helpDeskBotConfig.getAiDefaultPrefix());
    helpDeskAiConfig.setAgentServiceRoomPrefix(helpDeskBotConfig.getAiServicePrefix());

    helpDeskAiSession.setHelpDeskAiConfig(helpDeskAiConfig);

    HelpDeskAi helpDeskAi = new HelpDeskAi(helpDeskAiSession);

    MakerCheckerService agentMakerCheckerService =
        new MakerCheckerService(helpDeskBotConfig.getMakerCheckerMessageTemplate(),
            helpDeskBotConfig.getMakerCheckerEntityTemplate());
    agentMakerCheckerService.addCheck(new AgentExternalCheck(ticketClient));

    MakerCheckerService clientMakerCheckerService = new MakerCheckerService(helpDeskBotConfig.getMakerCheckerMessageTemplate(),
        helpDeskBotConfig.getMakerCheckerEntityTemplate());

    MessageProxyServiceSession proxyServiceSession = new MessageProxyServiceSession();
    proxyServiceSession.setGroupId(groupId);
    proxyServiceSession.setHelpDeskAi(helpDeskAi);
    proxyServiceSession.setSymphonyClient(symClient);
    proxyServiceSession.setAgentMakerCheckerService(agentMakerCheckerService);
    proxyServiceSession.setClientMakerCheckerService(clientMakerCheckerService);
    proxyServiceSession.setMembershipClient(membershipClient);
    proxyServiceSession.setTicketClient(ticketClient);

    MessageProxyService messageProxyService = new MessageProxyService(proxyServiceSession);
    symClient.getMessageService().addMessageListener(messageProxyService);
  }
}
