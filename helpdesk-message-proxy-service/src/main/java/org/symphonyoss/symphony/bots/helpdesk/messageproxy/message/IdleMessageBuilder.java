package org.symphonyoss.symphony.bots.helpdesk.messageproxy.message;

import org.symphonyoss.symphony.bots.utility.message.SymMessageBuilder;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Created by rsanchez on 11/12/17.
 */
public class IdleMessageBuilder {

  private static final String TEMPLATE = "<messageML>Ticket #%s %s</messageML>";

  private String ticketId;

  private String message;

  public IdleMessageBuilder ticket(String ticketId) {
    this.ticketId = ticketId;
    return this;
  }

  public IdleMessageBuilder message(String message) {
    this.message = message;
    return this;
  }

  public SymMessage build() {
    String ticketMessage = String.format(TEMPLATE, ticketId, message);
    return SymMessageBuilder.message(ticketMessage).build();
  }

}
