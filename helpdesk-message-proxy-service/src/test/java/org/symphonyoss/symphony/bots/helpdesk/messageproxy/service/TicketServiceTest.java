package org.symphonyoss.symphony.bots.helpdesk.messageproxy.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.HelpDeskBotInfo;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.HelpDeskServiceInfo;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.InstructionalMessageConfig;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.message.ClaimMessageBuilder;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.message.InstructionalMessageBuilder;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.model.UserInfo;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.clients.MessagesClient;
import org.symphonyoss.symphony.clients.UsersClient;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymRoomAttributes;
import org.symphonyoss.symphony.clients.model.SymRoomDetail;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick.tarsillo on 12/14/17.
 */
public class TicketServiceTest {
  private static final String TEST_TICKET_ID = "TEST_TICKET_ID";

  private static final String TEST_SERVICE_STREAM_ID = "TEST_SERVICE_STREAM";

  private static final String TEST_CLIENT_STREAM_ID = "TEST_CLIENT_STREAM";

  private static final String TEST_DISPLAY_NAME = "TEST_DISPLAY_NAME";

  private static final String TEST_CREATE_TICKET_MESSAGE = "TEST_CREATE";

  private static final String TEST_AGENT_STREAM = "TEST_AGENT_STREAM";

  private static final String CHIME_MESSAGE =
      "<div data-format=\"PresentationML\"data-version=\"2.0\"><audio src=\""
          + "https://asset.symphony.com/symphony/audio/chime.mp3\" autoplay=\"true\"/></div>";

  private static final String ATTACHMENT_MESSAGE = "This message contains an attachment!";

  private static final String TABLE_MESSAGE = "<div data-format=\"PresentationML\"data-version"
      + "=\"2.0\"><table><tr><td>text</td></tr></table></div>";

  private static final Long TEST_TIMESTAMP = 1L;

  private static final Long TEST_FROM_USER_ID = 2L;

  private TicketService ticketService;

  @Mock
  private UsersClient usersClient;

  @Mock
  private MessagesClient messagesClient;

  @Mock
  private TicketClient ticketClient;

  @Mock
  private SymphonyClient symphonyClient;

  @Before
  public void initMocks(){
    MockitoAnnotations.initMocks(this);

    when(symphonyClient.getUsersClient()).thenReturn(usersClient);
    when(symphonyClient.getMessagesClient()).thenReturn(messagesClient);
    when(symphonyClient.getLocalUser()).thenReturn(mock(SymUser.class));

    InstructionalMessageConfig instructionalMessageConfig = new InstructionalMessageConfig();
    instructionalMessageConfig.setCommand("TEST");

    HelpDeskBotInfo helpDeskBotInfo = mock(HelpDeskBotInfo.class);
    HelpDeskServiceInfo helpDeskServiceInfo = mock(HelpDeskServiceInfo.class);

    ticketService =
        new TicketService(TEST_AGENT_STREAM, null, TEST_CREATE_TICKET_MESSAGE, null, ticketClient,
            symphonyClient, instructionalMessageConfig, helpDeskBotInfo, helpDeskServiceInfo);
  }

  private SymMessage getTestInstructionalMessage() {
    InstructionalMessageBuilder messageBuilder = new InstructionalMessageBuilder();
    messageBuilder.command("TEST");

    return messageBuilder.build();
  }

  private SymMessage getTestClaimMessage() {
    String safeAgentStreamId = Base64.encodeBase64String(Base64.decodeBase64(TEST_AGENT_STREAM));
    ClaimMessageBuilder builder = new ClaimMessageBuilder();

    builder.streamId(safeAgentStreamId);
    builder.username(TEST_DISPLAY_NAME);

    return builder.build();
  }

  private SymMessage getTestSymMessage() {
    SymMessage symMessage = new SymMessage();
    symMessage.setFromUserId(TEST_FROM_USER_ID);
    symMessage.setStreamId(TEST_CLIENT_STREAM_ID);
    symMessage.setTimestamp(TEST_TIMESTAMP.toString());

    return symMessage;
  }

  private UserInfo getTestClient() throws UsersClientException {
    SymUser symUser = new SymUser();
    symUser.setId(TEST_FROM_USER_ID);
    symUser.setDisplayName(TEST_DISPLAY_NAME);
    when(usersClient.getUserFromId(TEST_FROM_USER_ID)).thenReturn(symUser);

    UserInfo client = new UserInfo();
    client.setUserId(TEST_FROM_USER_ID);
    client.setDisplayName(TEST_DISPLAY_NAME);

    return client;
  }

  private SymUser getTestUser() throws UsersClientException {
    SymUser symUser = new SymUser();
    symUser.setId(TEST_FROM_USER_ID);
    symUser.setDisplayName(TEST_DISPLAY_NAME);

    when(usersClient.getUserFromId(TEST_FROM_USER_ID)).thenReturn(symUser);

    return symUser;
  }

