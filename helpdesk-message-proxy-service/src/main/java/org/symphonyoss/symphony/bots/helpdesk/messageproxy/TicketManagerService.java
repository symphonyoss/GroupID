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
import org.symphonyoss.client.model.Room;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.service.MembershipService;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.service.RoomService;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.service.TicketService;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.clients.model.SymMessage;

import javax.ws.rs.InternalServerErrorException;

/**
 * Created by rsanchez on 01/12/17.
 */
@Service
public class TicketManagerService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TicketManagerService.class);

  private static final int TICKET_ID_LENGTH = 10;

  private static final String SERVICE_ROOM_NOT_CREATED = "There was a problem trying to create the service room. Please try again.";

  private final String groupId;

  private final MembershipService membershipService;

  private final TicketService ticketService;

  private final RoomService roomService;

  private final MessageProxyService messageProxyService;

  private final String agentStreamId;

  public TicketManagerService(@Value("${agentStreamId}") String agentStreamId,
      @Value("${groupId}") String groupId,
      MembershipService membershipService, TicketService ticketService, RoomService roomService,
      MessageProxyService messageProxyService) {
    this.groupId = groupId;
    this.membershipService = membershipService;
    this.ticketService = ticketService;
    this.roomService = roomService;
    this.messageProxyService = messageProxyService;
    this.agentStreamId = agentStreamId;
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
        try {
          serviceStream = roomService.createServiceStream(ticketId, groupId);
        } catch (RoomException e) {
          ticketService.sendMessageWhenRoomCreationFails(message);
          throw new InternalServerErrorException(SERVICE_ROOM_NOT_CREATED);
        }

        ticket = ticketService.createTicket(ticketId, message, serviceStream);
      } else {
        LOGGER.info("Ticket already exists");
      }
      messageProxyService.onMessage(membership, ticket, message);
    } else {
      messageProxyService.onMessage(membership, ticket, message);
    }
  }

}
