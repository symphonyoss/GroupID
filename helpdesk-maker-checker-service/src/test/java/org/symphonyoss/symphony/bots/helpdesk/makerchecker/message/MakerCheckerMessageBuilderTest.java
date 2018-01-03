package org.symphonyoss.symphony.bots.helpdesk.makerchecker.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.clients.model.SymMessage;

@RunWith(MockitoJUnitRunner.class)
public class MakerCheckerMessageBuilderTest {

  private static final String MAKER_CHECKER_ID = "6JFEVDBXP54";

  private static final String BOT_HOST = "192.168.0.2";

  private static final String SERVICE_HOST = "192.168.0.3";

  private static final long MAKER_ID = 9826885203304l;

  private static final String STREAM_ID = "pT4nMlbsOgqfgPuQTQO9yn___p9c7xwsdA";

  private static final long TIMESTAMP = 545465456465l;

  private static final String MESSAGE_ID = "1322154564545";

  private static final String GROUP_ID = "test";

  private static final String ATTACHMENT_ID =
      "internal_9826885173254%2FxtpDCplNtIJluaYgvQkfGg%3D%3D";

  private static final String PROXY_TO_STREAM_ID = "pT4nMlbsOgqfgPuQTQO9yn___p9c7xwsdA";

  private static final String EXPECTED_ENTITY_DATA = "{\"makerchecker\":{\"type\":\"com.symphony"
      + ".bots.helpdesk.event.makerchecker\",\"version\":\"1.0\","
      + "\"attachmentUrl\":\"192.168.0.3/v1/makerchecker/6JFEVDBXP54\","
      + "\"approveUrl\":\"192.168.0.2/v1/makerchecker/approve\","
      + "\"denyUrl\":\"192.168.0.2/v1/makerchecker/deny\",\"makerId\":9826885203304,"
      + "\"streamId\":\"pT4nMlbsOgqfgPuQTQO9yn___p9c7xwsdA\","
      + "\"proxyToStreamIds\":[\"pT4nMlbsOgqfgPuQTQO9yn___p9c7xwsdA\"],"
      + "\"timestamp\":545465456465,\"messageId\":\"1322154564545\",\"groupId\":\"test\","
      + "\"makerCheckerId\":\"6JFEVDBXP54\","
      + "\"attachmentId\":\"internal_9826885173254%2FxtpDCplNtIJluaYgvQkfGg%3D%3D\"}}";

  private static final String EXPECTED_MESSAGE = "<messageML>    <div class=\"entity\" "
      + "data-entity-id=\"makerchecker\">        <card class=\"barStyle\">            <header>   "
      + "             The above message contains an attachment and has therefore not been sent. "
      + "Please have a checker approve this message.            </header>        </card>    "
      + "</div></messageML>";

  private MakerCheckerMessageBuilder makerCheckerMessageBuilder;

  @Test
  public void testBuild() {
    makerCheckerMessageBuilder = mockValues();

    SymMessage symMessage = makerCheckerMessageBuilder.build();

    assertEquals(EXPECTED_ENTITY_DATA, symMessage.getEntityData());
    assertEquals(EXPECTED_MESSAGE, symMessage.getMessage());
  }

  private MakerCheckerMessageBuilder mockValues() {
    MakerCheckerMessageBuilder makerCheckerMessageBuilder = new MakerCheckerMessageBuilder();
    makerCheckerMessageBuilder.makerCheckerId(MAKER_CHECKER_ID);
    makerCheckerMessageBuilder.botHost(BOT_HOST);
    makerCheckerMessageBuilder.serviceHost(SERVICE_HOST);
    makerCheckerMessageBuilder.makerId(MAKER_ID);
    makerCheckerMessageBuilder.streamId(STREAM_ID);
    makerCheckerMessageBuilder.timestamp(TIMESTAMP);
    makerCheckerMessageBuilder.messageId(MESSAGE_ID);
    makerCheckerMessageBuilder.groupId(GROUP_ID);
    makerCheckerMessageBuilder.attachmentId(ATTACHMENT_ID);
    makerCheckerMessageBuilder.addProxyToStreamId(PROXY_TO_STREAM_ID);

    return makerCheckerMessageBuilder;
  }

}
