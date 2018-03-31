package org.symphonyoss.symphony.bots.ai.helpdesk;

import org.symphonyoss.symphony.bots.ai.helpdesk.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.ai.helpdesk.conversation.IdleTimerManager;
import org.symphonyoss.symphony.bots.ai.helpdesk.menu.AgentCommandMenu;
import org.symphonyoss.symphony.bots.ai.helpdesk.menu.ClientCommandMenu;
import org.symphonyoss.symphony.bots.ai.helpdesk.menu.ServiceCommandMenu;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.ai.model.SymphonyAiSessionKey;

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

  private final HelpDeskAiSession helpDeskAiSession;

  private final HelpDeskAiConfig aiConfig;

  private IdleTimerManager idleTimerManager;

  public HelpDeskAiSessionContext(SymphonyAiSessionKey sessionKey, HelpDeskAiSession helpDeskAiSession) {
    super(sessionKey);
    this.helpDeskAiSession = helpDeskAiSession;
    this.aiConfig = helpDeskAiSession.getHelpDeskAiConfig();
  }

  /**
   * Sets the session type of the HelpDesk AI (Service, Agent, or Client)
   * @param sessionType the type of the session
   */
  public void setSessionType(SessionType sessionType) {
    switch (sessionType) {
      case AGENT_SERVICE:
        setAiCommandMenu(new ServiceCommandMenu(helpDeskAiSession, aiConfig, idleTimerManager));
        break;
      case AGENT:
        setAiCommandMenu(new AgentCommandMenu());
        break;
      default:
        setAiCommandMenu(new ClientCommandMenu());
        break;
    }
  }

  public void setIdleTimerManager(IdleTimerManager idleTimerManager) {
    this.idleTimerManager = idleTimerManager;
  }
}
