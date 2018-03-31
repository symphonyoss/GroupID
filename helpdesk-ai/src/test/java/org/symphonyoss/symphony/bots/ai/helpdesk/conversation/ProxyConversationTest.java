package org.symphonyoss.symphony.bots.ai.helpdesk.conversation;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiResponse;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.ai.model.SymphonyAiSessionKey;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.MakerCheckerService;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.Collections;
import java.util.Set;

/**
 * Unit tests for {@link ProxyConversation}
 * Created by robson on 26/03/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProxyConversationTest {

  private static final String MESSAGE_ID = "MSG_ID";

  private static final String STREAM_ID = "STREAM";

  @Mock
  private MakerCheckerService makerCheckerService;

  @Mock
  private AiSessionContext sessionContext;

  @Mock
  private AiResponder responder;

  @Mock
  private ProxyIdleTimer timer;

  @Mock
  private SymphonyAiSessionKey sessionKey;

  private ProxyConversation proxyConversation;

  @Before
  public void init() {
    doReturn(sessionKey).when(sessionContext).getAiSessionKey();
    this.proxyConversation = new ProxyConversation(false, sessionContext, makerCheckerService);
  }

  @Test
  public void testDispatchMessage() {
    SymphonyAiMessage aiMessage = new SymphonyAiMessage(new SymMessage());

    doReturn(true).when(makerCheckerService).allChecksPass(any(SymMessage.class));

    proxyConversation.setProxyIdleTimer(timer);

    proxyConversation.onMessage(responder, aiMessage);

    verify(responder, times(1)).respond(any(AiResponse.class));
    verify(timer, times(1)).reset();
  }

  @Test
  public void testDispatchMakerChecker() {
    Set<String> proxyToIds = Collections.singleton(STREAM_ID);

    SymMessage message = new SymMessage();
    message.setId(MESSAGE_ID);

    SymphonyAiMessage aiMessage = new SymphonyAiMessage(message);

    doReturn(false).when(makerCheckerService).allChecksPass(any(SymMessage.class));
    doReturn(Collections.singleton(message)).when(makerCheckerService)
        .getMakerCheckerMessages(any(SymMessage.class), eq(proxyToIds));

    proxyConversation.addProxyId(STREAM_ID);

    proxyConversation.onMessage(responder, aiMessage);

    verify(makerCheckerService, times(1)).sendMakerCheckerMesssage(message, MESSAGE_ID, proxyToIds);
    verify(timer, never()).reset();
  }


}
