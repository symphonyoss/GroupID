package org.symphonyoss.symphony.bots.ai.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.ai.model.SymphonyAiSessionKey;

/**
 * Unit tests for {@link SymphonyAiSessionContextManager}
 * Created by robson on 26/03/18.
 */
public class SymphonyAiSessionContextManagerTest {

  private static final Long USER_ID = 123456L;

  private static final String STREAM_ID = "MOCK_STREAM";

  private static final String SESSION_KEY = USER_ID + ":" + STREAM_ID;

  @Test
  public void testManager() {
    SymphonyAiSessionKey sessionKey = new SymphonyAiSessionKey(SESSION_KEY, USER_ID, STREAM_ID);

    SymphonyAiSessionContextManager manager = new SymphonyAiSessionContextManager();

    assertNull(manager.getSessionContext(sessionKey));

    AiSessionContext sessionContext = new AiSessionContext(sessionKey);

    manager.putSessionContext(sessionKey, sessionContext);

    assertEquals(sessionContext, manager.getSessionContext(sessionKey));
  }
}
