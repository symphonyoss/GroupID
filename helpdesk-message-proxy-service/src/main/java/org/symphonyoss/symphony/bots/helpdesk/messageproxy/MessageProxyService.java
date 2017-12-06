package org.symphonyoss.symphony.bots.helpdesk.messageproxy;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.exceptions.RoomException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.client.services.MessageListener;
import org.symphonyoss.symphony.bots.ai.AiResponseIdentifier;
import org.symphonyoss.symphony.bots.ai.HelpDeskAiSessionContext;
import org.symphonyoss.symphony.bots.ai.conversation.ProxyConversation;
import org.symphonyoss.symphony.bots.ai.conversation.ProxyIdleTimer;
import org.symphonyoss.symphony.bots.ai.impl.AiResponseIdentifierImpl;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiSessionKey;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.MessageProxyServiceConfig;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.model.ClaimEntityTemplateData;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.model.MessageProxy;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.model.MessageProxyServiceSession;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.model.UserInfo;
import org.symphonyoss.symphony.bots.utility.client.SymphonyUtilClient;
import org.symphonyoss.symphony.bots.utility.template.MessageTemplate;
import org.symphonyoss.symphony.clients.UsersClient;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymRoomAttributes;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by nick.tarsillo on 9/26/17.
 * The message proxy service handles the proxying of messages between clients and agents.
 */
public class MessageProxyService implements MessageListener {
  private static final Logger LOG = LoggerFactory.getLogger(MessageProxyService.class);

  public static final int TICKET_ID_LENGTH = 10;

  private Map<String, MessageProxy> proxyMap = new HashMap<>();

  private MessageProxyServiceSession session;

  public MessageProxyService(MessageProxyServiceSession messageProxyServiceSession) {
    this.session = messageProxyServiceSession;
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
    Long userId = symMessage.getFromUserId();
    String streamId = symMessage.getStreamId();

    Membership membership = session.getMembershipClient().getMembership(userId);

    if (membership == null) {
      membership = session.getMembershipClient()
          .newMembership(userId, MembershipClient.MembershipType.CLIENT);
      LOG.info("Created new client membership for userid: " + userId);
    } else {
      LOG.info("Found membership: " + membership.toString());
    }

    AiSessionKey aiSessionKey = session.getHelpDeskAi().getSessionKey(userId, streamId);
    HelpDeskAiSessionContext aiSessionContext =
        (HelpDeskAiSessionContext) session.getHelpDeskAi().getSessionContext(aiSessionKey);
    AiConversation aiConversation = session.getHelpDeskAi().getConversation(aiSessionKey);

    Ticket ticket;
    if (MembershipClient.MembershipType.AGENT.name().equals(membership.getType())) {
      ticket = session.getTicketClient().getTicketByServiceStreamId(streamId);
      if (ticket != null && !proxyMap.containsKey(ticket.getId())) {
        createAgentProxy(ticket, aiSessionContext);
      } else if (ticket != null && aiConversation == null) {
        addAgentToProxy(ticket, aiSessionContext);
      }
    } else {
      ticket = session.getTicketClient().getUnresolvedTicketByClientStreamId(streamId);
      if (ticket == null) {
        ticket = createTicket(symMessage);
        sendTicketCreationMessages(ticket);
        createClientProxy(ticket, aiSessionContext);
      } else if (!proxyMap.containsKey(ticket.getId())) {
        createClientProxy(ticket, aiSessionContext);
      }
    }
  }

  private Ticket createTicket(SymMessage symMessage) {
    String ticketId = RandomStringUtils.randomAlphanumeric(TICKET_ID_LENGTH).toUpperCase();

    try {
      UsersClient usersClient = session.getSymphonyClient().getUsersClient();
      SymUser symUser = usersClient.getUserFromId(symMessage.getFromUserId());
      UserInfo client = new UserInfo();
      client.setUserId(symUser.getId());
      client.setDisplayName(symUser.getDisplayName());

      return session.getTicketClient().createTicket(ticketId, symMessage.getStreamId(),
          newServiceStream(ticketId, symMessage.getStreamId()),
          Long.valueOf(symMessage.getTimestamp()),
          client);
    } catch (UsersClientException e) {
      LOG.error("Could not get sym user when creating ticket: ", e);
    }

    return null;
  }

