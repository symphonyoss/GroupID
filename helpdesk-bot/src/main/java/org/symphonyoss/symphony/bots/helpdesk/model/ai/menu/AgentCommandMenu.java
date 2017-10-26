package org.symphonyoss.symphony.bots.helpdesk.model.ai.menu;

import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;
import org.symphonyoss.symphony.bots.helpdesk.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.model.ai.command.AcceptTicketCommand;
import org.symphonyoss.symphony.bots.helpdesk.model.ai.command.AddMemberCommand;

/**
 * Created by nick.tarsillo on 9/28/17.
 * An AI command line menu, that will contain agent commands.
 */
public class AgentCommandMenu extends AiCommandMenu {
  public AgentCommandMenu(String groupId) {
    HelpDeskBotConfig helpDeskBotConfig = HelpDeskBotConfig.getConfig(groupId);
    setCommandPrefix(helpDeskBotConfig.getAiDefaultPrefix());

    String acceptCommand = helpDeskBotConfig.getAcceptTicketCommand();
    addCommand(new AcceptTicketCommand(acceptCommand, acceptCommand));

    String addMemberCommand = helpDeskBotConfig.getAddMemberCommand();
    addCommand(new AddMemberCommand(addMemberCommand, addMemberCommand));
  }
}
