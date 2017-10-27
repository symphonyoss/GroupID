package org.symphonyoss.symphony.bots.ai;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;

/**
 * Created by nick.tarsillo on 10/9/17.
 */
public class HelpDeskAiSessionContext extends AiSessionContext {
  protected HelpDeskAiSession helpDeskAiSession;

  public HelpDeskAiSessionContext(HelpDeskAiSession helpDeskAiSession) {
    this.helpDeskAiSession = helpDeskAiSession;
  }

  public HelpDeskAiSession getHelpDeskAiSession() {
    return helpDeskAiSession;
  }

  public void setHelpDeskAiSession(HelpDeskAiSession helpDeskAiSession) {
    this.helpDeskAiSession = helpDeskAiSession;
  }

}
