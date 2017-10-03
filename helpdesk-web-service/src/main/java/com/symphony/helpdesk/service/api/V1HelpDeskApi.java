package com.symphony.helpdesk.service.api;

import com.symphony.api.helpdesk.service.model.Membership;
import com.symphony.api.helpdesk.service.model.MembershipResponse;
import com.symphony.api.helpdesk.service.model.Ticket;
import com.symphony.api.helpdesk.service.model.TicketResponse;
import com.symphony.api.helpdesk.service.model.TicketSearchResponse;
import com.symphony.helpdesk.service.api.impl.V1AbstractHelpDeskApi;
import com.symphony.helpdesk.service.model.MembershipDoa;
import com.symphony.helpdesk.service.model.TicketDoa;

/**
 * Created by nick.tarsillo on 9/25/17.
 */
public class V1HelpDeskApi extends V1AbstractHelpDeskApi {
  private MembershipDoa membershipDoa;
  private TicketDoa ticketDoa;

  public V1HelpDeskApi(MembershipDoa membershipDoa, TicketDoa ticketDoa) {
    this.membershipDoa = membershipDoa;
    this.ticketDoa = ticketDoa;
  }

  @Override
  protected MembershipResponse createMembership(Membership membership) {
    return membershipDoa.createMembership(membership);
  }

  @Override
  protected Membership deleteMembership(String id, String groupId) {
    return membershipDoa.deleteMembership(id, groupId);
  }

  @Override
  protected MembershipResponse getMembership(String id, String groupId) {
    return membershipDoa.getMembership(id, groupId);
  }

  @Override
  protected MembershipResponse updateMembership(String id, String groupId, Membership ticket) {
    return membershipDoa.updateMembership(id, groupId, ticket);
  }

  @Override
  protected TicketResponse createTicket(Ticket ticket) {
    return ticketDoa.createTicket(ticket);
  }

  @Override
  protected Ticket deleteTicket(String id) {
    return ticketDoa.deleteTicket(id);
  }

  @Override
  protected TicketResponse getTicket(String id) {
    return ticketDoa.getTicket(id);
  }

  @Override
  protected TicketSearchResponse searchTicket(String id, String groupId, String serviceRoomId) {
    return ticketDoa.searchTicket(id, groupId, serviceRoomId);
  }

  @Override
  protected TicketResponse updateTicket(String id, Ticket ticket) {
    return ticketDoa.updateTicket(id, ticket);
  }
}
