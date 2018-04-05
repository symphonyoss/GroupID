package org.symphonyoss.symphony.bots.helpdesk.bot.ticket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
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
import org.symphonyoss.client.services.MessageService;
import org.symphonyoss.symphony.bots.ai.helpdesk.HelpDeskAi;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.TicketResponse;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.User;
import org.symphonyoss.symphony.bots.helpdesk.bot.util.ValidateMembershipService;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.HelpDeskBotInfo;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
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
import javax.ws.rs.InternalServerErrorException;

/**
 * Created by rsanchez on 19/12/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class AcceptTicketServiceTest {

  private static final String MOCK_TICKET_ID = "ABCDEFG";

  private static final Long MOCK_USER_ID = 123456L;

  private static final String MOCK_USER_DISPLAY_NAME = "mock user";

  private static final String MOCK_SERVICE_STREAM_ID = "MWuIEaYBur6EisRqS4jXgX___p-46v8GdA";

  private static final String MOCK_CLIENT_STREAM_ID = "D65k1bCxsLXX-gvTis8pRX___p_zH5nJdA";

  private static final String STREAM_NAME = "Test Room";

  private static final String MOCK_MESSAGE_ID_1 = "2301";

  private static final String MOCK_MESSAGE_ID_2 = "2401";

  private static final String MOCK_MESSAGE_1 = "1 - Messages";

  private static final String MOCK_MESSAGE_2 = "2 - Messages";

  private static final long QUESTION_TIMESTAMP = 230111987l;

  private static final Long MOCK_CLIENT_ID = 123456L;

  private static final String MOCK_CLIENT_DISPLAY_NAME = "mock client";

  @Mock
  private SymphonyValidationUtil symphonyValidationUtil;

  @Mock
  private MembershipClient membershipClient;

  @Mock
  private SymphonyClient symphonyClient;

  @Mock
  private HelpDeskBotConfig helpDeskBotConfig;

  @Mock
  private TicketClient ticketClient;

  @Mock
  private RoomMembershipClient roomMembershipClient;

  @Mock
  private MessageService messageService;

  @Mock
  private MessagesClient messagesClient;

  @Mock
  private HelpDeskAi helpDeskAi;

  @Mock
  private ValidateMembershipService validateMembershipService;

  @Mock
  private HelpDeskBotInfo helpDeskBotInfo;

  private AcceptTicketService acceptTicketService;

  @Before
  public void init() {
    doReturn(roomMembershipClient).when(symphonyClient).getRoomMembershipClient();
    doReturn(messagesClient).when(symphonyClient).getMessagesClient();

    this.acceptTicketService =
        new AcceptTicketService(symphonyValidationUtil, symphonyClient,
            helpDeskBotConfig, ticketClient, helpDeskAi, validateMembershipService);
  }

  @Test
  public void testTicketNotClaimed() {
    Ticket ticket = new Ticket();
    ticket.setId(MOCK_TICKET_ID);

    SymUser agent = new SymUser();
    agent.setId(MOCK_USER_ID);

    try {
      acceptTicketService.execute(ticket, agent);
      fail();
    } catch (BadRequestException e) {
      assertEquals("Ticket was claimed.", e.getMessage());
    }
  }

  @Test(expected = InternalServerErrorException.class)
  public void testInternalServerErrorToAddedAgentToStream() throws SymException {
    doThrow(SymException.class).when(validateMembershipService).updateMembership(MOCK_USER_ID);

    Ticket ticket = new Ticket();
    ticket.setId(MOCK_TICKET_ID);
    ticket.setState(TicketClient.TicketStateType.UNSERVICED.getState());

    SymUser agent = new SymUser();
    agent.setId(MOCK_USER_ID);

    acceptTicketService.execute(ticket, agent);
  }

  @Test
  public void testSuccess() throws SymException {
    Ticket ticket = new Ticket();
    ticket.setId(MOCK_TICKET_ID);
    ticket.setState(TicketClient.TicketStateType.UNSERVICED.getState());

    SymUser agent = new SymUser();
    agent.setId(MOCK_USER_ID);
    agent.setDisplayName(MOCK_USER_DISPLAY_NAME);

    TicketResponse response = acceptTicketService.execute(ticket, agent);

    assertNotNull(response);
    assertEquals("Ticket accepted.", response.getMessage());
    assertEquals(MOCK_TICKET_ID, response.getTicketId());

    User user = response.getUser();
    assertNotNull(user);
    assertEquals(MOCK_USER_DISPLAY_NAME, user.getDisplayName());
    assertEquals(MOCK_USER_ID, user.getUserId());

    verify(ticketClient, times(1)).updateTicket(ticket);
    verify(helpDeskAi, times(1)).sendMessage(any(), any());
    verify(messagesClient, times(1)).sendMessage(any(SymStream.class), any(SymMessage.class));
  }


  @Test
  public void testSendMessageWithShowHistoryFalse() throws MessagesException {
    Ticket ticket = mockTicket();

    SymStream serviceStream = new SymStream();
    serviceStream.setStreamId(ticket.getServiceStreamId());

    doReturn(messagesClient).when(symphonyClient).getMessagesClient();
    doReturn(mockMessages()).when(messagesClient)
        .getMessagesFromStream(any(SymStream.class), eq(ticket.getQuestionTimestamp()), eq(0),
            eq(100));

    acceptTicketService.sendTicketHistory(ticket);

    verify(helpDeskAi, times(2)).sendMessage(any(), any());
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

  private SymStream mockStream() {
    SymStream symStream = new SymStream();
    symStream.setStreamId(MOCK_SERVICE_STREAM_ID);
    symStream.setRoomName(STREAM_NAME);

    return symStream;
  }

  private SymUser mockUser() {
    SymUser symUser = new SymUser();
    symUser.setId(MOCK_CLIENT_ID);
    symUser.setDisplayName(MOCK_CLIENT_DISPLAY_NAME);

    return symUser;
  }
}