  /**
   * If a agent talks in a client service room, but a proxy mapping does not exist, create a new
   * mapping based on the ticket.
   * @param ticket the ticket to base the mapping on.
   * @param aiSessionContext the ai session context
   */
  private void createAgentProxy(Ticket ticket, HelpDeskAiSessionContext aiSessionContext) {
    MessageProxyServiceConfig config = session.getMessageProxyServiceConfig();

    ProxyConversation aiConversation = new ProxyConversation(true,
        session.getAgentMakerCheckerService());
    aiConversation.addProxyId(ticket.getClientStreamId());

    session.getHelpDeskAi().startConversation(aiSessionContext.getAiSessionKey(), aiConversation, true);

    MessageProxy messageProxy = new MessageProxy();
    messageProxy.setAgentProxyTimer(new ProxyIdleTimer(config.getAgentIdleTimeValue(),
        config.getAgentIdleTimeUnit()) {
      @Override
      public void onIdleTimeout() {
        sendClaimMessage(ticket, true);
      }
    });
    messageProxy.getAgentProxyTimer().start();
    proxyMap.put(ticket.getId(), messageProxy);
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
    ProxyConversation aiConversation = new ProxyConversation(true,
        session.getClientMakerCheckerService());
    aiConversation.addProxyId(ticket.getClientStreamId());
    session.getHelpDeskAi().startConversation(aiSessionContext.getAiSessionKey(), aiConversation, true);

    MessageProxy messageProxy = proxyMap.get(ticket.getId());
    aiConversation.setProxyIdleTimer(messageProxy.getAgentProxyTimer());
    messageProxy.addProxyConversation(aiConversation);
  }

  /**
   * Creates a new proxy for the client. This includes:
   * Creating a new session with the help desk ai, and adding a new ai conversation.
   * Registering the proxy in the proxy map.
   */
  private void createClientProxy(Ticket ticket, HelpDeskAiSessionContext aiSessionContext) {
    MessageProxyServiceConfig config = session.getMessageProxyServiceConfig();
    ProxyConversation aiConversation =
        new ProxyConversation(false, session.getClientMakerCheckerService());
    aiConversation.addProxyId(ticket.getServiceStreamId());
    session.getHelpDeskAi().startConversation(aiSessionContext.getAiSessionKey(), aiConversation, true);

    MessageProxy messageProxy = new MessageProxy();
    messageProxy.setAgentProxyTimer(new ProxyIdleTimer(config.getAgentIdleTimeValue(),
        config.getAgentIdleTimeUnit()) {
      @Override
      public void onIdleTimeout() {
        sendClaimMessage(ticket, true);
      }
    });
    messageProxy.getAgentProxyTimer().start();
    proxyMap.put(ticket.getId(), messageProxy);
    proxyMap.get(ticket.getId()).addProxyConversation(aiConversation);
  }

  private void sendTicketCreationMessages(Ticket ticket) {
    SymphonyAiSessionKey sessionKey = (SymphonyAiSessionKey) session.getHelpDeskAi()
        .getSessionKey(ticket.getClient().getUserId(), ticket.getClientStreamId());

    SymphonyAiMessage aiMessage =
        new SymphonyAiMessage(session.getMessageProxyServiceConfig().getTicketCreationMessage());
    Set<AiResponseIdentifier> aiResponseIdentifierSet = new HashSet<>();
    aiResponseIdentifierSet.add(new AiResponseIdentifierImpl(ticket.getClientStreamId()));
    session.getHelpDeskAi()
        .sendMessage(aiMessage, aiResponseIdentifierSet, sessionKey);

    sendClaimMessage(ticket, false);
  }

