package org.symphonyoss.symphony.bots.helpdesk.bot;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.InitException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.impl.SymphonyBasicClient;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.bots.ai.HelpDeskAi;
import org.symphonyoss.symphony.bots.ai.HelpDeskAiSession;
import org.symphonyoss.symphony.bots.ai.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.authentication.HelpDeskAuthenticationService;
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

  private final HelpDeskAuthenticationService authenticationService;

  private final HelpDeskBotSession helpDeskBotSession;

  /**
   * Constructor to inject dependencies.
   *
   * @param configuration a configuration for the help desk bot.
   * @param authenticationService service to authenticate bot.
   */
  public HelpDeskBot(HelpDeskBotConfig configuration,
      HelpDeskAuthenticationService authenticationService) {
    this.configuration = configuration;
    this.authenticationService = authenticationService;

    this.helpDeskBotSession = new HelpDeskBotSession();
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
    LOG.info("Help Desk Bot starting up for groupId: " + configuration.getGroupId());

    helpDeskBotSession.setHelpDeskBotConfig(configuration);
    helpDeskBotSession.setSymphonyClient(initSymphonyClient());
    helpDeskBotSession.setTicketClient(initTicketClient());
    helpDeskBotSession.setMembershipClient(initMembershipClient());
    helpDeskBotSession.setHelpDeskAi(initHelpDeskAi());
    helpDeskBotSession.setAgentMakerCheckerService(initAgentMakerCheckerService());
    helpDeskBotSession.setClientMakerCheckerService(initClientMakerCheckerService());
    helpDeskBotSession.setMessageProxyService(initMessageProxyService());

    registerDefaultAgent();

    LOG.info("Help Desk Bot startup complete fpr groupId: " + configuration.getGroupId());
  }

  /**
   * Initializes Symphony Client.
   *
   * @return Symphony Client
   * @throws InitException Failure to initialize the client
   */
  private SymphonyClient initSymphonyClient() throws InitException {
    SymAuth symAuth = authenticationService.authenticate();

    SymphonyClient symClient = new SymphonyBasicClient();

    symClient.init(symAuth, configuration.getEmail(), configuration.getAgentUrl(),
        configuration.getPodUrl());

    return symClient;
  }

  private HelpDeskAi initHelpDeskAi() {
    HelpDeskAiSession helpDeskAiSession = new HelpDeskAiSession();
    helpDeskAiSession.setMembershipClient(helpDeskBotSession.getMembershipClient());
    helpDeskAiSession.setTicketClient(helpDeskBotSession.getTicketClient());
    helpDeskAiSession.setSymphonyClient(helpDeskBotSession.getSymphonyClient());

    HelpDeskAiConfig helpDeskAiConfig = new HelpDeskAiConfig();
    helpDeskAiConfig.setGroupId(configuration.getGroupId());
    helpDeskAiConfig.setAgentStreamId(configuration.getAgentStreamId());
    helpDeskAiConfig.setCloseTicketSuccessResponse(configuration.getCloseTicketSuccessResponse());
    helpDeskAiConfig.setAddMemberAgentSuccessResponse(configuration.getAddMemberAgentSuccessResponse());
    helpDeskAiConfig.setAddMemberClientSuccessResponse(configuration.getAddMemberClientSuccessResponse());
    helpDeskAiConfig.setAcceptTicketAgentSuccessResponse(configuration.getAcceptTicketAgentSuccessResponse());
    helpDeskAiConfig.setAcceptTicketClientSuccessResponse(configuration.getAcceptTicketClientSuccessResponse());
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
    agentMakerCheckerService.addCheck(new AgentExternalCheck(helpDeskBotSession.getSymphonyClient(),
        helpDeskBotSession.getTicketClient()));

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

  private MembershipClient initMembershipClient() {
    MembershipClient membershipClient = new MembershipClient(configuration.getGroupId(),
        configuration.getHelpDeskServiceUrl());

    return membershipClient;
  }

  private TicketClient initTicketClient() {
    TicketClient ticketClient = new TicketClient(configuration.getGroupId(),
        configuration.getHelpDeskServiceUrl());

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
    messageProxyServiceConfig.setHelpDeskBotHost(configuration.getHelpDeskBotUrl());
    messageProxyServiceConfig.setClaimEntityHeader(configuration.getClaimEntityHeader());

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
