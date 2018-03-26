package org.symphonyoss.symphony.bots.ai.helpdesk;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.bots.ai.helpdesk.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.ai.helpdesk.menu.AgentCommandMenu;
import org.symphonyoss.symphony.bots.ai.helpdesk.menu.ClientCommandMenu;
import org.symphonyoss.symphony.bots.ai.helpdesk.menu.ServiceCommandMenu;
import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;
import org.symphonyoss.symphony.bots.ai.model.SymphonyAiSessionKey;

/**
 * Unit tests for {@link HelpDeskAiSessionContext}
 * Created by robson on 26/03/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class HelpDeskAiSessionContextTest {

  private static final String SESSION_KEY = "SESSION";

  private static final Long USER_ID = 1234L;

  private static final String STREAM_ID = "STREAM";

  @Test
  public void testClientSession() {
    HelpDeskAiSession aiSession = new HelpDeskAiSession();
    aiSession.setHelpDeskAiConfig(new HelpDeskAiConfig());

    SymphonyAiSessionKey sessionKey = new SymphonyAiSessionKey(SESSION_KEY, USER_ID, STREAM_ID);

    HelpDeskAiSessionContext context = new HelpDeskAiSessionContext(sessionKey, aiSession);
    context.setSessionType(HelpDeskAiSessionContext.SessionType.CLIENT);

    AiCommandMenu aiCommandMenu = context.getAiCommandMenu();
    assertEquals(ClientCommandMenu.class, aiCommandMenu.getClass());
  }

  @Test
  public void testAgentSession() {
    HelpDeskAiSession aiSession = new HelpDeskAiSession();
    aiSession.setHelpDeskAiConfig(new HelpDeskAiConfig());

    SymphonyAiSessionKey sessionKey = new SymphonyAiSessionKey(SESSION_KEY, USER_ID, STREAM_ID);

    HelpDeskAiSessionContext context = new HelpDeskAiSessionContext(sessionKey, aiSession);
    context.setSessionType(HelpDeskAiSessionContext.SessionType.AGENT);

    AiCommandMenu aiCommandMenu = context.getAiCommandMenu();
    assertEquals(AgentCommandMenu.class, aiCommandMenu.getClass());
  }

  @Test
  public void testAgentServiceSession() {
    HelpDeskAiSession aiSession = new HelpDeskAiSession();
    aiSession.setHelpDeskAiConfig(new HelpDeskAiConfig());

    SymphonyAiSessionKey sessionKey = new SymphonyAiSessionKey(SESSION_KEY, USER_ID, STREAM_ID);

    HelpDeskAiSessionContext context = new HelpDeskAiSessionContext(sessionKey, aiSession);
    context.setSessionType(HelpDeskAiSessionContext.SessionType.AGENT_SERVICE);

    AiCommandMenu aiCommandMenu = context.getAiCommandMenu();
    assertEquals(ServiceCommandMenu.class, aiCommandMenu.getClass());
  }

}
