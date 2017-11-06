package org.symphonyoss.symphony.bots.helpdesk.api.impl;

import org.symphonyoss.symphony.bots.helpdesk.api.V1ApiService;
import org.symphonyoss.symphony.bots.helpdesk.model.MakerCheckerMessageDetail;
import org.symphonyoss.symphony.bots.helpdesk.model.SuccessResponse;

import javax.ws.rs.core.Response;

/**
 * Created by nick.tarsillo on 9/25/17.
 */
public abstract class V1AbstractHelpDeskApi implements V1ApiService {
  public abstract SuccessResponse acceptTicket(String ticketId, String agentId);
  public abstract SuccessResponse acceptMakerCheckerMessage(MakerCheckerMessageDetail makerCheckerMessage);

  @Override
  public Response v1TicketTicketIdAcceptPost(String ticketId, String agentId) {
    return Response.ok(acceptTicket(ticketId, agentId)).build();
  }

  @Override
  public Response v1MakercheckerAcceptPost(MakerCheckerMessageDetail makerCheckerMessage) {
    return Response.ok(acceptMakerCheckerMessage(makerCheckerMessage)).build();
  }
}
