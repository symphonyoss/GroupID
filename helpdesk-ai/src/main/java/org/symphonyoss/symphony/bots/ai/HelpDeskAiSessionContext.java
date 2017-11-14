package org.symphonyoss.symphony.bots.ai;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiSessionContext;
import org.symphonyoss.symphony.bots.ai.menu.AgentCommandMenu;
import org.symphonyoss.symphony.bots.ai.menu.ClientCommandMenu;
import org.symphonyoss.symphony.bots.ai.menu.ServiceCommandMenu;

/**
 * Created by nick.tarsillo on 10/9/17.
 */
public class HelpDeskAiSessionContext extends SymphonyAiSessionContext {
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
    if(sessionType.equals(SessionType.AGENT_SERVICE)) {
      setAiCommandMenu(new ServiceCommandMenu(helpDeskAiSession.getHelpDeskAiConfig()));
    } else if(sessionType.equals(SessionType.AGENT)) {
      setAiCommandMenu(new AgentCommandMenu(helpDeskAiSession.getHelpDeskAiConfig()));
    } else {
      setAiCommandMenu(new ClientCommandMenu());
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
