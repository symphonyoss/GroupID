package org.symphonyoss.symphony.bots.helpdesk.messageproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.ai.helpdesk.HelpDeskAi;
import org.symphonyoss.symphony.bots.ai.helpdesk.conversation.IdleTimerManager;
import org.symphonyoss.symphony.bots.ai.helpdesk.conversation.ProxyConversation;
import org.symphonyoss.symphony.bots.ai.helpdesk.conversation.ProxyIdleTimer;
import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.MakerCheckerService;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.IdleTicketConfig;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.HashSet;
import java.util.Set;

/**
 * The message proxy service handles the proxying of messages between clients and agents.
 * Created by nick.tarsillo on 9/26/17.
 */
@Service
public class MessageProxyService {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageProxyService.class);

  private final Set<AiSessionKey> agentConversations = new HashSet<>();

  private final Set<String> clientConversations = new HashSet<>();

  private final HelpDeskAi helpDeskAi;

  private final MakerCheckerService agentMakerCheckerService;

  private final MakerCheckerService clientMakerCheckerService;

  private final IdleTicketConfig idleTicketConfig;

  private final IdleTimerManager idleTimerManager;

  private final IdleMessageService idleMessageService;

  private final SymphonyClient symphonyClient;

  public MessageProxyService(HelpDeskAi helpDeskAi,
      @Qualifier("agentMakerCheckerService") MakerCheckerService agentMakerCheckerService,
      @Qualifier("clientMakerCheckerService") MakerCheckerService clientMakerCheckerService,
      IdleTicketConfig idleTicketConfig,
      IdleTimerManager idleTimerManager,
      IdleMessageService idleMessageService,
      SymphonyClient symphonyClient) {
    this.helpDeskAi = helpDeskAi;
    this.agentMakerCheckerService = agentMakerCheckerService;
    this.clientMakerCheckerService = clientMakerCheckerService;
    this.idleTicketConfig = idleTicketConfig;
    this.idleTimerManager = idleTimerManager;
    this.idleMessageService = idleMessageService;
    this.symphonyClient = symphonyClient;
  }

  /**
   * On message:
   * Get AiConversation for the user that sent the message.
   *
   * If the member is an agent, the agent is talking in a client service room
   * and a proxy has not been created yet, create a new proxy conversation for ticket.
   *
   * If the member is a client, and a proxy has not been created yet, create a new proxy conversation
   * for ticket.
   *
   * @param membership User info
   * @param ticket Ticket information
   * @param symMessage the message to proxy.
   */
  public void onMessage(Membership membership, Ticket ticket, SymMessage symMessage) {
    Long userId = symMessage.getFromUserId();
    String streamId = symMessage.getStreamId();

    if (ticket == null) {
      LOGGER.warn("Ticket was not created");
      return;
    }

    AiSessionKey aiSessionKey = helpDeskAi.getSessionKey(userId, streamId);

    createConversation(ticket, aiSessionKey, membership);
  }

  /**
   * Create proxy conversation if required.
   *
   * @param ticket Ticket information
   * @param aiSessionKey Session key
   * @param membership User info
   */
  private void createConversation(Ticket ticket, AiSessionKey aiSessionKey, Membership membership) {
    if (isAgentUser(membership) && shouldCreateAgentConversation(aiSessionKey, membership)) {
      createAgentProxy(ticket, aiSessionKey);
    } else if (shouldCreateClientConversation(ticket)) {
      createClientProxy(ticket, aiSessionKey);
    }
  }

  /**
   * Check if should create a new conversation for agent user.
   *
   * @param aiSessionKey Session key
   * @param membership User info
   * @return true if the conversation does not exist
   */
  private boolean shouldCreateAgentConversation(AiSessionKey aiSessionKey, Membership membership) {
    return !agentConversations.contains(aiSessionKey) && !isBotUser(membership);
  }

  /**
   * Check if should create a new conversation for client user.
   *
   * @param ticket Ticket information
   * @return true if the conversation does not exist
   */
  private boolean shouldCreateClientConversation(Ticket ticket) {
    return !clientConversations.contains(ticket.getId());
  }

  /**
   * Check if the group member is an agent.
   *
   * @param membership User info
   * @return true if the group member is an agent.
   */
  private boolean isAgentUser(Membership membership) {
    return MembershipClient.MembershipType.AGENT.name().equals(membership.getType());
  }

  /**
   * Check if the group member is the bot user.
   *
   * @param membership User info
   * @return true if the group member is the bot user.
   */
  private boolean isBotUser(Membership membership) {
    Long botUserId = symphonyClient.getLocalUser().getId();
    return membership.getId().equals(botUserId);
  }

  /**
   * If a agent talks in a service room, but a proxy mapping does not exist, create a new
   * mapping based on the ticket.
   * @param ticket the ticket to base the mapping on.
   * @param aiSessionKey session key
   */
  private void createAgentProxy(Ticket ticket, AiSessionKey aiSessionKey) {
    AiCommandMenu aiCommandMenu = helpDeskAi.newAiCommandMenu(aiSessionKey);

    ProxyConversation aiConversation = new ProxyConversation(aiCommandMenu, agentMakerCheckerService);
    aiConversation.addProxyId(ticket.getClientStreamId());

    helpDeskAi.startConversation(aiSessionKey, aiConversation);

    ProxyIdleTimer proxyIdleTimer = idleTimerManager.get(ticket.getId());

    if (proxyIdleTimer == null) {
      proxyIdleTimer = new ProxyIdleTimer(idleTicketConfig.getTimeout(),
          idleTicketConfig.getUnit()) {
        @Override
        public void onIdleTimeout() {
          idleMessageService.sendIdleMessage(ticket);
        }
      };

      idleTimerManager.put(ticket.getId(), proxyIdleTimer);
    }

    aiConversation.setProxyIdleTimer(proxyIdleTimer);

    agentConversations.add(aiSessionKey);
  }

  /**
   * If a client talks in a client room, but a proxy mapping does not exist, create a new
   * mapping based on the ticket.
   * @param ticket the ticket to base the mapping on.
   * @param aiSessionKey session key
   */
  private void createClientProxy(Ticket ticket, AiSessionKey aiSessionKey) {
    ProxyConversation aiConversation = new ProxyConversation(clientMakerCheckerService);
    aiConversation.addProxyId(ticket.getServiceStreamId());

    helpDeskAi.startConversation(aiSessionKey, aiConversation);

    idleTimerManager.put(ticket.getId(), (new ProxyIdleTimer(idleTicketConfig.getTimeout(),
        idleTicketConfig.getUnit()) {
      @Override
      public void onIdleTimeout() {
        idleMessageService.sendIdleMessage(ticket);
      }
    }));

    clientConversations.add(ticket.getId());
  }
}