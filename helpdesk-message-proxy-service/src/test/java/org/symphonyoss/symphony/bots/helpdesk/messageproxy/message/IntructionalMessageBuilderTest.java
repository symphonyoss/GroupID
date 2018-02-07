package org.symphonyoss.symphony.bots.helpdesk.messageproxy.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Created by nick.tarsillo on 12/14/17.
 */
public class IntructionalMessageBuilderTest {
  private static final String TEST_COMMAND = "<mention=\"%s\"/> Test Command";
  private static final String TEST_MESSAGE = "Test Message";
  private static final Long TEST_USER_ID = 1L;

  @Test
  public void testMessageBuilderBuild() {
    InstructionalMessageBuilder builder = new InstructionalMessageBuilder()
        .message(TEST_MESSAGE).command(TEST_COMMAND).mentionUserId(TEST_USER_ID);

    SymMessage expected = new SymMessage();
    expected.setMessage("<messageML>Use <mention=\"1\"/> Test Command Test Message</messageML>");
    assertEquals(expected.getMessage(), builder.build().getMessage());
  }
}
