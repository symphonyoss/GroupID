package org.symphonyoss.symphony.bots.helpdesk.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RestController;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.dao.MakercheckerDao;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.dao.MembershipDao;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Makerchecker;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.model.SuccessResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.model.TicketSearchResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.dao.TicketDao;

import java.util.List;

/**
 * Created by rsanchez on 13/11/17.
 */
@RestController
@Lazy
public class V1HelpDeskServiceController extends V1ApiController {

  private static final String DELETE_MEMBERSHIP_RESPONSE = "Membership deleted.";

  private static final String DELETE_TICKET_RESPONSE = "Ticket deleted.";

  @Autowired
  private MembershipDao membershipDao;

  @Autowired
  private TicketDao ticketDao;

  @Autowired
  private MakercheckerDao makercheckerDao;

  @Override
  public Membership createMembership(Membership membership) {
    validateRequiredParameter("groupId", membership.getGroupId(), "body");
    validateRequiredParameter("id", membership.getId(), "body");
    validateRequiredParameter("type", membership.getType(), "body");

    return membershipDao.createMembership(membership);
  }

  @Override
  public SuccessResponse deleteMembership(String groupId, Long id) {
    membershipDao.deleteMembership(groupId, id);
    return new SuccessResponse().message(DELETE_MEMBERSHIP_RESPONSE);
  }

  @Override
  public Membership getMembership(String groupId, Long id) {
    return membershipDao.getMembership(groupId, id);
  }

  @Override
  public Membership updateMembership(String groupId, Long id, Membership membership) {
    return membershipDao.updateMembership(groupId, id, membership);
  }

  @Override
  public Ticket createTicket(Ticket ticket) {
    validateRequiredParameter("groupId", ticket.getGroupId(), "body");
    validateRequiredParameter("state", ticket.getState(), "body");

    return ticketDao.createTicket(ticket);
  }

  @Override
  public SuccessResponse deleteTicket(String id) {
    ticketDao.deleteTicket(id);
    return new SuccessResponse().message(DELETE_TICKET_RESPONSE);
  }

  @Override
  public Ticket getTicket(String id) {
    return ticketDao.getTicket(id);
  }

  @Override
  public TicketSearchResponse searchTicket(String groupId, String serviceRoomId,
      String clientStreamId) {
    validateRequiredParameter("groupId", groupId, "parameters");

    List<Ticket> tickets = ticketDao.searchTicket(groupId, serviceRoomId, clientStreamId);

    if ((tickets != null) && (!tickets.isEmpty())) {
      TicketSearchResponse response = new TicketSearchResponse();
      response.addAll(tickets);

      return response;
    }

    return null;
  }

  @Override
  public Ticket updateTicket(String id, Ticket ticket) {
    return ticketDao.updateTicket(id, ticket);
  }

  @Override
  public Makerchecker createMakerchecker(Makerchecker makerchecker) {
    validateRequiredParameter("id", makerchecker.getId(), "body");
    validateRequiredParameter("makerId", makerchecker.getMakerId(), "body");
    validateRequiredParameter("streamId", makerchecker.getStreamId(), "body");
    validateRequiredParameter("state", makerchecker.getState(), "body");

    return makercheckerDao.createMakerchecker(makerchecker);
  }

  @Override
  public Makerchecker getMakerchecker(String id) {
    return makercheckerDao.getMakerchecker(id);
  }

  @Override
  public Makerchecker updateMakerchecker(String id, Makerchecker makerchecker) {
    validateRequiredParameter("id", makerchecker.getId(), "body");
    validateRequiredParameter("makerId", makerchecker.getMakerId(), "body");
    validateRequiredParameter("streamId", makerchecker.getStreamId(), "body");
    validateRequiredParameter("checker", makerchecker.getChecker()  , "body");
    validateRequiredParameter("state", makerchecker.getState(), "body");
    return makercheckerDao.updateMakerchecker(id, makerchecker);
  }
}
