package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStreamAttributes;

/**
 * Created by nick.tarsillo on 10/20/17.
 */
public class AgentExternalCheck extends Checker {
  private static final Logger LOG = LoggerFactory.getLogger(AgentExternalCheck.class);

  private TicketClient ticketClient;
  private SymphonyClient symphonyClient;

  public AgentExternalCheck(SymphonyClient symphonyClient, TicketClient ticketClient) {
    this.ticketClient = ticketClient;
    this.symphonyClient = symphonyClient;
  }

  @Override
  public boolean check(SymMessage message) {
    Ticket ticket = ticketClient.getTicketByServiceStreamId(message.getStreamId());
    if(ticket != null) {
      try {
        SymStreamAttributes streamAttributes =
            symphonyClient.getStreamsClient().getStreamAttributes(ticket.getClientStreamId());
        if((message.getAttachments() != null || !message.getAttachments().isEmpty()) &&
            streamAttributes.getCrossPod()) {
          return false;
        }
      } catch (StreamsException e) {
        LOG.error("Could not get stream for client stream: ", e);
      }
    }

    return true;
  }
}
