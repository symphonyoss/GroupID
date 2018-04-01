package org.symphonyoss.symphony.bots.ai.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.bots.ai.AiEventListener;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiResponse;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.Collections;
import java.util.Set;

/**
 * Unit tests for {@link SymphonyAi}
 * Created by rsanchez on 21/03/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class SymphonyAiTest {

  private static final String MOCK_STREAM_ID = "MOCK_STREAM";

  private static final String MOCK_MESSAGE_ID = "MOCK_MESSAGE_ID";

  private static final Long MOCK_USER_ID = 1234L;

  @Mock
  private AiEventListener aiEventListener;

  @Mock
  private SymphonyAiConversationManager aiConversationManager;

  @Mock
  private AiResponder aiResponder;

  @Mock
  private AiConversation aiConversation;

  private SymMessage symMessage = new SymMessage();

  private SymphonyAi symphonyAi;

  @Before
  public void init() {
    this.symphonyAi = new SymphonyAi(aiEventListener, aiConversationManager, aiResponder);

    this.symMessage.setId(MOCK_MESSAGE_ID);
    this.symMessage.setFromUserId(MOCK_USER_ID);
    this.symMessage.setStreamId(MOCK_STREAM_ID);
  }

  @Test
  public void testOnMessage() {
    AiSessionKey sessionKey = symphonyAi.getSessionKey(MOCK_USER_ID, MOCK_STREAM_ID);
    AiMessage message = new AiMessage(symMessage);

    doReturn(aiConversation).when(aiConversationManager).getConversation(sessionKey);

    symphonyAi.onMessage(symMessage);

    verify(aiEventListener, times(1)).onMessage(sessionKey, message, aiConversation);
  }

  @Test
  public void testStartConversation() {
    AiSessionKey sessionKey = symphonyAi.getSessionKey(MOCK_USER_ID, MOCK_STREAM_ID);

    symphonyAi.startConversation(sessionKey, aiConversation);

    verify(aiConversationManager, times(1)).registerConversation(sessionKey, aiConversation);
  }

  @Test
  public void testGetConversation() {
    AiSessionKey sessionKey = symphonyAi.getSessionKey(MOCK_USER_ID, MOCK_STREAM_ID);

    doReturn(aiConversation).when(aiConversationManager).getConversation(sessionKey);

    assertEquals(aiConversation, symphonyAi.getConversation(sessionKey));
  }

  @Test
  public void testEndConversation() {
    AiSessionKey sessionKey = symphonyAi.getSessionKey(MOCK_USER_ID, MOCK_STREAM_ID);

    symphonyAi.endConversation(sessionKey);

    verify(aiConversationManager, times(1)).removeConversation(sessionKey);
  }

  @Test
  public void testSendMessage() {
    Set<String> responseIdentifierSet = Collections.singleton(MOCK_STREAM_ID);
    AiMessage message = new AiMessage(symMessage);

    symphonyAi.sendMessage(message, MOCK_STREAM_ID);

    ArgumentCaptor<AiResponse> responseParam = ArgumentCaptor.forClass(AiResponse.class);

    verify(aiResponder, times(1)).respond(responseParam.capture());

    AiResponse response = responseParam.getValue();

    assertEquals(message, response.getMessage());
    assertEquals(responseIdentifierSet, response.getRespondTo());
  }
}
