package org.symphonyoss.symphony.bots.helpdesk.messageproxy;

import org.springframework.stereotype.Component;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.services.MessageListener;
import org.symphonyoss.symphony.bots.ai.HelpDeskAi;
import org.symphonyoss.symphony.clients.model.SymMessage;

import javax.annotation.PostConstruct;

/**
 * Component responsible for listening the messages sent to bot.
 *
 * Created by rsanchez on 01/12/17.
 */
@Component
public class ChatListener implements MessageListener {

  private final SymphonyClient symphonyClient;

  private final TicketManagerService ticketManagerService;

  private final HelpDeskAi helpDeskAi;

  public ChatListener(SymphonyClient symphonyClient, TicketManagerService ticketManagerService,
      HelpDeskAi helpDeskAi) {
    this.symphonyClient = symphonyClient;
    this.ticketManagerService = ticketManagerService;
    this.helpDeskAi = helpDeskAi;
  }

  @PostConstruct
  public void init() {
    this.symphonyClient.getMessageService().addMessageListener(this);
  }

  @Override
  public void onMessage(SymMessage symMessage) {
    ticketManagerService.messageReceived(symMessage);
    helpDeskAi.onMessage(symMessage);
  }

}
