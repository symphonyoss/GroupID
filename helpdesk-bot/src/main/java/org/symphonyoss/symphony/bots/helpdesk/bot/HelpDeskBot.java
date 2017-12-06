package org.symphonyoss.symphony.bots.helpdesk.bot;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.InitException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.symphony.bots.ai.HelpDeskAi;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.listener.AutoConnectionAcceptListener;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.session.HelpDeskBotSession;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.MakerCheckerService;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.MessageProxyService;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
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

  private final TicketClient ticketClient;

  private final MembershipClient membershipClient;

  private final HelpDeskAi helpDeskAi;

  private final MakerCheckerService agentMakerCheckerService;

  private final MakerCheckerService clientMakerCheckerService;

  private final MessageProxyService messageProxyService;

  private final AutoConnectionAcceptListener autoConnectionAcceptListener;

  private final HelpDeskBotSession helpDeskBotSession;

  /**
   * Constructor to inject dependencies.
   * @param configuration a configuration for the help desk bot.
   * @param symphonyClient
   * @param ticketClient
   * @param membershipClient
   * @param helpDeskAi
   * @param agentMakerCheckerService
   * @param clientMakerCheckerService
   * @param messageProxyService
   * @param autoConnectionAcceptListener
   */
  public HelpDeskBot(HelpDeskBotConfig configuration, SymphonyClient symphonyClient,
      TicketClient ticketClient, MembershipClient membershipClient, HelpDeskAi helpDeskAi,
      @Qualifier("agentMakerCheckerService") MakerCheckerService agentMakerCheckerService,
      @Qualifier("clientMakerCheckerService") MakerCheckerService clientMakerCheckerService,
      MessageProxyService messageProxyService,
      AutoConnectionAcceptListener autoConnectionAcceptListener) {
    this.configuration = configuration;
    this.symphonyClient = symphonyClient;
    this.ticketClient = ticketClient;
    this.membershipClient = membershipClient;
    this.helpDeskAi = helpDeskAi;
    this.agentMakerCheckerService = agentMakerCheckerService;
    this.clientMakerCheckerService = clientMakerCheckerService;
    this.messageProxyService = messageProxyService;
    this.autoConnectionAcceptListener = autoConnectionAcceptListener;

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
    String groupId = configuration.getGroupId();

    if (groupId == null) {
      throw new IllegalStateException("GroupId were not provided");
    }

    LOG.info("Help Desk Bot starting up for groupId: " + groupId);

    helpDeskBotSession.setHelpDeskBotConfig(configuration);
    helpDeskBotSession.setSymphonyClient(symphonyClient);
    helpDeskBotSession.setTicketClient(ticketClient);
    helpDeskBotSession.setMembershipClient(membershipClient);
    helpDeskBotSession.setHelpDeskAi(helpDeskAi);
    helpDeskBotSession.setAgentMakerCheckerService(agentMakerCheckerService);
    helpDeskBotSession.setClientMakerCheckerService(clientMakerCheckerService);
    helpDeskBotSession.setMessageProxyService(messageProxyService);
    helpDeskBotSession.setConnectionsEventListener(autoConnectionAcceptListener);

    registerDefaultAgent();

    LOG.info("Help Desk Bot startup complete fpr groupId: " + configuration.getGroupId());
  }

  private void registerDefaultAgent() {
    if(!StringUtils.isBlank(configuration.getDefaultAgentEmail())) {
      UsersClient userClient = symphonyClient.getUsersClient();
      try {
        SymUser symUser = userClient.getUserFromEmail(configuration.getDefaultAgentEmail());
        Membership membership = membershipClient.getMembership(symUser.getId());

        if(membership == null) {
          membershipClient.newMembership(symUser.getId(), MembershipClient.MembershipType.AGENT);
        } else if(!MembershipClient.MembershipType.AGENT.getType().equals(membership.getType())){
          membership.setType(MembershipClient.MembershipType.AGENT.getType());
          membershipClient.updateMembership(membership);
        }
      } catch (UsersClientException e) {
        throw new IllegalStateException("Error registering default agent user: ", e);
      }
    } else {
      throw new IllegalStateException("Bot email address were not provided");
    }
  }

  public HelpDeskBotSession getHelpDeskBotSession() {
    return helpDeskBotSession;
  }
}
