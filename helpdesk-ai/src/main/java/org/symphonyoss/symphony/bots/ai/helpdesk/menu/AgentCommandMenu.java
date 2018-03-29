package org.symphonyoss.symphony.bots.ai.helpdesk.menu;

import org.apache.commons.lang3.StringUtils;
import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;

/**
 * An AI command line menu, that will contain agent commands.
 * <p>
 * Created by nick.tarsillo on 9/28/17.
 */
public class AgentCommandMenu extends AiCommandMenu {

  public AgentCommandMenu() {
    super(StringUtils.EMPTY);
  }

}
