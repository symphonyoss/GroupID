package org.symphonyoss.symphony.bots.helpdesk.bot;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.impl.SymphonyBasicClient;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.bots.ai.HelpDeskAi;
import org.symphonyoss.symphony.bots.ai.HelpDeskAiSession;
import org.symphonyoss.symphony.bots.ai.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.session.HelpDeskBotSession;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.MakerCheckerService;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.AgentExternalCheck;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerServiceSession;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.MessageProxyService;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.MessageProxyServiceConfig;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.model.MessageProxyServiceSession;
import org.symphonyoss.symphony.bots.helpdesk.service.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.client.TicketClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.clients.AuthenticationClient;
import org.symphonyoss.symphony.clients.UsersClient;
import org.symphonyoss.symphony.clients.model.SymUser;

import javax.annotation.PostConstruct;

/**
 * Created by nick.tarsillo on 9/26/17.
 */
@Component
public class HelpDeskBot {

  private static final Logger LOG = LoggerFactory.getLogger(HelpDeskBot.class);

  private final HelpDeskBotConfig helpDeskBotConfig;

  private HelpDeskBotSession helpDeskBotSession;

  /**
   * Normal constructor.
   * @param helpDeskBotConfig a configuration for the help desk bot.
   */
  public HelpDeskBot(HelpDeskBotConfig helpDeskBotConfig) {
    this.helpDeskBotConfig = helpDeskBotConfig;
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
  public void init() {
    LOG.info("Help Desk Bot starting up for groupId: " + helpDeskBotConfig.getGroupId());

    helpDeskBotSession = new HelpDeskBotSession();
    helpDeskBotSession.setHelpDeskBotConfig(helpDeskBotConfig);
    helpDeskBotSession.setSymphonyClient(initSymphonyClient(helpDeskBotConfig));
    helpDeskBotSession.setTicketClient(initTicketClient(helpDeskBotConfig));
    helpDeskBotSession.setMembershipClient(initMembershipClient(helpDeskBotConfig));
    helpDeskBotSession.setHelpDeskAi(initHelpDeskAi(helpDeskBotConfig));
    helpDeskBotSession.setAgentMakerCheckerService(initAgentMakerCheckerService());
    helpDeskBotSession.setClientMakerCheckerService(initClientMakerCheckerService());
    helpDeskBotSession.setMessageProxyService(initMessageProxyService());

    registerDefaultAgent();

    LOG.info("Help Desk Bot startup complete fpr groupId: " + helpDeskBotConfig.getGroupId());
  }

  private SymphonyClient initSymphonyClient(HelpDeskBotConfig configuration) {
    SymphonyClient symClient = new SymphonyBasicClient();

    AuthenticationClient authClient = new AuthenticationClient(configuration.getSessionAuthUrl(),
        configuration.getKeyAuthUrl());

    LOG.info("Setting up auth http client for help desk bot with group id: " + configuration.getGroupId());
    try {
      System.setProperty("javax.net.ssl.keyStore", configuration.getKeyStoreFile());
      System.setProperty("javax.net.ssl.keyStorePassword", configuration.getKeyStorePassword());
      System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");
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

    return symClient;
  }

  private HelpDeskAi initHelpDeskAi(HelpDeskBotConfig configuration) {
    HelpDeskAiSession helpDeskAiSession = new HelpDeskAiSession();
    helpDeskAiSession.setMembershipClient(helpDeskAiSession.getMembershipClient());
    helpDeskAiSession.setTicketClient(helpDeskBotSession.getTicketClient());
    helpDeskAiSession.setSymphonyClient(helpDeskBotSession.getSymphonyClient());

    HelpDeskAiConfig helpDeskAiConfig = new HelpDeskAiConfig();
    helpDeskAiConfig.setCloseTicketSuccessResponse(configuration.getCloseTicketSuccessResponse());
    helpDeskAiConfig.setAddMemberAgentSuccessResponse(configuration.getAddMemberAgentSuccessResponse());
    helpDeskAiConfig.setAddMemberClientSuccessResponse(configuration.getAddMemberClientSuccessResponse());
    helpDeskAiConfig.setAcceptTicketAgentSuccessResponse(configuration.getAcceptTicketAgentSuccessResponse());
    helpDeskAiConfig.setAcceptTicketAgentSuccessResponse(configuration.getAcceptTicketClientSuccessResponse());
    helpDeskAiConfig.setAcceptTicketCommand(configuration.getAcceptTicketCommand());
    helpDeskAiConfig.setCloseTicketCommand(configuration.getCloseTicketCommand());
    helpDeskAiConfig.setAddMemberCommand(configuration.getAddMemberCommand());
    helpDeskAiConfig.setDefaultPrefix(configuration.getAiDefaultPrefix());
    helpDeskAiConfig.setAgentServiceRoomPrefix(configuration.getAiServicePrefix());

    helpDeskAiSession.setHelpDeskAiConfig(helpDeskAiConfig);

    HelpDeskAi helpDeskAi = new HelpDeskAi(helpDeskAiSession);

    return helpDeskAi;
  }

  private MakerCheckerService initAgentMakerCheckerService() {
    HelpDeskBotConfig configuration = helpDeskBotSession.getHelpDeskBotConfig();

    MakerCheckerServiceSession makerCheckerServiceSession = new MakerCheckerServiceSession();
    makerCheckerServiceSession.setEntityTemplate(configuration.getMakerCheckerMessageTemplate());
    makerCheckerServiceSession.setMessageTemplate(configuration.getMakerCheckerMessageTemplate());
    makerCheckerServiceSession.setSymphonyClient(helpDeskBotSession.getSymphonyClient());
    MakerCheckerService agentMakerCheckerService = new MakerCheckerService(makerCheckerServiceSession);
    agentMakerCheckerService.addCheck(new AgentExternalCheck(helpDeskBotSession.getTicketClient()));

    return  agentMakerCheckerService;
  }

  private MakerCheckerService initClientMakerCheckerService() {
    HelpDeskBotConfig configuration = helpDeskBotSession.getHelpDeskBotConfig();

    MakerCheckerServiceSession makerCheckerServiceSession = new MakerCheckerServiceSession();
    makerCheckerServiceSession.setEntityTemplate(configuration.getMakerCheckerMessageTemplate());
    makerCheckerServiceSession.setMessageTemplate(configuration.getMakerCheckerMessageTemplate());
    makerCheckerServiceSession.setSymphonyClient(helpDeskBotSession.getSymphonyClient());
    MakerCheckerService clientMakerCheckerService = new MakerCheckerService(makerCheckerServiceSession);

    return clientMakerCheckerService;
  }

  private MembershipClient initMembershipClient(HelpDeskBotConfig configuration) {
    MembershipClient membershipClient = new MembershipClient(configuration.getGroupId(),
        configuration.getMemberServiceUrl());

    return membershipClient;
  }

  private TicketClient initTicketClient(HelpDeskBotConfig configuration) {
    TicketClient ticketClient = new TicketClient(configuration.getGroupId(),
        configuration.getTicketServiceUrl());

    return ticketClient;
  }

  private MessageProxyService initMessageProxyService() {
    HelpDeskBotConfig configuration = helpDeskBotSession.getHelpDeskBotConfig();

    MessageProxyServiceSession proxyServiceSession = new MessageProxyServiceSession();
    MessageProxyServiceConfig messageProxyServiceConfig = new MessageProxyServiceConfig();
    messageProxyServiceConfig.setGroupId(configuration.getGroupId());
    messageProxyServiceConfig.setAgentStreamId(configuration.getAgentStreamId());
    messageProxyServiceConfig.setClaimMessageTemplate(configuration.getClaimMessageTemplate());
    messageProxyServiceConfig.setClaimEntityTemplate(configuration.getClaimEntityTemplate());
    messageProxyServiceConfig.setTicketCreationMessage(configuration.getTicketCreationMessage());

    proxyServiceSession.setMessageProxyServiceConfig(messageProxyServiceConfig);
    proxyServiceSession.setHelpDeskAi(helpDeskBotSession.getHelpDeskAi());
    proxyServiceSession.setSymphonyClient(helpDeskBotSession.getSymphonyClient());
    proxyServiceSession.setAgentMakerCheckerService(helpDeskBotSession.getAgentMakerCheckerService());
    proxyServiceSession.setClientMakerCheckerService(helpDeskBotSession.getClientMakerCheckerService());
    proxyServiceSession.setMembershipClient(helpDeskBotSession.getMembershipClient());
    proxyServiceSession.setTicketClient(helpDeskBotSession.getTicketClient());

    MessageProxyService messageProxyService = new MessageProxyService(proxyServiceSession);
    helpDeskBotSession.getSymphonyClient().getMessageService().addMessageListener(messageProxyService);

    return messageProxyService;
  }

  private void registerDefaultAgent() {
    HelpDeskBotConfig configuration = helpDeskBotSession.getHelpDeskBotConfig();

    if(!StringUtils.isBlank(configuration.getDefaultAgentEmail())) {
      MembershipClient membershipClient = helpDeskBotSession.getMembershipClient();
      UsersClient userClient = helpDeskBotSession.getSymphonyClient().getUsersClient();
      try {
        SymUser symUser = userClient.getUserFromEmail(configuration.getDefaultAgentEmail());
        Membership membership = membershipClient.getMembership(symUser.getId().toString());
        if(membership == null) {
          membershipClient.newMembership(symUser.getId().toString(), MembershipClient.MembershipType.AGENT);
        } else if(!membership.getType().equals(MembershipClient.MembershipType.AGENT.getType())){
          membership.setType(MembershipClient.MembershipType.AGENT.getType());
          membershipClient.updateMembership(membership);
        }
      } catch (UsersClientException e) {
        LOG.error("Error registering default agent user: ", e);
      }
    }
  }

  public HelpDeskBotSession getHelpDeskBotSession() {
    return helpDeskBotSession;
  }
}
