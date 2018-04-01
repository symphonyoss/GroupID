package org.symphonyoss.symphony.bots.ai.helpdesk.message;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.AttachmentsException;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.clients.AttachmentsClient;
import org.symphonyoss.symphony.clients.MessagesClient;
import org.symphonyoss.symphony.clients.UsersClient;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lumoura on 21/02/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class MessageProducerTest {
  private String EMPTY_MESSAGE = "<div data-format=\"PresentationML\" data-version=\"2.0\"></div>";
  private String COMPLEX_MESSAGE =
      "<div data-format=\"PresentationML\" data-version=\"2.0\">Ok, this is a pretty complex "
          + "message. It has <b>BOLD CHARACTERS</b>, <i>ITALIC CHARACTERS</i>, and even some "
          + "ñóñ-îśò çĥæŕⒶ<span class=\"entity\" data-entity-id=\"emoji1\">©</span> |èrź! &quot; "
          + "/ \\ | &quot; ` ' ' &quot;<br/>There is even a new line!<br/>We could even attach "
          + "some emojis: <span class=\"entity\" data-entity-id=\"emoji2\">\uD83D\uDE23</span> "
          + "<span class=\"entity\" data-entity-id=\"emoji3\">\uD83D\uDE17</span> <span "
          + "class=\"entity\" data-entity-id=\"emoji4\">\uD83D\uDE0D</span> <span "
          + "class=\"entity\" data-entity-id=\"emoji5\">\uD83D\uDE0E</span> <span "
          + "class=\"entity\" data-entity-id=\"emoji6\">\uD83D\uDE00</span> <span "
          + "class=\"entity\" data-entity-id=\"emoji7\">\uD83D\uDE01</span> <span "
          + "class=\"entity\" data-entity-id=\"emoji8\">\uD83D\uDE06</span> <span "
          + "class=\"entity\" data-entity-id=\"emoji9\">\uD83D\uDE05</span> <span "
          + "class=\"entity\" data-entity-id=\"emoji10\">\uD83D\uDE03</span> <span "
          + "class=\"entity\" data-entity-id=\"emoji12\">\uD83C\uDDE8\uD83C\uDDEE</span> <span "
          + "class=\"entity\" data-entity-id=\"emoji13\">\uD83C\uDDEE\uD83C\uDDE8</span> <span "
          + "class=\"entity\" data-entity-id=\"emoji14\">\uD83C\uDDF9\uD83C\uDDFB</span> <span "
          + "class=\"entity\" data-entity-id=\"emoji15\">\uD83C\uDDF9\uD83C\uDDF0</span> <span "
          + "class=\"entity\" data-entity-id=\"emoji16\">\uD83C\uDDFF\uD83C\uDDFC</span><br"
          + "/>Finally,<ul><li>we</li><li>have</li><li>a</li><li>bullet</li><li>list</li></ul"
          + "></div>";
  private String COMPLEX_MESSAGE_ENTITY_DATA =
      "{\"emoji1\":{\"type\":\"com.symphony.emoji\",\"version\":\"1.0\",\"data\":{\"short\""
          + ":\"copyright\",\"size\":\"normal\",\"unicode\":\"©\"}},\"emoji2\":{\"type\":\"com"
          + ".symphony"
          + ".emoji\",\"version\":\"1.0\",\"data\":{\"shortcode\":\"persevere\","
          + "\"size\":\"normal\","
          + "\"unicode\":\"\uD83D\uDE23\"}},\"emoji3\":{\"type\":\"com.symphony.emoji\","
          + "\"version\":\"1.0\",\"data\":{\"shortcode\":\"kissing\",\"size\":\"normal\","
          + "\"unicode\":\"\uD83D\uDE17\"}},\"emoji4\":{\"type\":\"com.symphony.emoji\","
          + "\"version\":\"1.0\",\"data\":{\"shortcode\":\"heart_eyes\",\"size\":\"normal\","
          + "\"unicode\":\"\uD83D\uDE0D\"}},\"emoji5\":{\"type\":\"com.symphony.emoji\","
          + "\"version\":\"1.0\",\"data\":{\"shortcode\":\"sunglasses\",\"size\":\"normal\","
          + "\"unicode\":\"\uD83D\uDE0E\"}},\"emoji6\":{\"type\":\"com.symphony.emoji\","
          + "\"version\":\"1.0\",\"data\":{\"shortcode\":\"grinning\",\"size\":\"normal\","
          + "\"unicode\":\"\uD83D\uDE00\"}},\"emoji7\":{\"type\":\"com.symphony.emoji\","
          + "\"version\":\"1.0\",\"data\":{\"shortcode\":\"grin\",\"size\":\"normal\","
          + "\"unicode\":\"\uD83D\uDE01\"}},\"emoji8\":{\"type\":\"com.symphony.emoji\","
          + "\"version\":\"1.0\",\"data\":{\"shortcode\":\"laughing\",\"size\":\"normal\","
          + "\"unicode\":\"\uD83D\uDE06\"}},\"emoji9\":{\"type\":\"com.symphony.emoji\","
          + "\"version\":\"1.0\",\"data\":{\"shortcode\":\"sweat_smile\",\"size\":\"normal\","
          + "\"unicode\":\"\uD83D\uDE05\"}},\"emoji10\":{\"type\":\"com.symphony.emoji\","
          + "\"version\":\"1.0\",\"data\":{\"shortcode\":\"smiley\",\"size\":\"normal\","
          + "\"unicode\":\"\uD83D\uDE03\"}},\"emoji11\":{\"type\":\"com.symphony.emoji\","
          + "\"version\":\"1.0\",\"data\":{\"shortcode\":\"rofl\",\"size\":\"normal\"}},"
          + "\"emoji12\":{\"type\":\"com.symphony.emoji\",\"version\":\"1.0\","
          + "\"data\":{\"shortcode\":\"flag_ci\",\"size\":\"normal\","
          + "\"unicode\":\"\uD83C\uDDE8\uD83C\uDDEE\"}},\"emoji13\":{\"type\":\"com.symphony"
          + ".emoji\","
          + "\"version\":\"1.0\",\"data\":{\"shortcode\":\"flag_ic\",\"size\":\"normal\","
          + "\"unicode\":\"\uD83C\uDDEE\uD83C\uDDE8\"}},\"emoji14\":{\"type\":\"com.symphony"
          + ".emoji\","
          + "\"version\":\"1.0\",\"data\":{\"shortcode\":\"flag_tv\",\"size\":\"normal\","
          + "\"unicode\":\"\uD83C\uDDF9\uD83C\uDDFB\"}},\"emoji15\":{\"type\":\"com.symphony"
          + ".emoji\","
          + "\"version\":\"1.0\",\"data\":{\"shortcode\":\"flag_tk\",\"size\":\"normal\","
          + "\"unicode\":\"\uD83C\uDDF9\uD83C\uDDF0\"}},\"emoji16\":{\"type\":\"com.symphony"
          + ".emoji\","
          + "\"version\":\"1.0\",\"data\":{\"shortcode\":\"flag_zw\",\"size\":\"normal\","
          + "\"unicode\":\"\uD83C\uDDFF\uD83C\uDDFC\"}}}";
  private String CHIME_MESSAGE =
      "<div data-format=\"PresentationML\"data-version=\"2.0\"><audio src=\""
          + "https://asset.symphony.com/symphony/audio/chime.mp3\" autoplay=\"true\"/></div>";
  private String EXPECTED_CHIME_MESSAGE =
      "<messageML><audio src=\"https://asset.symphony.com/symphony/audio/chime.mp3\" autoplay"
          + "=\"true\"></audio></messageML>";


  private static final Long USER_ID = Long.valueOf(1234);
  private static final int ATTACHMENTS_COUNT = 10;
  private static final String USER_NAME = "Ultimate Tester";

  @Mock
  private MembershipClient membershipClient;

  @Mock
  private SymphonyClient symphonyClient;

  @Mock
  private MessagesClient messagesClient;

  @Mock
  private UsersClient usersClient;

  @Mock
  private Membership membership;

  @Mock
  private SymUser symUser;

  @Mock
  private AttachmentsClient attachmentsClient;

  private MessageProducer messageProducer;

  private AiMessage aiMessage;

  private byte[] fileBytes = "filebytes".getBytes();

  private String streamId = "random_stream_id";

  @Before
  public void init() throws UsersClientException, AttachmentsException {
    aiMessage = new AiMessage("");
    aiMessage.setFromUserId(USER_ID);
    doReturn(messagesClient).when(symphonyClient).getMessagesClient();
    doReturn(usersClient).when(symphonyClient).getUsersClient();
    doReturn(symUser).when(usersClient).getUserFromId(anyLong());
    doReturn(USER_NAME).when(symUser).getDisplayName();
    doReturn(membership).when(membershipClient).getMembership(anyLong());
    doReturn(attachmentsClient).when(symphonyClient).getAttachmentsClient();
    doReturn(fileBytes).when(attachmentsClient)
        .getAttachmentData(any(SymAttachmentInfo.class), any(SymMessage.class));
    messageProducer = new MessageProducer(membershipClient, symphonyClient);
  }

  @Test
  public void testBotMessage() throws MessagesException {
    String BOT_MESSAGE =
        "Thank you for contacting us, this session is now over. Any new messages in this chat will "
            + "be delivered to the JPM Equity Team as a new session.";
    String EXPECTED_BOT_MESSAGE = "<messageML>" + BOT_MESSAGE + "</messageML>";
    aiMessage.setAiMessage(BOT_MESSAGE);

    messageProducer.publishMessage(aiMessage, streamId);
    ArgumentCaptor<SymMessage> captor = ArgumentCaptor.forClass(SymMessage.class);
    verify(messagesClient).sendMessage(any(SymStream.class), captor.capture());

    SymMessage sentMessage = captor.getValue();
    assertEquals(EXPECTED_BOT_MESSAGE, sentMessage.getMessage());
  }

  @Test
  public void testAgentChime() throws MessagesException {
    doReturn("AGENT").when(membership).getType();
    aiMessage.setMessageData(CHIME_MESSAGE);

    messageProducer.publishMessage(aiMessage, streamId);
    ArgumentCaptor<SymMessage> captor = ArgumentCaptor.forClass(SymMessage.class);
    verify(messagesClient).sendMessage(any(SymStream.class), captor.capture());

    SymMessage sentMessage = captor.getValue();
    assertEquals(EXPECTED_CHIME_MESSAGE, sentMessage.getMessage());
  }

  @Test
  public void testAgentComplexMessage() throws MessagesException {
    String EXPECTED_COMPLEX_MESSAGE =
        "<messageML>\nOk, this is a pretty complex message. It has <b>BOLD CHARACTERS</b>, "
            + "<i>ITALIC CHARACTERS</i>, and even some ñóñ-îśò çĥæŕⒶ<span class=\"entity\" "
            + "data-entity-id=\"emoji1\">©</span> |èrź! \" / \\ | \" ` ' ' \"<br/>There is even a "
            + "new line!<br/>We could even attach some emojis: <span class=\"entity\" "
            + "data-entity-id=\"emoji2\">\uD83D\uDE23</span> <span class=\"entity\" "
            + "data-entity-id=\"emoji3\">\uD83D\uDE17</span> <span class=\"entity\" "
            + "data-entity-id=\"emoji4\">\uD83D\uDE0D</span> <span class=\"entity\" "
            + "data-entity-id=\"emoji5\">\uD83D\uDE0E</span> <span class=\"entity\" "
            + "data-entity-id=\"emoji6\">\uD83D\uDE00</span> <span class=\"entity\" "
            + "data-entity-id=\"emoji7\">\uD83D\uDE01</span> <span class=\"entity\" "
            + "data-entity-id=\"emoji8\">\uD83D\uDE06</span> <span class=\"entity\" "
            + "data-entity-id=\"emoji9\">\uD83D\uDE05</span> <span class=\"entity\" "
            + "data-entity-id=\"emoji10\">\uD83D\uDE03</span> <span class=\"entity\" "
            + "data-entity-id=\"emoji12\">\uD83C\uDDE8\uD83C\uDDEE</span> <span class=\"entity\" "
            + "data-entity-id=\"emoji13\">\uD83C\uDDEE\uD83C\uDDE8</span> <span class=\"entity\" "
            + "data-entity-id=\"emoji14\">\uD83C\uDDF9\uD83C\uDDFB</span> <span class=\"entity\" "
            + "data-entity-id=\"emoji15\">\uD83C\uDDF9\uD83C\uDDF0</span> <span class=\"entity\" "
            + "data-entity-id=\"emoji16\">\uD83C\uDDFF\uD83C\uDDFC</span><br/>Finally,<ul>\n "
            + "<li>we</li>\n <li>have</li>\n <li>a</li>\n <li>bullet</li>\n "
            + "<li>list</li>\n</ul></messageML>";
    doReturn("AGENT").when(membership).getType();
    aiMessage.setMessageData(COMPLEX_MESSAGE);
    aiMessage.setEntityData(COMPLEX_MESSAGE_ENTITY_DATA);

    messageProducer.publishMessage(aiMessage, streamId);
    ArgumentCaptor<SymMessage> captor = ArgumentCaptor.forClass(SymMessage.class);
    verify(messagesClient).sendMessage(any(SymStream.class), captor.capture());

    SymMessage sentMessage = captor.getValue();
    assertEquals(EXPECTED_COMPLEX_MESSAGE, sentMessage.getMessage());
    assertEquals(COMPLEX_MESSAGE_ENTITY_DATA, sentMessage.getEntityData());
  }

  @Test
  public void testAgentAttachment() throws MessagesException {
    String tmpDir = System.getProperty("java.io.tmpdir");
    doReturn("AGENT").when(membership).getType();
    File file = new File(tmpDir + File.separator + "test_directory" + File.separator + "test_file");
    aiMessage.setMessageData(EMPTY_MESSAGE);
    aiMessage.setAttachment(file);

    messageProducer.publishMessage(aiMessage, streamId);
    ArgumentCaptor<SymMessage> captor = ArgumentCaptor.forClass(SymMessage.class);
    verify(messagesClient).sendMessage(any(SymStream.class), captor.capture());

    SymMessage sentMessage = captor.getValue();
    assertEquals(file, sentMessage.getAttachment());
  }

  @Test
  public void testClientChime() throws MessagesException {
    doReturn("CLIENT").when(membership).getType();
    aiMessage.setMessageData(CHIME_MESSAGE);

    messageProducer.publishMessage(aiMessage, streamId);
    ArgumentCaptor<SymMessage> captor = ArgumentCaptor.forClass(SymMessage.class);
    verify(messagesClient).sendMessage(any(SymStream.class), captor.capture());

    SymMessage sentMessage = captor.getValue();
    assertEquals(EXPECTED_CHIME_MESSAGE, sentMessage.getMessage());
  }

  @Test
  public void testClientComplexMessage() throws MessagesException {
    String EXPECTED_USER_COMPLEX_MESSAGE =
        "<messageML><b>" + USER_NAME
            + "</b>: \nOk, this is a pretty complex message. It has <b>BOLD "
            + "CHARACTERS</b>, <i>ITALIC CHARACTERS</i>, and even some ñóñ-îśò çĥæŕⒶ<span "
            + "class=\"entity\" data-entity-id=\"emoji1\">©</span> |èrź! \" / \\ | \" ` ' ' "
            + "\"<br/>There is even a new line!<br/>We could even attach some emojis: <span "
            + "class=\"entity\" data-entity-id=\"emoji2\">\uD83D\uDE23</span> <span "
            + "class=\"entity\" data-entity-id=\"emoji3\">\uD83D\uDE17</span> <span "
            + "class=\"entity\" data-entity-id=\"emoji4\">\uD83D\uDE0D</span> <span "
            + "class=\"entity\" data-entity-id=\"emoji5\">\uD83D\uDE0E</span> <span "
            + "class=\"entity\" data-entity-id=\"emoji6\">\uD83D\uDE00</span> <span "
            + "class=\"entity\" data-entity-id=\"emoji7\">\uD83D\uDE01</span> <span "
            + "class=\"entity\" data-entity-id=\"emoji8\">\uD83D\uDE06</span> <span "
            + "class=\"entity\" data-entity-id=\"emoji9\">\uD83D\uDE05</span> <span "
            + "class=\"entity\" data-entity-id=\"emoji10\">\uD83D\uDE03</span> <span "
            + "class=\"entity\" data-entity-id=\"emoji12\">\uD83C\uDDE8\uD83C\uDDEE</span> <span "
            + "class=\"entity\" data-entity-id=\"emoji13\">\uD83C\uDDEE\uD83C\uDDE8</span> <span "
            + "class=\"entity\" data-entity-id=\"emoji14\">\uD83C\uDDF9\uD83C\uDDFB</span> <span "
            + "class=\"entity\" data-entity-id=\"emoji15\">\uD83C\uDDF9\uD83C\uDDF0</span> <span "
            + "class=\"entity\" data-entity-id=\"emoji16\">\uD83C\uDDFF\uD83C\uDDFC</span><br"
            + "/>Finally,<ul>\n <li>we</li>\n <li>have</li>\n <li>a</li>\n <li>bullet</li>\n "
            + "<li>list</li>\n</ul></messageML>";
    doReturn("CLIENT").when(membership).getType();
    aiMessage.setMessageData(COMPLEX_MESSAGE);
    aiMessage.setEntityData(COMPLEX_MESSAGE_ENTITY_DATA);

    messageProducer.publishMessage(aiMessage, streamId);
    ArgumentCaptor<SymMessage> captor = ArgumentCaptor.forClass(SymMessage.class);
    verify(messagesClient).sendMessage(any(SymStream.class), captor.capture());

    SymMessage sentMessage = captor.getValue();
    assertEquals(EXPECTED_USER_COMPLEX_MESSAGE, sentMessage.getMessage());
    assertEquals(COMPLEX_MESSAGE_ENTITY_DATA, sentMessage.getEntityData());
  }

  @Test
  public void testClientMultipleAttachments() throws MessagesException {
    String tmpDir = System.getProperty("java.io.tmpdir");
    doReturn("CLIENT").when(membership).getType();
    List<SymAttachmentInfo> attachmentInfoList = new ArrayList<SymAttachmentInfo>() {};
    for (int i = 0; i < ATTACHMENTS_COUNT; i++) {
      SymAttachmentInfo attachmentInfo = new SymAttachmentInfo();
      attachmentInfo.setId(String.valueOf(i));
      attachmentInfo.setName("file_" + String.valueOf(i));
      attachmentInfoList.add(attachmentInfo);
    }
    aiMessage.setAttachments(attachmentInfoList);
    aiMessage.setMessageData(EMPTY_MESSAGE);

    messageProducer.publishMessage(aiMessage, streamId);
    ArgumentCaptor<SymMessage> captor = ArgumentCaptor.forClass(SymMessage.class);
    verify(messagesClient, times(ATTACHMENTS_COUNT)).sendMessage(any(SymStream.class),
        captor.capture());
    assertEquals(attachmentInfoList, captor.getValue().getAttachments());
    List<SymMessage> sentMessageList = captor.getAllValues();
    for (int i = 0; i < ATTACHMENTS_COUNT; i++) {
      File file = new File(
          tmpDir + File.separator + attachmentInfoList.get(i).getId() + File.separator
              + attachmentInfoList.get(i).getName());
      assertEquals(file, sentMessageList.get(i).getAttachment());
    }
  }
}
