package org.symphonyoss.symphony.bots.helpdesk.model.ai.menu;

import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;
import org.symphonyoss.symphony.bots.helpdesk.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.model.ai.command.CloseTicketCommand;

/**
 * Created by nick.tarsillo on 9/28/17.
 * An AI command line menu that will contain commands for agents in client service rooms.
 */
public class ServiceCommandMenu extends AiCommandMenu {
  public ServiceCommandMenu(String groupId) {
    HelpDeskBotConfig helpDeskBotConfig = HelpDeskBotConfig.getConfig(groupId);
    setCommandPrefix(helpDeskBotConfig.getAiDefaultPrefix());

    addCommand(new CloseTicketCommand(helpDeskBotConfig.getCloseTicketCommand(),
        helpDeskBotConfig.getAiDefaultPrefix() + helpDeskBotConfig.getCloseTicketCommand()));
  }
}
