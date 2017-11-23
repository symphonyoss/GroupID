package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model;

import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Created by nick.tarsillo on 10/20/17.
 */
public class AgentExternalCheck extends Checker {
  private TicketClient ticketClient;

  public AgentExternalCheck(TicketClient ticketClient) {
    this.ticketClient = ticketClient;
  }

  @Override
  public boolean check(SymMessage message) {
    //TODO SJC does not support checking if a room is cross pod yet. Waiting for new version.
    return false;
  }
}
