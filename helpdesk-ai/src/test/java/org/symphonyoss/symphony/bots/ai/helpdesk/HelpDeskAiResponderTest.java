package org.symphonyoss.symphony.bots.ai.helpdesk;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.ai.helpdesk.message.MessageProducer;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;

/**
 * Unit tests for {@link HelpDeskAiResponder}
 * Created by robson on 26/03/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class HelpDeskAiResponderTest {

  private static final String MOCK_STREAM_ID = "STREAM";

  @Mock
  private MessageProducer messageProducer;

  @Mock
  private SymphonyAiMessage symphonyAiMessage;

  @Test
  public void testPublishMessage() {
    HelpDeskAiResponder responder = new HelpDeskAiResponder(messageProducer);
    responder.publishMessage(MOCK_STREAM_ID, symphonyAiMessage);

    verify(messageProducer, times(1)).publishMessage(symphonyAiMessage, MOCK_STREAM_ID);
  }
}
