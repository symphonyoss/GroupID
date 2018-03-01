package org.symphonyoss.symphony.bots.helpdesk.bot.listener;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.events.SymUserJoinedRoom;
import org.symphonyoss.client.events.SymUserLeftRoom;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.services.MessageService;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.service.TicketService;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.model.UserInfo;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.clients.MessagesClient;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.Arrays;

/**
 * Created by rsanchez on 14/12/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class HelpDeskRoomEventListenerTest {

  private static final Long MOCK_USER = 123456L;

  private static final String MOCK_STREAM = "Yc-my4qYo4-ZoQyR6C16o3___q_zYfhtWB";

  private static final String MOCK_STREAM2 = "anyStream";

  private static final Long MOCK_BOT_USER = 654321L;

  private static final Long MOCK_ANY_USER = 032121L;

  private static final String MOCK_BOT_STREAM = "Zs-nx3pQh3-XyKlT5B15m3___p_zHfetdA";

  private static final String WELCOME_MESSAGE = "Thanks for contacting the helpdesk bot";
  public static final String MESSAGE_ID = "";

  private String runawayAgentMessage = "Message";

  @Mock
  private HelpDeskBotConfig config;

  @Mock
  private SymphonyClient symphonyClient;

  @Mock
  private MessagesClient messagesClient;

  @Mock
  private TicketClient ticketClient;

  @Mock
  private TicketService ticketService;

  @Mock
  private MessageService messageService;

  private HelpDeskRoomEventListener listener;

  @Before
  public void init() {
    doReturn(WELCOME_MESSAGE).when(config).getWelcomeMessage();
    doReturn(MOCK_BOT_STREAM).when(config).getAgentStreamId();

    doReturn(messageService).when(symphonyClient).getMessageService();

    SymUser symUser = new SymUser();
    symUser.setId(MOCK_BOT_USER);

    doReturn(symUser).when(symphonyClient).getLocalUser();

    this.listener =
        new HelpDeskRoomEventListener(runawayAgentMessage, symphonyClient, ticketClient, config,
            ticketService);
  }

  @Test
  public void testJoinedRoomIsNotABotUser() throws MessagesException {
    SymUserJoinedRoom joinedRoom = mockJoinEvent(MOCK_USER, MOCK_STREAM);

    listener.onSymUserJoinedRoom(joinedRoom);

    verify(messageService, never()).sendMessage(any(SymStream.class), any(SymMessage.class));
  }

  @Test
  public void testJoinedRoomIsAgentStream() throws MessagesException {
    SymUserJoinedRoom joinedRoom = mockJoinEvent(MOCK_BOT_USER, MOCK_BOT_STREAM);

    listener.onSymUserJoinedRoom(joinedRoom);

    verify(messageService, never()).sendMessage(any(SymStream.class), any(SymMessage.class));
  }

  @Test
  public void testJoinedRoom() throws MessagesException {
    SymUserJoinedRoom joinedRoom = mockJoinEvent(MOCK_BOT_USER, MOCK_STREAM);

    listener.onSymUserJoinedRoom(joinedRoom);

    verify(messageService, times(1)).sendMessage(any(SymStream.class), any(SymMessage.class));
  }

  @Test
  public void testUserLeftWhenTicketIsResolved() throws MessagesException {
    SymUserLeftRoom symUserLeftRoom = mockLeaveEvent(MOCK_ANY_USER, MOCK_STREAM);

    Ticket mockTicket = new Ticket();
    mockTicket.setAgent(new UserInfo());
    mockTicket.setState(TicketClient.TicketStateType.RESOLVED.toString());

    doReturn(mockTicket).when(ticketClient).getTicketByServiceStreamId(eq(MOCK_STREAM));

    listener.onSymUserLeftRoom(symUserLeftRoom);

    verify(ticketClient, times(1)).getTicketByServiceStreamId(MOCK_STREAM);
    verify(ticketClient, never()).updateTicket(eq(mockTicket));
  }

  @Test
  public void testUserLeftRoomOnlyTheBotRemains() throws MessagesException {
    SymUserLeftRoom symUserLeftRoom = mockLeaveEvent(MOCK_ANY_USER, MOCK_STREAM);

    UserInfo clientUserInfo = new UserInfo();
    clientUserInfo.setUserId(MOCK_ANY_USER);

    Ticket mockTicket = new Ticket();
    mockTicket.setAgent(new UserInfo());
    mockTicket.setState(TicketClient.TicketStateType.UNRESOLVED.toString());
    mockTicket.setQuestionTimestamp(1L);
    mockTicket.setClient(clientUserInfo);

    SymMessage symMessage = new SymMessage();
    symMessage.setId(MESSAGE_ID);

    doReturn(MOCK_STREAM2).when(config).getAgentStreamId();
    doReturn(mockTicket).when(ticketClient).getTicketByServiceStreamId(eq(MOCK_STREAM));
    doReturn(messagesClient).when(symphonyClient).getMessagesClient();
    doReturn(Arrays.asList(symMessage)).when(messagesClient)
        .getMessagesFromStream(any(SymStream.class), anyLong(), anyInt(), anyInt());

    listener.onSymUserLeftRoom(symUserLeftRoom);

    assertEquals(null, mockTicket.getAgent());
    assertEquals(TicketClient.TicketStateType.UNSERVICED.toString(), mockTicket.getState());

    verify(ticketClient, times(1)).getTicketByServiceStreamId(MOCK_STREAM);
    verify(ticketClient, times(1)).updateTicket(eq(mockTicket));
    verify(ticketService, times(1)).sendTicketMessageToAgentStreamId(eq(mockTicket),
        any(SymMessage.class));
    verify(ticketService, times(1)).sendClientMessageToServiceStreamId(anyString(),
        any(SymMessage.class));
  }

  private SymUserJoinedRoom mockJoinEvent(Long userId, String stream) {
    SymUser symUser = new SymUser();
    symUser.setId(userId);
    symUser.setDisplayName(StringUtils.EMPTY);

    SymStream symStream = new SymStream();
    symStream.setRoomName(StringUtils.EMPTY);
    symStream.setStreamId(stream);

    SymUserJoinedRoom symUserJoinedRoom = new SymUserJoinedRoom();
    symUserJoinedRoom.setAffectedUser(symUser);
    symUserJoinedRoom.setStream(symStream);

    return symUserJoinedRoom;
  }

  private SymUserLeftRoom mockLeaveEvent(Long userId, String stream) {
    SymUser symUser = new SymUser();
    symUser.setId(userId);
    symUser.setDisplayName(StringUtils.EMPTY);

    SymStream symStream = new SymStream();
    symStream.setRoomName(StringUtils.EMPTY);
    symStream.setStreamId(stream);
    symStream.setMembers(Arrays.asList(symUser));

    SymUserLeftRoom symUserLeftRoom = new SymUserLeftRoom();
    symUserLeftRoom.setAffectedUser(symUser);
    symUserLeftRoom.setStream(symStream);

    return symUserLeftRoom;
  }

}
