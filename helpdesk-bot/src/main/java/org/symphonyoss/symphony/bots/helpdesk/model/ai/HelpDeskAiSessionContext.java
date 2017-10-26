package org.symphonyoss.symphony.bots.helpdesk.model.ai;

import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.helpdesk.model.HelpDeskBotSession;

/**
 * Created by nick.tarsillo on 10/9/17.
 */
public class HelpDeskAiSessionContext extends AiSessionContext {
  private HelpDeskBotSession helpDeskBotSession;

  public HelpDeskBotSession getHelpDeskBotSession() {
    return helpDeskBotSession;
  }

  public void setHelpDeskBotSession(HelpDeskBotSession helpDeskBotSession) {
    this.helpDeskBotSession = helpDeskBotSession;
  }
}
