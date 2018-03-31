package org.symphonyoss.symphony.bots.helpdesk.messageproxy;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.symphonyoss.symphony.bots.ai.helpdesk.HelpDeskAi;
import org.symphonyoss.symphony.bots.ai.helpdesk.conversation.IdleTimerManager;
import org.symphonyoss.symphony.bots.ai.helpdesk.conversation.ProxyConversation;
import org.symphonyoss.symphony.bots.ai.helpdesk.conversation.ProxyIdleTimer;
import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.ai.model.SymphonyAiSessionKey;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.MakerCheckerService;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.IdleTicketConfig;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by nick.tarsillo on 9/26/17.
 * The message proxy service handles the proxying of messages between clients and agents.
 */
@Service
public class MessageProxyService {

  private final Set<String> clientConversations = new HashSet<>();

  private final Set<String> agentConversations = new HashSet<>();

  private final HelpDeskAi helpDeskAi;

  private final MakerCheckerService agentMakerCheckerService;

  private final MakerCheckerService clientMakerCheckerService;

  private final IdleTicketConfig idleTicketConfig;

  private final IdleTimerManager idleTimerManager;

  private final IdleMessageService idleMessageService;

  public MessageProxyService(HelpDeskAi helpDeskAi,
      @Qualifier("agentMakerCheckerService") MakerCheckerService agentMakerCheckerService,
      @Qualifier("clientMakerCheckerService") MakerCheckerService clientMakerCheckerService,
      IdleTicketConfig idleTicketConfig,
      IdleTimerManager idleTimerManager,
      IdleMessageService idleMessageService) {
    this.helpDeskAi = helpDeskAi;
    this.agentMakerCheckerService = agentMakerCheckerService;
    this.clientMakerCheckerService = clientMakerCheckerService;
    this.idleTicketConfig = idleTicketConfig;
    this.idleTimerManager = idleTimerManager;
    this.idleMessageService = idleMessageService;
  }

  /**
   * On message:
   * Get AiConversation for the user that sent the message.
   *
   * If the member is an agent, the agent is talking in a client service room
   * and a proxy has not been created yet, create a new proxy conversation for ticket.
   *
   * If the member is an agent, the agent is talking in a client service room
   * and a proxy has already been created, add the agent to the proxy.
   *
   * If the member is a client, and a ticket has not been created, create a new ticket and
   * proxy.
   *
   * If the member is a client, and ticket exists but not a proxy conversation, create a new proxy.
   *
   * If the member is a client, and the ticket and proxy exists with the unserviced state,
   * update the ticket transcript.
   * @param symMessage the message to proxy.
   */
  public void onMessage(Membership membership, Ticket ticket, SymMessage symMessage) {
    Long userId = symMessage.getFromUserId();
    String streamId = symMessage.getStreamId();

    SymphonyAiSessionKey aiSessionKey = helpDeskAi.getSessionKey(userId, streamId);

    if (MembershipClient.MembershipType.AGENT.name().equals(membership.getType())) {
      AiConversation aiConversation = helpDeskAi.getConversation(aiSessionKey);

      if (ticket != null && !agentConversations.contains(ticket.getId())) {
        createAgentProxy(ticket, aiSessionKey);
      } else if (ticket != null && aiConversation == null) {
        addAgentToProxy(ticket, aiSessionKey);
      }
    } else if ((ticket != null) && (!clientConversations.contains(ticket.getId()))) {
      createClientProxy(ticket, aiSessionKey);
    }
  }

  /**
   * If a agent talks in a client service room, but a proxy mapping does not exist, create a new
   * mapping based on the ticket.
   * @param ticket the ticket to base the mapping on.
   * @param aiSessionKey session key
   */
  private void createAgentProxy(Ticket ticket, SymphonyAiSessionKey aiSessionKey) {
    AiCommandMenu aiCommandMenu = helpDeskAi.newAiCommandMenu(aiSessionKey);

    ProxyConversation aiConversation = new ProxyConversation(aiCommandMenu, agentMakerCheckerService);
    aiConversation.addProxyId(ticket.getClientStreamId());

    helpDeskAi.startConversation(aiSessionKey, aiConversation);

    idleTimerManager.put(ticket.getId(), (new ProxyIdleTimer(idleTicketConfig.getTimeout(),
        idleTicketConfig.getUnit()) {
      @Override
      public void onIdleTimeout() {
        idleMessageService.sendIdleMessage(ticket);
      }
    }));

    agentConversations.add(ticket.getId());
  }

  /**
   * If a proxy has already been created for the ticket, but the agent has not been mapped,
   * map the agent.
   * @param ticket the ticket to base the mapping on.
   * @param aiSessionKey session key
   */
  private void addAgentToProxy(Ticket ticket, SymphonyAiSessionKey aiSessionKey) {
    AiCommandMenu aiCommandMenu = helpDeskAi.newAiCommandMenu(aiSessionKey);

    ProxyConversation aiConversation = new ProxyConversation(aiCommandMenu, agentMakerCheckerService);
    aiConversation.addProxyId(ticket.getClientStreamId());

    helpDeskAi.startConversation(aiSessionKey, aiConversation);

    ProxyIdleTimer proxyIdleTimer = idleTimerManager.get(ticket.getId());
    aiConversation.setProxyIdleTimer(proxyIdleTimer);
  }

  private void createClientProxy(Ticket ticket, SymphonyAiSessionKey aiSessionKey) {
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