package org.symphonyoss.symphony.bots.ai.helpdesk;

import org.symphonyoss.symphony.bots.ai.helpdesk.conversation.IdleTimerManager;
import org.symphonyoss.symphony.bots.ai.helpdesk.menu.AgentCommandMenu;
import org.symphonyoss.symphony.bots.ai.helpdesk.menu.ClientCommandMenu;
import org.symphonyoss.symphony.bots.ai.helpdesk.menu.ServiceCommandMenu;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;

/**
 * HelpDesk AI Session Context
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
  protected IdleTimerManager idleTimerManager;

  public HelpDeskAiSession getHelpDeskAiSession() {
    return helpDeskAiSession;
  }

  public void setHelpDeskAiSession(HelpDeskAiSession helpDeskAiSession) {
    this.helpDeskAiSession = helpDeskAiSession;
  }

  public SessionType getSessionType() {
    return sessionType;
  }

  /**
   * Sets the session type of the HelpDesk AI (Service, Agent, or Client)
   * @param sessionType the type of the session
   */
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

  public IdleTimerManager getIdleTimerManager() {
    return idleTimerManager;
  }

  public void setIdleTimerManager(IdleTimerManager idleTimerManager) {
    this.idleTimerManager = idleTimerManager;
  }
}
