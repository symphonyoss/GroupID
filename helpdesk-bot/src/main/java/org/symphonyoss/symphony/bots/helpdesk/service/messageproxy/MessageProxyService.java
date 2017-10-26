package org.symphonyoss.symphony.bots.helpdesk.service.messageproxy;

import org.symphonyoss.symphony.bots.ai.impl.AiSymphonyChatListener;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.helpdesk.common.BotConstants;
import org.symphonyoss.symphony.bots.helpdesk.model.HelpDeskBotSession;
import org.symphonyoss.symphony.bots.helpdesk.model.ai.HelpDeskAiSessionKey;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.MembershipService;
import org.symphonyoss.symphony.bots.helpdesk.service.messageproxy.model.MessageProxy;
import org.symphonyoss.symphony.bots.helpdesk.service.messageproxy.model.ProxyConversation;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.TicketService;

import org.apache.commons.lang.RandomStringUtils;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.services.MessageListener;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nick.tarsillo on 9/26/17.
 * The message proxy service handles the proxying of messages between clients and agents.
 */
public class MessageProxyService implements MessageListener {
  private Map<String, MessageProxy> proxyMap = new HashMap<>();

  private HelpDeskBotSession helpDeskBotSession;

  public MessageProxyService(HelpDeskBotSession helpDeskSession) {
    this.helpDeskBotSession = helpDeskSession;
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

    Membership membership = helpDeskBotSession.getMembershipService().getMembership(userId);
    if(membership == null) {
      membership = helpDeskBotSession.getMembershipService().newMembership(userId);
    }

    AiConversation aiConversation = helpDeskBotSession.getHelpDeskAi().getConversation(
        helpDeskBotSession.getHelpDeskAi().getSessionKey(userId, streamId));

    Ticket ticket = null;
    if(membership.getType().equals(MembershipService.MembershipType.AGENT)) {
      ticket = helpDeskBotSession.getTicketService().getTicketByServiceStreamId(streamId);
      if(ticket != null && !proxyMap.containsKey(ticket.getId())) {
        createAgentProxy(ticket, symMessage);
      } else if(ticket != null && aiConversation == null) {
        addAgentToProxy(ticket, symMessage);
      }
    } else {
      ticket = helpDeskBotSession.getTicketService().getTicketByClientStreamId(streamId);
      if (ticket == null) {
        ticket = helpDeskBotSession.getTicketService().createTicket(
            RandomStringUtils.randomAlphanumeric(BotConstants.TICKET_ID_LENGTH).toUpperCase(),
            streamId, symMessage.getMessageText());
        createClientProxy(ticket, symMessage);
      } else if(!proxyMap.containsKey(ticket.getId())) {
        createClientProxy(ticket, symMessage);
      } else if(ticket.getState().equals(TicketService.TicketStateType.UNSERVICED)) {
        ticket.addTranscriptItem(symMessage.getMessage());
        helpDeskBotSession.getTicketService().updateTicket(ticket);
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

    ProxyConversation aiConversation = new ProxyConversation(true, helpDeskBotSession.getAgentMakerCheckerService());
    aiConversation.addProxyId(ticket.getClientStreamId());
    helpDeskBotSession.getHelpDeskAi().startConversation(chatListener.getAiSessionKey(), aiConversation);

    proxyMap.put(ticket.getId(), new MessageProxy());
    proxyMap.get(ticket.getId()).addProxyConversation(aiConversation);
  }

  /**
   * If a proxy has already been created for the ticket, but the agent has not been mapped,
   * map the agent.
   * @param ticket the ticket to base the mapping on.
   * @param symMessage the message sent by the agent.
   */
  private void addAgentToProxy(Ticket ticket, SymMessage symMessage) {
    AiSymphonyChatListener chatListener = newAiSession(symMessage, HelpDeskAiSessionKey.SessionType.AGENT_SERVICE);

    ProxyConversation aiConversation = new ProxyConversation(true, helpDeskBotSession.getAgentMakerCheckerService());
    aiConversation.addProxyId(ticket.getServiceStreamId());
    helpDeskBotSession.getHelpDeskAi().startConversation(chatListener.getAiSessionKey(), aiConversation);

    proxyMap.get(ticket.getId()).addProxyConversation(aiConversation);
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

    ProxyConversation aiConversation = new ProxyConversation(false, helpDeskBotSession.getClientMakerCheckerService());
    aiConversation.addProxyId(ticket.getServiceStreamId());
    helpDeskBotSession.getHelpDeskAi().startConversation(chatListener.getAiSessionKey(), aiConversation);

    proxyMap.put(ticket.getId(), new MessageProxy());
    proxyMap.get(ticket.getId()).addProxyConversation(aiConversation);
  }

  /**
   * Creates a new AI session.
   * @param symMessage the message to base the session on
   * @param sessionType the help desk session type
   * @return a chat listener for the ai
   */
  private AiSymphonyChatListener newAiSession(SymMessage symMessage, HelpDeskAiSessionKey.SessionType sessionType) {
    Chat chat = helpDeskBotSession.getSymphonyClient().getChatService().getChatByStream(symMessage.getStreamId());
    AiSymphonyChatListener chatListener = helpDeskBotSession.getHelpDeskAi().createNewHelpDeskSession(
        symMessage.getFromUserId().toString(),
        symMessage.getStreamId(),
        helpDeskBotSession.getGroupId(),
        sessionType);
    chat.addListener(chatListener);

    return chatListener;
  }

}
