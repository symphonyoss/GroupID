package org.symphonyoss.symphony.bots.helpdesk.messageproxy;

import static org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient
    .MembershipType.AGENT;
import static org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient
    .MembershipType.CLIENT;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.RestException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.service.MembershipService;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.service.RoomService;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.service.TicketService;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.pod.api.UsersApi;
import org.symphonyoss.symphony.pod.invoker.ApiClient;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.invoker.Configuration;
import org.symphonyoss.symphony.pod.model.UserV2;

import javax.ws.rs.BadRequestException;

/**
 * Created by rsanchez on 01/12/17.
 */
@Service
public class TicketManagerService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TicketManagerService.class);

  private static final int TICKET_ID_LENGTH = 10;

  private final String groupId;

  private final MembershipService membershipService;

  private final TicketService ticketService;

  private final RoomService roomService;

  private final MessageProxyService messageProxyService;

  private final String agentStreamId;

  private final SymphonyClient symphonyClient;

  private final ApiClient apiClient;

  private final UsersApi usersApi;


  public TicketManagerService(@Value("${agentStreamId}") String agentStreamId,
      @Value("${groupId}") String groupId,
      MembershipService membershipService, TicketService ticketService, RoomService roomService,
      MessageProxyService messageProxyService,
      SymphonyClient symphonyClient) {
    this.groupId = groupId;
    this.membershipService = membershipService;
    this.ticketService = ticketService;
    this.roomService = roomService;
    this.messageProxyService = messageProxyService;
    this.agentStreamId = agentStreamId;
    this.symphonyClient = symphonyClient;
    this.apiClient = Configuration.getDefaultApiClient();
    this.usersApi = new UsersApi(apiClient);
  }

  /**
   * On message:
   * Check if the message was sent in a ticket room or in a queue room.
   * Check if membership exists, if exists and he is an agent, update him, if not, create membership
   * according to the type.
   * Check if ticket exists, if not, create service room and ticket. Also sends ticket message
   * to agent stream id
   * @param message the message to proxy.
   */
  public void messageReceived(SymMessage message) {
    Membership membership;
    String podName = null;

    Ticket ticket = ticketService.getTicketByServiceStreamId(message.getStreamId());
    boolean external = false;

    if (message.getStreamId().equals(agentStreamId) || ticket != null) {
      membership = membershipService.updateMembership(message, AGENT);
    } else {
      membership = membershipService.updateMembership(message, CLIENT);
    }

    if (CLIENT.getType().equals(membership.getType())) {
      ticket = ticketService.getUnresolvedTicket(message.getStreamId());

      if (ticket == null) {
        String ticketId = RandomStringUtils.randomAlphanumeric(TICKET_ID_LENGTH).toUpperCase();

        boolean user = isExternal(message);

        if (user == true) {
          podName = getPodNameFromExternalUser(message);
        }

        Room serviceStream = roomService.createServiceStream(ticketId, groupId, podName);

        ticket = ticketService.createTicket(ticketId, message, serviceStream);
      } else {
        LOGGER.info("Ticket already exists");
      }
      messageProxyService.onMessage(membership, ticket, message);
    } else {
      messageProxyService.onMessage(membership, ticket, message);
    }
  }

  private String getPodNameFromExternalUser(SymMessage message) {
    SymAuth symAuth = symphonyClient.getSymAuth();
    UserV2 user;
    try {
      user =
          usersApi.v2UserGet(symAuth.getSessionToken().getToken(), message.getSymUser().getId(),
              null, null, false);
    } catch (ApiException e) {
      try {
        throw new UsersClientException("API Error communicating with POD, while retrieving user details for " + message.getSymUser().getId(),
            new RestException(usersApi.getApiClient().getBasePath(), e.getCode(), e));
      } catch (UsersClientException e1) {
        throw new BadRequestException("User " + message.getSymUser().getId() + " not found");
      }
    }
      return user != null ? user.getCompany() : null;
  }

  public boolean isExternal(SymMessage message) {
    SymAuth symAuth = symphonyClient.getSymAuth();
    UserV2 user;
    try {
      user =
          usersApi.v2UserGet(symAuth.getSessionToken().getToken(), message.getSymUser().getId(),
              null, null, true);
    } catch (ApiException e) {
      try {
        throw new UsersClientException("API Error communicating with POD, while retrieving user details for " + message.getSymUser().getId(),
            new RestException(usersApi.getApiClient().getBasePath(), e.getCode(), e));
      } catch (UsersClientException e1) {
        throw new BadRequestException("User " + message.getSymUser().getId() + " not found");
      }
    }
    return user == null;
  }

}
