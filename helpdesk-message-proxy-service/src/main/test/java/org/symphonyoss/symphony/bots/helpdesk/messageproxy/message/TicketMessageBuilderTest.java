package org.symphonyoss.symphony.bots.helpdesk.messageproxy.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Created by rsanchez on 20/12/17.
 */
public class TicketMessageBuilderTest {

  private static final String TEST_MESSAGE = "test message";

  @Test
  public void testMessage() {
    TicketMessageBuilder builder = new MockTicketMessageBuilder(TEST_MESSAGE);
    String expectedMessage = builder.getMessageTemplate();

    SymMessage message = builder.build();
    assertEquals(expectedMessage, message.getMessage());
    assertEquals("{\"helpdesk\":{\"message\":\"test message\"}}", message.getEntityData());
  }

}
