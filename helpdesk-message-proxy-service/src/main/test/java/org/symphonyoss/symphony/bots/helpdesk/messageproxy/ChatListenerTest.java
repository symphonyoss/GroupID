package org.symphonyoss.symphony.bots.helpdesk.messageproxy;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.bots.ai.HelpDeskAi;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.io.IOException;
import java.util.Collections;

/**
 * Created by rsanchez on 18/12/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class ChatListenerTest {

  private static final String MOCK_TEXT = "Mock text";

  @Mock
  private TicketManagerService ticketManagerService;

  @Mock
  private HelpDeskAi helpDeskAi;

  @Test
  public void testWithoutContent() {
    SymMessage message = new SymMessage();

    ChatListener listener = new ChatListener(ticketManagerService, helpDeskAi);
    listener.ready();

    listener.onMessage(message);

    verify(ticketManagerService, never()).messageReceived(message);
    verify(helpDeskAi, never()).onMessage(message);

    message.setAttachments(Collections.emptyList());

    verify(ticketManagerService, never()).messageReceived(message);
    verify(helpDeskAi, never()).onMessage(message);
  }

  @Test
  public void testEmptyMessage() throws IOException {
    SymMessage message = new SymMessage();
    message.setAttachments(Collections.singletonList(new SymAttachmentInfo()));

    ChatListener listener = new ChatListener(ticketManagerService, helpDeskAi);
    listener.ready();

    listener.onMessage(message);

    verify(ticketManagerService, times(1)).messageReceived(message);
    verify(helpDeskAi, times(1)).onMessage(message);
  }

  @Test
  public void testNotReady() {
    SymMessage message = new SymMessage();
    message.setMessageText(MOCK_TEXT);

    ChatListener listener = new ChatListener(ticketManagerService, helpDeskAi);
    listener.onMessage(message);

    verify(ticketManagerService, never()).messageReceived(message);
    verify(helpDeskAi, never()).onMessage(message);
  }

  @Test
  public void testReady() {
    SymMessage message = new SymMessage();
    message.setMessageText(MOCK_TEXT);

    ChatListener listener = new ChatListener(ticketManagerService, helpDeskAi);
    listener.ready();

    listener.onMessage(message);

    verify(ticketManagerService, times(1)).messageReceived(message);
    verify(helpDeskAi, times(1)).onMessage(message);
  }
}
