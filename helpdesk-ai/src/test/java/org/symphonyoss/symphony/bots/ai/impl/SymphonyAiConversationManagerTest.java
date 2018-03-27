package org.symphonyoss.symphony.bots.ai.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.bots.ai.conversation.NullConversation;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;

/**
 * Unit tests for {@link SymphonyAiConversationManager}
 * Created by robson on 26/03/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class SymphonyAiConversationManagerTest {

  @Mock
  private AiConversation conversation;

  @Mock
  private AiSessionContext aiSessionContext;

  @Test
  public void testManager() {
    SymphonyAiConversationManager manager = new SymphonyAiConversationManager();

    AiConversation result = manager.getConversation(aiSessionContext);

    assertEquals(NullConversation.class, result.getClass());

    manager.registerConversation(aiSessionContext, conversation);
    result = manager.getConversation(aiSessionContext);

    assertEquals(conversation, result);

    manager.removeConversation(aiSessionContext);
    result = manager.getConversation(aiSessionContext);

    assertEquals(NullConversation.class, result.getClass());
  }
}
