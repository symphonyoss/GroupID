package org.symphonyoss.symphony.bots.ai;
import org.symphonyoss.symphony.bots.ai.menu.AgentCommandMenu;
import org.symphonyoss.symphony.bots.ai.menu.ClientCommandMenu;
import org.symphonyoss.symphony.bots.ai.menu.ServiceCommandMenu;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;

/**
 * Created by nick.tarsillo on 10/9/17.
 */
public class HelpDeskAiSessionContext extends AiSessionContext {
  public enum SessionType {
    AGENT_SERVICE,
    AGENT,
    CLIENT
  }

  protected String groupId;
  protected SessionType sessionType;
  protected HelpDeskAiSession helpDeskAiSession;

  public HelpDeskAiSession getHelpDeskAiSession() {
    return helpDeskAiSession;
  }

  public void setHelpDeskAiSession(HelpDeskAiSession helpDeskAiSession) {
    this.helpDeskAiSession = helpDeskAiSession;
  }

  public SessionType getSessionType() {
    return sessionType;
  }

  public void setSessionType(SessionType sessionType) {
    switch (sessionType) {
      case AGENT_SERVICE:
        setAiCommandMenu(new ServiceCommandMenu(helpDeskAiSession.getHelpDeskAiConfig()));
        break;
      case AGENT:
        setAiCommandMenu(new AgentCommandMenu(helpDeskAiSession.getHelpDeskAiConfig()));
        break;
      default:
        setAiCommandMenu(new ClientCommandMenu());
        break;
    }

    this.sessionType = sessionType;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }


}
