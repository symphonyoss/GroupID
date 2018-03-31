package org.symphonyoss.symphony.bots.ai.helpdesk.menu;

import org.symphonyoss.symphony.bots.ai.helpdesk.HelpDeskAiSession;
import org.symphonyoss.symphony.bots.ai.helpdesk.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.ai.helpdesk.conversation.IdleTimerManager;
import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;
import org.symphonyoss.symphony.bots.ai.helpdesk.command.CloseTicketCommand;

/**
 * Created by nick.tarsillo on 9/28/17.
 * An AI command line menu that will contain commands for agents in client service rooms.
 */
public class ServiceCommandMenu extends AiCommandMenu {

  public ServiceCommandMenu(HelpDeskAiSession session, HelpDeskAiConfig helpDeskAiConfig,
      IdleTimerManager idleTimerManager) {
    super(helpDeskAiConfig.getAgentServiceRoomPrefix());
    addCommand(new CloseTicketCommand(session, helpDeskAiConfig, idleTimerManager));
  }

}
