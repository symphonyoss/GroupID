package org.symphonyoss.symphony.bots.ai.helpdesk.menu;

import org.symphonyoss.symphony.bots.ai.helpdesk.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;
import org.symphonyoss.symphony.bots.ai.helpdesk.command.AcceptTicketCommand;
import org.symphonyoss.symphony.bots.ai.helpdesk.command.AddMemberCommand;

/**
 * An AI command line menu, that will contain agent commands.
 * <p>
 * Created by nick.tarsillo on 9/28/17.
 */
public class AgentCommandMenu extends AiCommandMenu {

  public AgentCommandMenu(HelpDeskAiConfig helpDeskAiConfig) {
    super(helpDeskAiConfig.getDefaultPrefix());

    addCommand(new AcceptTicketCommand(helpDeskAiConfig));
    addCommand(new AddMemberCommand(helpDeskAiConfig));
  }

}
