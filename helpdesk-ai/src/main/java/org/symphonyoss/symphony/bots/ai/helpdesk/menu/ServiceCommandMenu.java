package org.symphonyoss.symphony.bots.ai.helpdesk.menu;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.ai.helpdesk.command.CloseTicketCommand;
import org.symphonyoss.symphony.bots.ai.helpdesk.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.ai.helpdesk.conversation.IdleTimerManager;
import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;

/**
 * Created by nick.tarsillo on 9/28/17.
 * An AI command line menu that will contain commands for agents in client service rooms.
 */
public class ServiceCommandMenu extends AiCommandMenu {

  public ServiceCommandMenu(HelpDeskAiConfig helpDeskAiConfig, TicketClient ticketClient,
      SymphonyClient symphonyClient, IdleTimerManager idleTimerManager) {
    super(helpDeskAiConfig.getAgentServiceRoomPrefix());
    addCommand(new CloseTicketCommand(helpDeskAiConfig, ticketClient, symphonyClient, idleTimerManager));
  }

}
