package org.symphonyoss.symphony.bots.ai.menu;

import org.symphonyoss.symphony.bots.ai.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;
import org.symphonyoss.symphony.bots.ai.command.AcceptTicketCommand;
import org.symphonyoss.symphony.bots.ai.command.AddMemberCommand;

/**
 * Created by nick.tarsillo on 9/28/17.
 * An AI command line menu, that will contain agent commands.
 */
public class AgentCommandMenu extends AiCommandMenu {
  public AgentCommandMenu(HelpDeskAiConfig helpDeskAiConfig) {
    setCommandPrefix(helpDeskAiConfig.getDefaultPrefix());

    addCommand(new AcceptTicketCommand(helpDeskAiConfig.getAcceptTicketCommand(),
        helpDeskAiConfig.getDefaultPrefix() + helpDeskAiConfig.getAcceptTicketCommand()));
    addCommand(new AddMemberCommand(helpDeskAiConfig.getAddMemberCommand(),
        helpDeskAiConfig.getDefaultPrefix() + helpDeskAiConfig.getAddMemberCommand()));
  }
}
