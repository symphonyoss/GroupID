package org.symphonyoss.symphony.bots.helpdesk.messageproxy;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.symphonyoss.symphony.bots.ai.HelpDeskAi;
import org.symphonyoss.symphony.bots.ai.HelpDeskAiSessionContext;
import org.symphonyoss.symphony.bots.ai.conversation.ProxyConversation;
import org.symphonyoss.symphony.bots.ai.conversation.ProxyIdleTimer;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.MakerCheckerService;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.HelpDeskBotInfo;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.HelpDeskServiceInfo;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.IdleTicketConfig;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.message.IdleMessageBuilder;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.model.MessageProxy;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.service.TicketService;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nick.tarsillo on 9/26/17.
 * The message proxy service handles the proxying of messages between clients and agents.
 */
@Service
public class MessageProxyService {

  private final Map<String, MessageProxy> clientProxy = new HashMap<>();

  private final Map<String, MessageProxy> agentProxy = new HashMap<>();

  private final HelpDeskAi helpDeskAi;

  private final MakerCheckerService agentMakerCheckerService;

  private final MakerCheckerService clientMakerCheckerService;

  private final IdleTicketConfig idleTicketConfig;

  private final TicketService ticketService;

  private final HelpDeskBotInfo helpDeskBotInfo;

  private final HelpDeskServiceInfo helpDeskServiceInfo;

  private final String agentStreamId;

  public MessageProxyService(HelpDeskAi helpDeskAi,
      @Qualifier("agentMakerCheckerService") MakerCheckerService agentMakerCheckerService,
      @Qualifier("clientMakerCheckerService") MakerCheckerService clientMakerCheckerService,
      IdleTicketConfig idleTicketConfig, TicketService ticketService,
      HelpDeskBotInfo helpDeskBotInfo, HelpDeskServiceInfo helpDeskServiceInfo,
      @Value("${agentStreamId}") String agentStreamId) {
    this.helpDeskAi = helpDeskAi;
    this.agentMakerCheckerService = agentMakerCheckerService;
    this.clientMakerCheckerService = clientMakerCheckerService;
    this.idleTicketConfig = idleTicketConfig;
    this.ticketService = ticketService;
    this.helpDeskBotInfo = helpDeskBotInfo;
    this.helpDeskServiceInfo = helpDeskServiceInfo;
    this.agentStreamId = agentStreamId;
  }

  /**
   * On message:
   *    Get AiConversation for the user that sent the message.
   *
   *    If the member is an agent, the agent is talking in a client service room
   *      and a proxy has not been created yet, create a new proxy conversation for ticket.
   *
   *    If the member is an agent, the agent is talking in a client service room
   *      and a proxy has already been created, add the agent to the proxy.
   *
   *    If the member is a client, and a ticket has not been created, create a new ticket and
   *      proxy.
   *
   *    If the member is a client, and ticket exists but not a proxy conversation, create a new proxy.
   *
   *    If the member is a client, and the ticket and proxy exists with the unserviced state,
   *      update the ticket transcript.
   *
   * @param symMessage the message to proxy.
   */
  public void onMessage(Membership membership, Ticket ticket, SymMessage symMessage) {
    Long userId = symMessage.getFromUserId();
    String streamId = symMessage.getStreamId();

    AiSessionKey aiSessionKey = helpDeskAi.getSessionKey(userId, streamId);

    if (MembershipClient.MembershipType.AGENT.name().equals(membership.getType())) {
      AiConversation aiConversation = helpDeskAi.getConversation(aiSessionKey);
      HelpDeskAiSessionContext aiSessionContext = (HelpDeskAiSessionContext) helpDeskAi.getSessionContext(aiSessionKey);

      if (ticket != null && !agentProxy.containsKey(ticket.getId())) {
        createAgentProxy(ticket, aiSessionContext);
      } else if (ticket != null && aiConversation == null) {
        addAgentToProxy(ticket, aiSessionContext);
      }
    } else if ((ticket != null) && (!clientProxy.containsKey(ticket.getId()))) {
      HelpDeskAiSessionContext aiSessionContext =
          (HelpDeskAiSessionContext) helpDeskAi.getSessionContext(aiSessionKey);
      createClientProxy(ticket, aiSessionContext);
    }
  }