  @Test
  public void testCreateTicket() throws UsersClientException, MessagesException {
    SymMessage testSym = getTestSymMessage();

    Ticket mockTicket = mock(Ticket.class);
    when(ticketClient.createTicket(TEST_TICKET_ID, TEST_CLIENT_STREAM_ID, TEST_SERVICE_STREAM_ID,
        TEST_TIMESTAMP, getTestClient(), Boolean.TRUE, testSym.getId())).thenReturn(mockTicket);

    SymMessage symMessage = new SymMessage();
    symMessage.setMessageText(TEST_CREATE_TICKET_MESSAGE);

    SymStream stream = new SymStream();
    stream.setStreamId(TEST_CLIENT_STREAM_ID);
    when(messagesClient.sendMessage(stream, symMessage)).thenReturn(symMessage);
    stream.setStreamId(TEST_AGENT_STREAM);
    when(messagesClient.sendMessage(stream, getTestClaimMessage())).thenReturn(getTestClaimMessage());
    stream.setStreamId(TEST_SERVICE_STREAM_ID);
    when(messagesClient.sendMessage(stream, getTestInstructionalMessage()))
        .thenReturn(getTestInstructionalMessage());

    Room serviceStream = mockRoom();

    Ticket ticket = ticketService.createTicket(TEST_TICKET_ID, testSym, serviceStream);

    assertEquals("Ticket return", mockTicket, ticket);
  }

  @Test
  public void testSendTicketMessageWithChime() throws UsersClientException, MessagesException {
    SymUser user = getTestUser();

    SymMessage testSym = getTestSymMessage();
    testSym.setMessage(CHIME_MESSAGE);

    ticketService.sendTicketMessageToAgentStreamId(mock(Ticket.class), testSym);

    ArgumentCaptor<SymMessage> captor = ArgumentCaptor.forClass(SymMessage.class);
    verify(messagesClient).sendMessage(any(SymStream.class), captor.capture());
    SymMessage sentMessage = captor.getValue();

    assertTrue(sentMessage.getEntityData().contains("\"question\":\"" + user.getDisplayName() + " sent a chime!\""));
  }

  @Test
  public void testSendTicketMessageWithAttachmentOnly()
      throws UsersClientException, MessagesException {
    SymUser user = getTestUser();
    List<SymAttachmentInfo> attachments = new ArrayList<>();
    attachments.add(new SymAttachmentInfo());
    attachments.add(new SymAttachmentInfo());

    SymMessage testSym = getTestSymMessage();
    testSym.setMessageText("");
    testSym.setAttachments(attachments);

    ticketService.sendTicketMessageToAgentStreamId(mock(Ticket.class), testSym);

    ArgumentCaptor<SymMessage> captor = ArgumentCaptor.forClass(SymMessage.class);
    verify(messagesClient).sendMessage(any(SymStream.class), captor.capture());
    SymMessage sentMessage = captor.getValue();

    assertTrue(sentMessage.getEntityData().contains("\"question\":\"" + user.getDisplayName() + " sent an attachment!\""));
  }

  @Test
  public void testSendTicketMessageWithText() throws UsersClientException, MessagesException {
    getTestUser();
    List<SymAttachmentInfo> attachments = new ArrayList<>();
    attachments.add(new SymAttachmentInfo());
    attachments.add(new SymAttachmentInfo());

    SymMessage testSym = getTestSymMessage();
    testSym.setMessageText(ATTACHMENT_MESSAGE);
    testSym.setAttachments(attachments);

    ticketService.sendTicketMessageToAgentStreamId(mock(Ticket.class), testSym);

    ArgumentCaptor<SymMessage> captor = ArgumentCaptor.forClass(SymMessage.class);
    verify(messagesClient).sendMessage(any(SymStream.class), captor.capture());
    SymMessage sentMessage = captor.getValue();

    assertTrue(sentMessage.getEntityData().contains("\"question\":\"" + ATTACHMENT_MESSAGE));
  }

  @Test
  public void testSendTicketMessageWithTable() throws MessagesException, UsersClientException {
    SymUser user = getTestUser();

    SymMessage testSym = getTestSymMessage();
    testSym.setMessageText("");
    testSym.setMessage(TABLE_MESSAGE);

    ticketService.sendTicketMessageToAgentStreamId(mock(Ticket.class), testSym);

    ArgumentCaptor<SymMessage> captor = ArgumentCaptor.forClass(SymMessage.class);
    verify(messagesClient).sendMessage(any(SymStream.class), captor.capture());
    SymMessage sentMessage = captor.getValue();

    assertTrue(sentMessage.getEntityData().contains("\"question\":\"" + user.getDisplayName() + " sent a table!\""));
  }

  @Test
  public void testSendMessageWhenRoomCreationFails() throws MessagesException {
    SymMessage symMessage = new SymMessage();
    symMessage.setMessageText(TEST_CREATE_TICKET_MESSAGE);

    ticketService.sendMessageWhenRoomCreationFails(symMessage);

    verify(messagesClient, times(1)).sendMessage(any(SymStream.class), any(SymMessage.class));
  }

  private Room mockRoom() {
    Room room = new Room();
    room.setId(TEST_SERVICE_STREAM_ID);

    SymRoomAttributes symRoomAttributes = new SymRoomAttributes();
    symRoomAttributes.setViewHistory(Boolean.TRUE);

    SymRoomDetail symRoomDetail = new SymRoomDetail();
    symRoomDetail.setRoomAttributes(symRoomAttributes);

    room.setRoomDetail(symRoomDetail);

    return room;
  }
}