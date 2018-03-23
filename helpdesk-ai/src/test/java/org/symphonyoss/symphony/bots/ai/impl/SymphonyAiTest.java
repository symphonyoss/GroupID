package org.symphonyoss.symphony.bots.ai.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
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
import org.symphonyoss.symphony.bots.ai.AiResponseIdentifier;
import org.symphonyoss.symphony.bots.ai.conversation.NullConversation;
import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.ai.model.AiResponse;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.ai.model.SymphonyAiSessionKey;
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
  private SymphonyAiSessionContextManager aiSessionContextManager;

  @Mock
  private SymphonyAiConversationManager aiConversationManager;

  @Mock
  private AiResponder aiResponder;

  @Mock
  private AiSessionContext sessionContext;

  @Mock
  private AiConversation aiConversation;

  @Mock
  private AiResponseIdentifier responseIdentifier;

  private SymMessage symMessage = new SymMessage();

  private SymphonyAi symphonyAi;

  @Before
  public void init() {
    this.symphonyAi =
        new SymphonyAi(aiEventListener, aiSessionContextManager, aiConversationManager,
            aiResponder);

    this.symMessage.setId(MOCK_MESSAGE_ID);
    this.symMessage.setFromUserId(MOCK_USER_ID);
    this.symMessage.setStreamId(MOCK_STREAM_ID);
  }

  @Test
  public void testEqualsMessageId() {
    SymphonyAiSessionKey sessionKey = symphonyAi.getSessionKey(MOCK_USER_ID, MOCK_STREAM_ID);

    doReturn(sessionContext).when(aiSessionContextManager).getSessionContext(sessionKey);
    doReturn(MOCK_MESSAGE_ID).when(sessionContext).getLastMessageId();

    symphonyAi.onMessage(symMessage);

    verify(sessionContext, never()).setLastMessageId(MOCK_MESSAGE_ID);
  }

  @Test
  public void testDoNotAllowCommandsNullComandMenu() {
    SymphonyAiSessionKey sessionKey = symphonyAi.getSessionKey(MOCK_USER_ID, MOCK_STREAM_ID);
    SymphonyAiMessage message = new SymphonyAiMessage(symMessage);

    doReturn(sessionContext).when(aiSessionContextManager).getSessionContext(sessionKey);

    symphonyAi.onMessage(symMessage);

    verify(sessionContext, times(1)).setLastMessageId(MOCK_MESSAGE_ID);
    verify(aiEventListener, never()).onCommand(message, sessionContext);
    verify(aiEventListener, never()).onConversation(eq(message), any(AiConversation.class));
  }

  @Test
  public void testDoNotAllowCommands() {
    SymphonyAiSessionKey sessionKey = symphonyAi.getSessionKey(MOCK_USER_ID, MOCK_STREAM_ID);
    SymphonyAiMessage message = new SymphonyAiMessage(symMessage);

    doReturn(sessionContext).when(aiSessionContextManager).getSessionContext(sessionKey);
    doReturn(true).when(sessionContext).allowCommands();
    doReturn(aiConversation).when(aiConversationManager).getConversation(sessionKey);

    symphonyAi.onMessage(symMessage);

    verify(sessionContext, times(1)).setLastMessageId(MOCK_MESSAGE_ID);
    verify(aiEventListener, never()).onCommand(message, sessionContext);
    verify(aiEventListener, times(1)).onConversation(eq(message), any(AiConversation.class));
  }

  @Test
  public void testAllowCommandsNullConversation() {
    SymphonyAiSessionKey sessionKey = symphonyAi.getSessionKey(MOCK_USER_ID, MOCK_STREAM_ID);
    SymphonyAiMessage message = new SymphonyAiMessage(symMessage);

    NullConversation nullConversation = new NullConversation();

    doReturn(sessionContext).when(aiSessionContextManager).getSessionContext(sessionKey);
    doReturn(true).when(sessionContext).allowCommands();
    doReturn(nullConversation).when(aiConversationManager).getConversation(sessionKey);

    symphonyAi.onMessage(symMessage);

    verify(sessionContext, times(1)).setLastMessageId(MOCK_MESSAGE_ID);
    verify(aiEventListener, times(1)).onCommand(message, sessionContext);
    verify(aiEventListener, times(1)).onConversation(message, nullConversation);
  }

  @Test
  public void testGetSessionAlreadyExists() {
    SymphonyAiSessionKey sessionKey = symphonyAi.getSessionKey(MOCK_USER_ID, MOCK_STREAM_ID);

    doReturn(sessionContext).when(aiSessionContextManager).getSessionContext(sessionKey);

    assertEquals(sessionContext, symphonyAi.getSessionContext(sessionKey));
  }

  @Test
  public void testGetSession() {
    SymphonyAiSessionKey sessionKey = symphonyAi.getSessionKey(MOCK_USER_ID, MOCK_STREAM_ID);

    ArgumentCaptor<AiSessionContext> sessionContexParam = ArgumentCaptor.forClass(AiSessionContext.class);

    AiSessionContext result = symphonyAi.getSessionContext(sessionKey);

    verify(aiSessionContextManager).putSessionContext(eq(sessionKey), sessionContexParam.capture());

    assertEquals(sessionContexParam.getValue(), result);
  }

  @Test
  public void testStartConversation() {
    SymphonyAiSessionKey sessionKey = symphonyAi.getSessionKey(MOCK_USER_ID, MOCK_STREAM_ID);

    doReturn(sessionContext).when(aiSessionContextManager).getSessionContext(sessionKey);

    symphonyAi.startConversation(sessionKey, aiConversation);

    verify(aiConversationManager, times(1)).registerConversation(aiConversation);
  }

  @Test
  public void testGetConversation() {
    SymphonyAiSessionKey sessionKey = symphonyAi.getSessionKey(MOCK_USER_ID, MOCK_STREAM_ID);

    doReturn(sessionContext).when(aiSessionContextManager).getSessionContext(sessionKey);
    doReturn(aiConversation).when(aiConversationManager).getConversation(sessionKey);

    assertEquals(aiConversation, symphonyAi.getConversation(sessionKey));
  }

  @Test
  public void testEndConversation() {
    SymphonyAiSessionKey sessionKey = symphonyAi.getSessionKey(MOCK_USER_ID, MOCK_STREAM_ID);

    doReturn(sessionContext).when(aiSessionContextManager).getSessionContext(sessionKey);

    symphonyAi.endConversation(sessionKey);

    verify(aiConversationManager, times(1)).removeConversation(sessionKey);
  }

  @Test
  public void testSendMessage() {
    SymphonyAiSessionKey sessionKey = symphonyAi.getSessionKey(MOCK_USER_ID, MOCK_STREAM_ID);
    Set<AiResponseIdentifier> responseIdentifierSet = Collections.singleton(responseIdentifier);
    SymphonyAiMessage message = new SymphonyAiMessage(symMessage);

    doReturn(sessionContext).when(aiSessionContextManager).getSessionContext(sessionKey);

    symphonyAi.sendMessage(message, responseIdentifierSet, sessionKey);

    verify(aiResponder, times(1)).respond(sessionContext);

    ArgumentCaptor<AiResponse> responseParam = ArgumentCaptor.forClass(AiResponse.class);

    verify(aiResponder, times(1)).addResponse(eq(sessionContext), responseParam.capture());

    AiResponse response = responseParam.getValue();

    assertEquals(message, response.getMessage());
    assertEquals(responseIdentifierSet, response.getRespondTo());
  }
}