  /**
   * If a agent talks in a client service room, but a proxy mapping does not exist, create a new
   * mapping based on the ticket.
   * @param ticket the ticket to base the mapping on.
   * @param aiSessionContext the ai session context
   */
  private void createAgentProxy(Ticket ticket, HelpDeskAiSessionContext aiSessionContext) {
    ProxyConversation aiConversation = new ProxyConversation(true, agentMakerCheckerService);
    aiConversation.addProxyId(ticket.getClientStreamId());

    helpDeskAi.startConversation(aiSessionContext.getAiSessionKey(), aiConversation);

    MessageProxy messageProxy = new MessageProxy();
    messageProxy.setAgentProxyTimer(new ProxyIdleTimer(idleTicketConfig.getTimeout(),
        idleTicketConfig.getUnit()) {
      @Override
      public void onIdleTimeout() {
        sendIdleMessage(ticket);
      }
    });
    messageProxy.getAgentProxyTimer().start();

    agentProxy.put(ticket.getId(), messageProxy);
    aiConversation.setProxyIdleTimer(messageProxy.getAgentProxyTimer());
    messageProxy.addProxyConversation(aiConversation);
  }

  /**
   * If a proxy has already been created for the ticket, but the agent has not been mapped,
   * map the agent.
   * @param ticket the ticket to base the mapping on.
   * @param aiSessionContext the ai session context
   */
  private void addAgentToProxy(Ticket ticket, HelpDeskAiSessionContext aiSessionContext) {
    ProxyConversation aiConversation = new ProxyConversation(true, agentMakerCheckerService);
    aiConversation.addProxyId(ticket.getClientStreamId());
    helpDeskAi.startConversation(aiSessionContext.getAiSessionKey(), aiConversation);

    MessageProxy messageProxy = agentProxy.get(ticket.getId());
    aiConversation.setProxyIdleTimer(messageProxy.getAgentProxyTimer());
    messageProxy.addProxyConversation(aiConversation);
  }

  /**
   * Creates a new proxy for the client. This includes:
   * Creating a new session with the help desk ai, and adding a new ai conversation.
   * Registering the proxy in the proxy map.
   */
  private void createClientProxy(Ticket ticket, HelpDeskAiSessionContext aiSessionContext) {
    ProxyConversation aiConversation = new ProxyConversation(false, clientMakerCheckerService);
    aiConversation.addProxyId(ticket.getServiceStreamId());

    helpDeskAi.startConversation(aiSessionContext.getAiSessionKey(), aiConversation);

    MessageProxy messageProxy = new MessageProxy();
    messageProxy.setAgentProxyTimer(new ProxyIdleTimer(idleTicketConfig.getTimeout(),
        idleTicketConfig.getUnit()) {
      @Override
      public void onIdleTimeout() {
        sendIdleMessage(ticket);
      }
    });
    messageProxy.getAgentProxyTimer().start();

    clientProxy.put(ticket.getId(), messageProxy);
    messageProxy.addProxyConversation(aiConversation);
  }

  private void sendIdleMessage(Ticket ticket) {
    String safeAgentStreamId = Base64.encodeBase64String(Base64.decodeBase64(agentStreamId));

    SymMessage message = new IdleMessageBuilder()
        .message(idleTicketConfig.getMessage())
        .ticketState(ticket.getState())
        .botHost(helpDeskBotInfo.getUrl())
        .serviceHost(helpDeskServiceInfo.getUrl())
        .ticketId(ticket.getId())
        .streamId(safeAgentStreamId)
        .build();

    ticketService.sendIdleMessageToAgentStreamId(message);
  }
  
}