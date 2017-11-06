package org.symphonyoss.symphony.bots.helpdesk.service.api;

import org.symphonyoss.symphony.bots.helpdesk.service.api.impl.V1AbstractHelpDeskApi;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.model.MembershipDao;
import org.symphonyoss.symphony.bots.helpdesk.service.model.MembershipResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.model.TicketDao;
import org.symphonyoss.symphony.bots.helpdesk.service.model.TicketResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.model.TicketSearchResponse;

/**
 * Created by nick.tarsillo on 9/25/17.
 */
public class V1HelpDeskApi extends V1AbstractHelpDeskApi {
  private MembershipDao membershipDao;
  private TicketDao ticketDao;

  public V1HelpDeskApi(MembershipDao membershipDao, TicketDao ticketDao) {
    this.membershipDao = membershipDao;
    this.ticketDao = ticketDao;
  }

  @Override
  protected MembershipResponse createMembership(Membership membership) {
    return membershipDao.createMembership(membership);
  }

  @Override
  protected Membership deleteMembership(String id, String groupId) {
    return membershipDao.deleteMembership(id, groupId);
  }

  @Override
  protected MembershipResponse getMembership(String id, String groupId) {
    return membershipDao.getMembership(id, groupId);
  }

  @Override
  protected MembershipResponse updateMembership(String id, String groupId, Membership ticket) {
    return membershipDao.updateMembership(id, groupId, ticket);
  }

  @Override
  protected TicketResponse createTicket(Ticket ticket) {
    return ticketDao.createTicket(ticket);
  }

  @Override
  protected Ticket deleteTicket(String id) {
    return ticketDao.deleteTicket(id);
  }

  @Override
  protected TicketResponse getTicket(String id) {
    return ticketDao.getTicket(id);
  }

  @Override
  protected TicketSearchResponse searchTicket(String id, String groupId, String serviceRoomId, String clientStreamId) {
    return ticketDao.searchTicket(id, groupId, serviceRoomId, clientStreamId);
  }

  @Override
  protected TicketResponse updateTicket(String id, Ticket ticket) {
    return ticketDao.updateTicket(id, ticket);
  }
}
