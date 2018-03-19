package org.symphonyoss.symphony.bots.helpdesk.bot.config;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by campidelli on 19/03/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class HelpDeskBotConfigTest {

  private static final String ORIGINAL_STREAM_ID = "7xUD+mAOCfLBJ63wX2d4VH///p3PhqCfdA==";
  private static final String MODIFIED_STREAM_ID = "7xUD-mAOCfLBJ63wX2d4VH___p3PhqCfdA";

  private HelpDeskBotConfig config;

  @Before
  public void init() {
    config = new HelpDeskBotConfig();
  }

  @Test
  public void testGetAgentStreamId() {
    config.setAgentStreamId(ORIGINAL_STREAM_ID);

    String value = config.getAgentStreamId();

    assertEquals(MODIFIED_STREAM_ID, value);
  }

  @Test
  public void testGetAgentStreamIdNull() {
    String value = config.getAgentStreamId();

    assertNull(value);
  }
}
