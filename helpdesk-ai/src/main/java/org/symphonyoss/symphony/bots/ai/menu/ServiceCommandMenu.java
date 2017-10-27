package org.symphonyoss.symphony.bots.ai.menu;

import org.symphonyoss.symphony.bots.ai.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;
import org.symphonyoss.symphony.bots.ai.command.CloseTicketCommand;

/**
 * Created by nick.tarsillo on 9/28/17.
 * An AI command line menu that will contain commands for agents in client service rooms.
 */
public class ServiceCommandMenu extends AiCommandMenu {
  public ServiceCommandMenu(HelpDeskAiConfig helpDeskAiConfig) {
    setCommandPrefix(helpDeskAiConfig.getAgentServiceRoomPrefix());

    addCommand(new CloseTicketCommand(helpDeskAiConfig.getCloseTicketCommand(),
        helpDeskAiConfig.getAgentServiceRoomPrefix() + helpDeskAiConfig.getCloseTicketCommand()));
  }
}
