package org.symphonyoss.symphony.bots.helpdesk.service.api;

import org.springframework.web.bind.annotation.RestController;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.model.MembershipResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.model.SuccessResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.model.TicketResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.model.TicketSearchResponse;

/**
 * Created by rsanchez on 13/11/17.
 */
@RestController
public class V1HelpDeskServiceController extends V1ApiController {

  @Override
  public MembershipResponse getMembership(String groupId, String id) {
    return null; // TODO
  }

  @Override
  public MembershipResponse createMembership(String groupId, Membership membership) {
    return null; // TODO
  }

  @Override
  public MembershipResponse updateMembership(String groupId, String id, Membership ticket) {
    return null; // TODO
  }

  @Override
  public SuccessResponse deleteMembership(String groupId, String id) {
    return null; // TODO
  }

  @Override
  public TicketResponse createTicket(Ticket ticket) {
    return null; // TODO
  }

  @Override
  public SuccessResponse deleteTicket(String id) {
    return null; // TODO
  }

  @Override
  public TicketResponse getTicket(String id) {
    return null; // TODO
  }

  @Override
  public TicketSearchResponse searchTicket(String groupId, String serviceRoomId, String clientStreamId) {
    return null; // TODO
  }

  @Override
  public TicketResponse updateTicket(String id, Ticket ticket) {
    return null; // TODO
  }
}