  /**
   * Creates a new service stream for a ticket.
   * @param ticketId the ticket ID to create the service stream for
   * @param streamId the clients stream ID
   * @return the stream ID for the new service stream
   */
  private String newServiceStream(String ticketId, String streamId) {
    SymRoomAttributes roomAttributes = new SymRoomAttributes();
    roomAttributes.setCreatorUser(session.getSymphonyClient().getLocalUser());

    String users = "";
    try {
      for (SymUser symUser : session.getSymphonyClient()
          .getUsersClient()
          .getUsersFromStream(streamId)) {
        users += symUser.getFirstName() + " " + symUser.getLastName() + ", ";
      }
    } catch (UsersClientException e) {
      LOG.error("Could not retrieve names of users in stream: " + streamId, e);
    }
    users = users.substring(0, users.length() - 3);

    roomAttributes.setDescription("Service room for users " + users + ".");
    roomAttributes.setDiscoverable(false);
    roomAttributes.setMembersCanInvite(true);
    roomAttributes.setName(
        session.getMessageProxyServiceConfig().getGroupId() + " Ticket Room (" + ticketId + ")");
    roomAttributes.setReadOnly(false);
    roomAttributes.setPublic(false);

    Room room = null;
    try {
      room = session.getSymphonyClient().getRoomService().createRoom(roomAttributes);
      LOG.info("Created new room: " + roomAttributes.getName());
    } catch (RoomException e) {
      LOG.error("Create room failed: ", e);
    }

    return room.getStreamId();
  }

  private void sendClaimMessage(Ticket ticket, boolean isIdle) {
    SymphonyAiSessionKey sessionKey = (SymphonyAiSessionKey) session.getHelpDeskAi()
        .getSessionKey(ticket.getClient().getUserId(), ticket.getClientStreamId());

    SymphonyUtilClient utilClient = new SymphonyUtilClient(session.getSymphonyClient());
    String question =
        utilClient.getByTimestamp(ticket.getClientStreamId(), ticket.getQuestionTimestamp())
            .getMessageText();

    String message = getClaimMessageData();
    String entity = getClaimEntityData(ticket, ticket.getClient().getUserId(),
        question, isIdle);

    SymphonyAiMessage aiMessage = new SymphonyAiMessage(message);
    aiMessage.setEntityData(entity);
    Set<AiResponseIdentifier> aiResponseIdentifierSet = new HashSet<>();
    aiResponseIdentifierSet.add(
        new AiResponseIdentifierImpl(session.getMessageProxyServiceConfig().getAgentStreamId()));
    if(!isIdle) {
      aiResponseIdentifierSet.add(
          new AiResponseIdentifierImpl(ticket.getServiceStreamId()));
    }
    session.getHelpDeskAi().sendMessage(aiMessage, aiResponseIdentifierSet, sessionKey);
  }

  private String getClaimMessageData() {
    return session.getMessageProxyServiceConfig().getClaimMessageTemplate();
  }

  private String getClaimEntityData(Ticket ticket, Long fromUserId, String question, boolean isIdle) {
    try {
      UsersClient usersClient = session.getSymphonyClient().getUsersClient();
      SymUser symUser = usersClient.getUserFromId(fromUserId);

      String username = symUser.getDisplayName();
      String botHost = session.getMessageProxyServiceConfig().getHelpDeskBotHost();
      String serviceHost = session.getMessageProxyServiceConfig().getHelpDeskServiceHost();
      String agentStreamId = session.getMessageProxyServiceConfig().getAgentStreamId();

      String header = session.getMessageProxyServiceConfig().getClaimEntityHeader();
      if(isIdle) {
        header = session.getMessageProxyServiceConfig().getIdleClaimEntityHeader();
      }

      MessageTemplate entityTemplate = new MessageTemplate(
          session.getMessageProxyServiceConfig().getClaimEntityTemplate());
      ClaimEntityTemplateData entityTemplateData =
          new ClaimEntityTemplateData(ticket.getId(), ticket.getState(), username, botHost,
              serviceHost, agentStreamId, header, symUser.getCompany(),
              question);

      return entityTemplate.buildFromData(entityTemplateData);
    } catch (UsersClientException e) {
      LOG.error("Could not find user when creating claim message: ", e);
    }

    return null;
  }
}