package org.symphonyoss.symphony.bots.helpdesk.messageproxy.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Created by rsanchez on 20/12/17.
 */
public class ClaimMessageBuilderTest {

  private static final String MOCK_BOT_URL = "https://test.symphony.com/helpdesk-bot";

  private static final String MOCK_SERVICE_URL = "https://test.symphony.com/helpdesk";

  private static final String MOCK_TICKET = "ABCDEFG";

  private static final String MOCK_STREAM = "Zs+nx3pQh3+XyKlT5B15m3///p/zHfetdA==";

  private static final String MOCK_USERNAME = "test user";

  private static final String TEST_HEADER = "test header";

  private static final String TEST_COMPANY = "test company";

  private static final String TEST_MESSAGE = "test message";

  private static final String EXPECTED_ENTITY = "{\"helpdesk\":{\"type\":\"com.symphony.bots"
      + ".helpdesk.event.ticket\",\"version\":\"1.0\",\"claimUrl\":\"https://test.symphony"
      + ".com/helpdesk-bot/v1/ticket/ABCDEFG/accept\",\"joinUrl\":\"https://test.symphony"
      + ".com/helpdesk-bot/v1/ticket/ABCDEFG/join\",\"ticketUrl\":\"https://test.symphony"
      + ".com/helpdesk/v1/ticket/ABCDEFG\",\"ticketId\":\"ABCDEFG\",\"state\":\"UNSERVICED\","
      + "\"streamId\":\"Zs+nx3pQh3+XyKlT5B15m3///p/zHfetdA==\",\"user\":{\"type\":\"com.symphony"
      + ".bots.helpdesk.event.ticket.user\",\"version\":\"1.0\",\"displayName\":\"test user\"},"
      + "\"message\":{\"type\":\"com.symphony.bots.helpdesk.event.ticket.message\","
      + "\"version\":\"1.0\",\"header\":\"test header\",\"company\":\"test company\","
      + "\"customer\":\"test user\",\"question\":\"test message\"}}}";

  @Test
  public void testMessage() {
    ClaimMessageBuilder builder = new ClaimMessageBuilder();
    String expectedMessage = builder.getMessageTemplate();

    SymMessage message = builder.ticketState(TicketClient.TicketStateType.UNSERVICED.getState())
        .username(MOCK_USERNAME)
        .header(TEST_HEADER)
        .company(TEST_COMPANY)
        .question(TEST_MESSAGE)
        .botHost(MOCK_BOT_URL)
        .serviceHost(MOCK_SERVICE_URL)
        .ticketId(MOCK_TICKET)
        .streamId(MOCK_STREAM)
        .build();

    assertEquals(expectedMessage, message.getMessage());
    assertEquals(EXPECTED_ENTITY, message.getEntityData());
  }

}
