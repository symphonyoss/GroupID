package com.symphony.helpdesk.service.api.impl;

import com.symphony.api.helpdesk.service.api.V1ApiService;
import com.symphony.api.helpdesk.service.model.Membership;
import com.symphony.api.helpdesk.service.model.MembershipResponse;
import com.symphony.api.helpdesk.service.model.Ticket;
import com.symphony.api.helpdesk.service.model.TicketResponse;
import com.symphony.api.helpdesk.service.model.TicketSearchResponse;

import javax.ws.rs.core.Response;

/**
 * Created by nick.tarsillo on 9/25/17.
 */
public abstract class V1AbstractHelpDeskApi implements V1ApiService {
  protected abstract MembershipResponse createMembership(Membership membership);
  protected abstract Membership deleteMembership(String id, String groupId);
  protected abstract MembershipResponse getMembership(String id, String groupId);
  protected abstract MembershipResponse updateMembership(String id, String groupId, Membership ticket);
  protected abstract TicketResponse createTicket(Ticket ticket);
  protected abstract Ticket deleteTicket(String id);
  protected abstract TicketResponse getTicket(String id);
  protected abstract TicketSearchResponse searchTicket(String id, String groupId, String serviceRoomId);
  protected abstract TicketResponse updateTicket(String id, Ticket ticket);

  @Override
  public Response v1MembershipCreatePost(Membership membership) {
    return Response.ok(createMembership(membership)).build();
  }

  @Override
  public Response v1MembershipIdGroupIdDeletePost(String id, String groupId) {
    deleteMembership(id, groupId);
    return Response.ok().build();
  }

  @Override
  public Response v1MembershipIdGroupIdGetGet(String id, String groupId) {
    return Response.ok(getMembership(id, groupId)).build();
  }

  @Override
  public Response v1MembershipIdGroupIdUpdatePost(String id, String groupId, Membership ticket) {
    return Response.ok(updateMembership(id, groupId,ticket)).build();
  }

  @Override
  public Response v1TicketCreatePost(Ticket ticket) {
    return Response.ok(createTicket(ticket)).build();
  }

  @Override
  public Response v1TicketIdDeletePost(String id) {
    deleteTicket(id);
    return Response.ok().build();
  }

  @Override
  public Response v1TicketIdGetGet(String id) {
    return Response.ok(getTicket(id)).build();
  }

  @Override
  public Response v1TicketSearchGet(String id, String groupId, String serviceRoomId) {
    return Response.ok(searchTicket(id, groupId, serviceRoomId)).build();
  }

  @Override
  public Response v1TicketIdUpdatePost(String id, Ticket ticket) {
    return Response.ok(updateTicket(id, ticket)).build();
  }
}
