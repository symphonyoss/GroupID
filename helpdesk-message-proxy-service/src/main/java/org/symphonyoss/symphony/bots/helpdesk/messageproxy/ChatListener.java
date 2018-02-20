package org.symphonyoss.symphony.bots.helpdesk.messageproxy;

import org.springframework.stereotype.Component;
import org.symphonyoss.client.services.MessageListener;
import org.symphonyoss.symphony.bots.ai.HelpDeskAi;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Component responsible for listening the messages sent to bot.
 *
 * Created by rsanchez on 01/12/17.
 */
@Component
public class ChatListener implements MessageListener {

  private final TicketManagerService ticketManagerService;

  private final HelpDeskAi helpDeskAi;

  private boolean ready;

  public ChatListener(TicketManagerService ticketManagerService, HelpDeskAi helpDeskAi) {
    this.ticketManagerService = ticketManagerService;
    this.helpDeskAi = helpDeskAi;
  }

  @Override
  public void onMessage(SymMessage symMessage) {
    if (ready) {
      ticketManagerService.messageReceived(symMessage);
      helpDeskAi.onMessage(symMessage);
    }
  }

  public void ready() {
    this.ready = true;
  }

}
