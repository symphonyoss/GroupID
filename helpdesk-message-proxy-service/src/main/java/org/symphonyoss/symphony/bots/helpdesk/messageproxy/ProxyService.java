package org.symphonyoss.symphony.bots.helpdesk.messageproxy;

import static org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient.MembershipType.CLIENT;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.service.MembershipService;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.service.RoomService;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.service.TicketService;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Created by rsanchez on 01/12/17.
 */
@Service
public class ProxyService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProxyService.class);

  private static final int TICKET_ID_LENGTH = 10;

  private final String groupId;

  private final MembershipService membershipService;

  private final TicketService ticketService;

  private final RoomService roomService;

  public ProxyService(@Value("${groupId}") String groupId, SymphonyClient symphonyClient,
      MembershipService membershipService, TicketService ticketService, RoomService roomService) {
    this.groupId = groupId;
    this.membershipService = membershipService;
    this.ticketService = ticketService;
    this.roomService = roomService;
  }

  /**
   * On message:
   *    Check if membership exists, if not, create membership.
   *    Check if ticket exists, if not, create service room and ticket. Also sends ticket message
   *    to agent stream id
   *
   * @param message the message to proxy.
   */
  public void messageReceived(SymMessage message) {
    Membership membership = membershipService.updateMembership(message);

    if (CLIENT.getType().equals(membership.getType())) {
      Ticket ticket = ticketService.getUnresolvedTicket(message.getStreamId());

      if (ticket == null) {
        String ticketId = RandomStringUtils.randomAlphanumeric(TICKET_ID_LENGTH).toUpperCase();

        String serviceStreamId = roomService.newServiceStream(ticketId, groupId);

        ticketService.createTicket(ticketId, message, serviceStreamId);
      } else {
        LOGGER.info("Ticket already exists");
      }
    }
  }

}
