package com.symphony.bots.helpdesk.model.ai;

import com.symphony.bots.ai.model.AiSessionContext;
import com.symphony.bots.ai.model.AiSessionContextManager;
import com.symphony.bots.ai.model.AiSessionKey;
import com.symphony.bots.helpdesk.model.ai.menu.AgentCommandMenu;
import com.symphony.bots.helpdesk.model.ai.menu.ClientCommandMenu;
import com.symphony.bots.helpdesk.model.ai.menu.ServiceCommandMenu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nick.tarsillo on 9/28/17.
 * Extension of the ai session context manager.
 */
public class HelpDeskSessionContextManager extends AiSessionContextManager {
  private static final Logger LOG = LoggerFactory.getLogger(HelpDeskSessionContextManager.class);

  public HelpDeskSessionContextManager(String aiSessionContextDir) {
    super(aiSessionContextDir);
  }

  /**
   * Creates a new session context. The menu will automatically be set for the session, based on
   * the help desk session type.
   * @param aiSessionKey the session key to base the session on.
   * @return the session context
   */
  @Override
  protected AiSessionContext newSessionContext(AiSessionKey aiSessionKey) {
    AiSessionContext aiSessionContext = new AiSessionContext();
    aiSessionContext.setAiSessionKey(aiSessionKey);

    HelpDeskAiSessionKey sessionKey = (HelpDeskAiSessionKey) aiSessionKey;
    if(sessionKey.getSessionType().equals(HelpDeskAiSessionKey.SessionType.AGENT_SERVICE)) {
      aiSessionContext.setAiCommandMenu(new ServiceCommandMenu());
      LOG.info("New agent service AI session created.");
    } else if(sessionKey.getSessionType().equals(HelpDeskAiSessionKey.SessionType.AGENT)) {
      aiSessionContext.setAiCommandMenu(new AgentCommandMenu());
      LOG.info("New agent AI session created.");
    } else {
      aiSessionContext.setAiCommandMenu(new ClientCommandMenu());
      LOG.info("New client AI session created.");
    }

    return aiSessionContext;
  }

}
