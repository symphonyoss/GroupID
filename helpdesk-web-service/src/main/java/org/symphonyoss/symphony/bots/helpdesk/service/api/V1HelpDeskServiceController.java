package org.symphonyoss.symphony.bots.helpdesk.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.bind.annotation.RestController;
import org.symphonyoss.symphony.bots.helpdesk.service.api.V1ApiController;
import org.symphonyoss.symphony.bots.helpdesk.service.model.HealthcheckResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.model.MembershipDao;
import org.symphonyoss.symphony.bots.helpdesk.service.model.MembershipResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.model.SuccessResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.model.TicketDao;
import org.symphonyoss.symphony.bots.helpdesk.service.model.TicketResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.model.TicketSearchResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.model.health.HealthCheckFailedException;

/**
 * Created by nick.tarsillo on 9/25/17.
 */
@RestController
public class V1HelpDeskServiceController extends V1ApiController {
  @Autowired
  private MembershipDao membershipDao;
  @Autowired
  private TicketDao ticketDao;

  @Override
  public MembershipResponse createMembership(Membership membership) {
    return membershipDao.createMembership(membership);
  }

  @Override
  public SuccessResponse deleteMembership(String id, String groupId) {
    membershipDao.deleteMembership(id, groupId);
    return new SuccessResponse();
  }

  @Override
  public MembershipResponse getMembership(String id, String groupId) {
    return membershipDao.getMembership(id, groupId);
  }

  @Override
  public MembershipResponse updateMembership(String id, String groupId, Membership ticket) {
    return membershipDao.updateMembership(id, groupId, ticket);
  }

  @Override
  public TicketResponse createTicket(Ticket ticket) {
    return ticketDao.createTicket(ticket);
  }

  @Override
  public SuccessResponse deleteTicket(String id) {
    ticketDao.deleteTicket(id);
    return new SuccessResponse();
  }

  @Override
  public TicketResponse getTicket(String id) {
    return ticketDao.getTicket(id);
  }

  @Override
  public HealthcheckResponse healthcheck() {
    HealthcheckResponse healthcheckResponse = new HealthcheckResponse();

    try {
      membershipDao.healthcheck();
      healthcheckResponse.setMembershipDatabaseConnectivityCheck(true);
    } catch (HealthCheckFailedException e) {
      healthcheckResponse.setMembershipDatabaseConnectivityCheck(false);
      healthcheckResponse.setMembershipDatabaseConnectivityError(e.getMessage());
    }

    try {
      ticketDao.healthcheck();
      healthcheckResponse.setTicketDatabaseConnectivityCheck(true);
    } catch (HealthCheckFailedException e) {
      healthcheckResponse.setTicketDatabaseConnectivityCheck(false);
      healthcheckResponse.setTicketDatabaseConnectivityError(e.getMessage());
    }

    return healthcheckResponse;
  }

  @Override
  public TicketSearchResponse searchTicket(String id, String groupId, String serviceRoomId, String clientStreamId) {
    return ticketDao.searchTicket(id, groupId, serviceRoomId, clientStreamId);
  }

  @Override
  public TicketResponse updateTicket(String id, Ticket ticket) {
    return ticketDao.updateTicket(id, ticket);
  }
}
