package org.symphonyoss.symphony.bots.helpdesk.bot.ticket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.symphony.bots.ai.HelpDeskAi;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.TicketResponse;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.User;
import org.symphonyoss.symphony.bots.helpdesk.bot.util.ValidateMembershipService;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.model.UserInfo;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.validation.SymphonyValidationUtil;
import org.symphonyoss.symphony.clients.MessagesClient;
import org.symphonyoss.symphony.clients.RoomMembershipClient;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.BadRequestException;

/**
 * Created by rsanchez on 19/12/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class TicketServiceTest {

  private static final String MOCK_TICKET_ID = "ABCDEFG";

  private static final Long MOCK_CLIENT_ID = 123456L;

  private static final String MOCK_CLIENT_DISPLAY_NAME = "mock client";

  private static final Long MOCK_AGENT_ID = 654321L;

  private static final String MOCK_AGENT_DISPLAY_NAME = "mock agent";

  private static final String MOCK_AGENT_STREAM_ID = "Zs-nx3pQh3-XyKlT5B15m3___p_zHfetdA";

  private static final String MOCK_SERVICE_STREAM_ID = "MWuIEaYBur6EisRqS4jXgX___p-46v8GdA";

  private static final String MOCK_CLIENT_STREAM_ID = "D65k1bCxsLXX-gvTis8pRX___p_zH5nJdA";

  private static final String STREAM_NAME = "Test Room";

  private static final String MOCK_MESSAGE_ID_1 = "2301";

  private static final String MOCK_MESSAGE_ID_2 = "2401";

  private static final String MOCK_MESSAGE_1 = "1 - Messages";

  private static final String MOCK_MESSAGE_2 = "2 - Messages";

  private static final long QUESTION_TIMESTAMP = 230111987l;

  @Mock
  private SymphonyValidationUtil symphonyValidationUtil;

  @Mock
  private SymphonyClient symphonyClient;

  @Mock
  private HelpDeskBotConfig helpDeskBotConfig;

  @Mock
  private TicketClient ticketClient;

  @Mock
  private RoomMembershipClient roomMembershipClient;

  @Mock
  private ValidateMembershipService validateMembershipService;

  @Mock
  private HelpDeskAi helpDeskAi;

  @Mock
  private MessagesClient messagesClient;

  private TicketService ticketService;

  @Before
  public void init() {
    doReturn(roomMembershipClient).when(symphonyClient).getRoomMembershipClient();
    doReturn(MOCK_AGENT_STREAM_ID).when(helpDeskBotConfig).getAgentStreamId();

    this.ticketService =
        new MockTicketService(symphonyValidationUtil, symphonyClient, helpDeskBotConfig,
            ticketClient, validateMembershipService, helpDeskAi);
  }

  @Test
  public void testTicketNotFound() {
    try {
      ticketService.execute(MOCK_TICKET_ID, MOCK_CLIENT_ID);
      fail();
    } catch (BadRequestException e) {
      assertEquals("Ticket not found.", e.getMessage());
    }
  }

  @Test
  public void testTicketFound() {
    Ticket ticket = mockTicket();
    SymUser agentUser = mockAgent();

    doReturn(ticket).when(ticketClient).getTicket(MOCK_TICKET_ID);
    doReturn(agentUser).when(symphonyValidationUtil).validateUserId(MOCK_CLIENT_ID);

    TicketResponse response = ticketService.execute(MOCK_TICKET_ID, MOCK_CLIENT_ID);

    verify(symphonyValidationUtil, times(1)).validateStream(MOCK_SERVICE_STREAM_ID);
    verify(symphonyValidationUtil, times(1)).validateStream(MOCK_CLIENT_STREAM_ID);

    assertNotNull(response);
    assertEquals("Success", response.getMessage());
    assertEquals(TicketClient.TicketStateType.UNRESOLVED.getState(), response.getState());
    assertEquals(MOCK_TICKET_ID, response.getTicketId());

    User user = response.getUser();
    assertNotNull(user);
    assertEquals(MOCK_AGENT_DISPLAY_NAME, user.getDisplayName());
    assertEquals(MOCK_AGENT_ID, user.getUserId());
  }

  @Test
  public void testAddAgentToStream() throws SymException {
    Ticket ticket = new Ticket();
    ticket.setServiceStreamId(MOCK_SERVICE_STREAM_ID);

    ticketService.addAgentToServiceStream(ticket, MOCK_CLIENT_ID);

    verify(roomMembershipClient, times(1)).addMemberToRoom(MOCK_SERVICE_STREAM_ID, MOCK_CLIENT_ID);
  }

  @Test
  public void testSendMessageWithShowHistoryFalse() throws MessagesException {
    Ticket ticket = mockTicket();

    SymStream serviceStream = new SymStream();
    serviceStream.setStreamId(ticket.getServiceStreamId());

    doReturn(messagesClient).when(symphonyClient).getMessagesClient();
    doReturn(mockMessages()).when(messagesClient)
        .getMessagesFromStream(any(SymStream.class), eq(ticket.getQuestionTimestamp()), eq(0), eq(100));

    ticketService.sendMessageWithShowHistoryFalse(ticket, MOCK_CLIENT_ID);

    verify(helpDeskAi, times(2)).sendMessage(any(), any(), any());
  }

  private Ticket mockTicket() {
    Ticket ticket = new Ticket();

    ticket.setId(MOCK_TICKET_ID);
    ticket.setServiceStreamId(MOCK_SERVICE_STREAM_ID);
    ticket.setClientStreamId(MOCK_CLIENT_STREAM_ID);
    ticket.setState(TicketClient.TicketStateType.UNRESOLVED.getState());
    ticket.setShowHistory(Boolean.FALSE);
    ticket.setQuestionTimestamp(QUESTION_TIMESTAMP);
    ticket.setClient(mockClient());

    return ticket;
  }

  private UserInfo mockClient() {
    UserInfo userInfo = new UserInfo();
    userInfo.setUserId(MOCK_CLIENT_ID);
    userInfo.setDisplayName(MOCK_CLIENT_DISPLAY_NAME);

    return userInfo;
  }

  private SymUser mockAgent() {
    SymUser agentUser = new SymUser();
    agentUser.setId(MOCK_AGENT_ID);
    agentUser.setDisplayName(MOCK_AGENT_DISPLAY_NAME);

    return agentUser;
  }

  private List<SymMessage> mockMessages() {
    List<SymMessage> messages = new ArrayList<>();

    SymMessage symMessage = mockMessage(MOCK_MESSAGE_ID_1, MOCK_MESSAGE_1);
    messages.add(symMessage);

    symMessage = mockMessage(MOCK_MESSAGE_ID_2, MOCK_MESSAGE_2);
    messages.add(symMessage);

    return messages;
  }

  private SymMessage mockMessage(String id, String message) {
    SymMessage symMessage = new SymMessage();
    symMessage.setId(id);
    symMessage.setMessage(message);

    SymStream serviceStream = mockStream();
    symMessage.setStream(serviceStream);
    symMessage.setStreamId(serviceStream.getStreamId());
    symMessage.setTimestamp(id);

    symMessage.setSymUser(mockUser());

    return symMessage;
  }

  private SymUser mockUser() {
    SymUser symUser = new SymUser();
    symUser.setId(MOCK_CLIENT_ID);
    symUser.setDisplayName(MOCK_CLIENT_DISPLAY_NAME);

    return symUser;
  }

  private SymStream mockStream() {
    SymStream symStream = new SymStream();
    symStream.setStreamId(MOCK_SERVICE_STREAM_ID);
    symStream.setRoomName(STREAM_NAME);

    return symStream;
  }
}
