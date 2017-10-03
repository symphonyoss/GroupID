package com.symphony.bots.helpdesk.service.messageproxy;

import com.symphony.api.helpdesk.service.model.Membership;
import com.symphony.api.helpdesk.service.model.Ticket;
import com.symphony.bots.ai.impl.AiSymphonyChatListener;
import com.symphony.bots.ai.model.AiConversation;
import com.symphony.bots.helpdesk.model.HelpDeskBotSession;
import com.symphony.bots.helpdesk.model.ai.HelpDeskAi;
import com.symphony.bots.helpdesk.model.ai.HelpDeskAiSessionKey;
import com.symphony.bots.helpdesk.service.makerchecker.MakerCheckerService;
import com.symphony.bots.helpdesk.service.membership.MembershipService;
import com.symphony.bots.helpdesk.service.messageproxy.model.ProxyConversation;
import com.symphony.bots.helpdesk.service.ticket.TicketService;

import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.services.ChatService;
import org.symphonyoss.client.services.MessageListener;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by nick.tarsillo on 9/26/17.
 * The message proxy service handles the proxying of messages between clients and agents.
 */
public class MessageProxyService implements MessageListener {
  private Map<String, Set<ProxyConversation>> proxyMap = new HashMap<>();

  private HelpDeskAi helpDeskAi;

  private MakerCheckerService agentMakerCheckerService;
  private MakerCheckerService clientMakerCheckerService;
  private MembershipService membershipService;
  private TicketService ticketService;

  private ChatService chatService;

  public MessageProxyService(HelpDeskBotSession helpDeskSession) {
    helpDeskAi = helpDeskSession.getHelpDeskAi();
    agentMakerCheckerService = helpDeskSession.getAgentMakerCheckerService();
    clientMakerCheckerService = helpDeskSession.getClientMakerCheckerService();
    membershipService = helpDeskSession.getMembershipService();
    ticketService = helpDeskSession.getTicketService();
    chatService = helpDeskSession.getSymphonyClient().getChatService();
  }

  /**
   * On message:
   *    Check if membership exits, if not, create membership. (CLIENT)
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
  @Override
  public void onMessage(SymMessage symMessage) {
    String userId = symMessage.getFromUserId().toString();
    String streamId = symMessage.getStreamId();

    Membership membership = membershipService.getMembership(userId);
    if(membership == null) {
      membership = membershipService.newMembership(userId);
    }

    AiConversation aiConversation = helpDeskAi.getConversation(
        helpDeskAi.getSessionKey(userId, streamId));

    Ticket ticket = null;
    if(membership.getType().equals(MembershipService.MembershipType.AGENT)) {
      ticket = ticketService.getTicketByServiceRoomId(streamId);
      if(ticket != null && !proxyMap.containsKey(ticket.getId())) {
        createAgentProxy(ticket, symMessage);
      } else if(ticket != null && aiConversation == null) {
        addAgentToProxy(ticket, symMessage);
      }
    } else {
      ticket = ticketService.getTicket(streamId);
      if (ticket == null) {
        ticket = ticketService.createTicket(streamId, streamId, symMessage.getMessageText());
        createClientProxy(ticket, symMessage);
      } else if(!proxyMap.containsKey(ticket.getId())) {
        createClientProxy(ticket, symMessage);
      } else if(ticket.getState().equals(TicketService.TicketStateType.UNSERVICED)) {
        ticket.addTranscriptItem(symMessage.getMessage());
        ticketService.updateTicket(ticket);
      }
    }
  }

  /**
   * If a agent talks in a client service room, but a proxy mapping does not exist, create a new
   * mapping based on the ticket.
   * @param ticket the ticket to base the mapping on.
   * @param symMessage the message sent by the agent.
   */
  private void createAgentProxy(Ticket ticket, SymMessage symMessage) {
    AiSymphonyChatListener chatListener = newAiSession(symMessage, HelpDeskAiSessionKey.SessionType.AGENT_SERVICE);

    ProxyConversation aiConversation = new ProxyConversation(true, agentMakerCheckerService);
    aiConversation.addProxyId(ticket.getClientStreamId());
    helpDeskAi.startConversation(chatListener.getAiSessionKey(), aiConversation);

    proxyMap.put(ticket.getId(), new HashSet<>());
    proxyMap.get(ticket.getId()).add(aiConversation);
  }

  /**
   * If a proxy has already been created for the ticket, but the agent has not been mapped,
   * map the agent.
   * @param ticket the ticket to base the mapping on.
   * @param symMessage the message sent by the agent.
   */
  private void addAgentToProxy(Ticket ticket, SymMessage symMessage) {
    AiSymphonyChatListener chatListener = newAiSession(symMessage, HelpDeskAiSessionKey.SessionType.AGENT_SERVICE);

    ProxyConversation aiConversation = new ProxyConversation(true, agentMakerCheckerService);
    aiConversation.addProxyId(ticket.getServiceStreamId());
    helpDeskAi.startConversation(chatListener.getAiSessionKey(), aiConversation);

    proxyMap.get(ticket.getId()).add(aiConversation);
  }

  /**
   * Creates a new proxy for the client. This includes:
   *    Creating a new session with the help desk ai, and adding a new ai conversation.
   *    Registering the proxy in the proxy map.
   * @param ticket
   * @param symMessage
   */
  private void createClientProxy(Ticket ticket, SymMessage symMessage) {
    AiSymphonyChatListener chatListener = newAiSession(symMessage, HelpDeskAiSessionKey.SessionType.CLIENT);

    ProxyConversation aiConversation = new ProxyConversation(false, clientMakerCheckerService);
    aiConversation.addProxyId(ticket.getServiceStreamId());
    helpDeskAi.startConversation(chatListener.getAiSessionKey(), aiConversation);

    proxyMap.put(ticket.getId(), new HashSet<>());
    proxyMap.get(ticket.getId()).add(aiConversation);
  }

  /**
   * Creates a new AI session.
   * @param symMessage the message to base the session on
   * @param sessionType the help desk session type
   * @return a chat listener for the ai
   */
  private AiSymphonyChatListener newAiSession(SymMessage symMessage, HelpDeskAiSessionKey.SessionType sessionType) {
    Chat chat = chatService.getChatByStream(symMessage.getStreamId());
    AiSymphonyChatListener chatListener = helpDeskAi.createNewHelpDeskSession(symMessage.getFromUserId(),
        symMessage.getStreamId(), sessionType);
    chat.addListener(chatListener);

    return chatListener;
  }

}
