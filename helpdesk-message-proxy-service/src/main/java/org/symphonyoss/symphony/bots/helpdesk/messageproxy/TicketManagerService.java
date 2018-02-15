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
import org.symphonyoss.client.exceptions.RoomException;
import org.symphonyoss.client.SymphonyClient;
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

import javax.ws.rs.InternalServerErrorException;

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

    ApiClient apiClient = Configuration.getDefaultApiClient();
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

    Ticket ticket = ticketService.getTicketByServiceStreamId(message.getStreamId());

    if (message.getStreamId().equals(agentStreamId) || ticket != null) {
      membership = membershipService.updateMembership(message, AGENT);
    } else {
      membership = membershipService.updateMembership(message, CLIENT);
    }

    if (CLIENT.getType().equals(membership.getType())) {
      ticket = ticketService.getUnresolvedTicket(message.getStreamId());

      if (ticket == null) {
        String ticketId = RandomStringUtils.randomAlphanumeric(TICKET_ID_LENGTH).toUpperCase();
        Room serviceStream = null;
        Long userId = message.getSymUser().getId();

        try {
          if (isExternal(userId)) {
            String podName = getPodNameFromExternalUser(userId);
            serviceStream = roomService.createServiceStream(ticketId, groupId, podName);
          } else {
            serviceStream = roomService.createServiceStream(ticketId, groupId);
          }

          ticket = ticketService.createTicket(ticketId, message, serviceStream);
        } catch (RoomException e) {
          ticketService.sendMessageWhenRoomCreationFails(message);
        }
      } else {
        LOGGER.info("Ticket already exists");
      }
      messageProxyService.onMessage(membership, ticket, message);
    } else {
      messageProxyService.onMessage(membership, ticket, message);
    }
  }

  /**
   * Retrieves the POD name from external users.
   *
   * @param userId User ID
   * @return POD name
   * @throws InternalServerErrorException Failure to retrieve user info
   */
  private String getPodNameFromExternalUser(Long userId) {
    SymAuth symAuth = symphonyClient.getSymAuth();
    String sessionToken = symAuth.getSessionToken().getToken();

    try {
      UserV2 user = usersApi.v2UserGet(sessionToken, userId, null, null, false);
      return user != null ? user.getCompany() : null;
    } catch (ApiException e) {
      LOGGER.error("API Error communicating with POD, while retrieving user details for " +
          userId + " to get the company name.", e);
      throw new InternalServerErrorException("User " + userId + " not found");
    }
  }

  /**
   * Checks if the user is external.
   *
   * @param userId User ID
   * @return true if the user is external or false otherwise.
   * @throws InternalServerErrorException Failure to retrieve user info
   */
  private boolean isExternal(Long userId) {
    SymAuth symAuth = symphonyClient.getSymAuth();
    String sessionToken = symAuth.getSessionToken().getToken();

    try {
      UserV2 user = usersApi.v2UserGet(sessionToken, userId, null, null, true);
      return user == null;
    } catch (ApiException e) {
      LOGGER.error("API Error communicating with POD, while retrieving user details for " +
          userId, e);
      throw new InternalServerErrorException("User " + userId + " not found");
    }
  }

}
