package org.symphonyoss.symphony.bots.helpdesk.messageproxy.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Created by rsanchez on 20/12/17.
 */
public class IdleMessageBuilderTest {

  private static final String MOCK_BOT_URL = "https://test.symphony.com/helpdesk-bot";

  private static final String MOCK_SERVICE_URL = "https://test.symphony.com/helpdesk";

  private static final String MOCK_TICKET = "ABCDEFG";

  private static final String MOCK_STREAM = "Zs+nx3pQh3+XyKlT5B15m3///p/zHfetdA==";

  private static final String TEST_MESSAGE = "test message";

  private static final String EXPECTED_ENTITY = "{\"helpdesk\":{\"type\":\"com.symphony.bots"
      + ".helpdesk.event.ticket\",\"version\":\"1.0\",\"joinUrl\":\"https://test.symphony"
      + ".com/helpdesk-bot/v1/ticket/ABCDEFG/join\",\"ticketUrl\":\"https://test.symphony"
      + ".com/helpdesk/v1/ticket/ABCDEFG\",\"ticketId\":\"ABCDEFG\","
      + "\"streamId\":\"Zs+nx3pQh3+XyKlT5B15m3///p/zHfetdA==\",\"message\":\"test message\"}}";

  @Test
  public void testMessage() {
    IdleMessageBuilder builder = new IdleMessageBuilder();
    String expectedMessage = builder.getMessageTemplate();

    SymMessage message = builder.message(TEST_MESSAGE)
        .botHost(MOCK_BOT_URL)
        .serviceHost(MOCK_SERVICE_URL)
        .ticketId(MOCK_TICKET)
        .streamId(MOCK_STREAM)
        .build();

    assertEquals(expectedMessage, message.getMessage());
    assertEquals(EXPECTED_ENTITY, message.getEntityData());
  }

}
