package org.symphonyoss.symphony.bots.helpdesk.bot.ticket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.symphony.bots.ai.HelpDeskAi;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.TicketResponse;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.User;
import org.symphonyoss.symphony.bots.helpdesk.bot.util.ValidateMembershipService;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.validation.SymphonyValidationUtil;
import org.symphonyoss.symphony.clients.RoomMembershipClient;
import org.symphonyoss.symphony.clients.model.SymUser;

import javax.ws.rs.BadRequestException;

/**
 * Created by rsanchez on 19/12/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class TicketServiceTest {

  private static final String MOCK_TICKET_ID = "ABCDEFG";

  private static final Long MOCK_USER_ID = 123456L;

  private static final String MOCK_USER_DISPLAY_NAME = "mock user";

  private static final String MOCK_AGENT_STREAM_ID = "Zs-nx3pQh3-XyKlT5B15m3___p_zHfetdA";

  private static final String MOCK_SERVICE_STREAM_ID = "MWuIEaYBur6EisRqS4jXgX___p-46v8GdA";

  private static final String MOCK_CLIENT_STREAM_ID = "D65k1bCxsLXX-gvTis8pRX___p_zH5nJdA";

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
      ticketService.execute(MOCK_TICKET_ID, MOCK_USER_ID);
      fail();
    } catch (BadRequestException e) {
      assertEquals("Ticket not found.", e.getMessage());
    }
  }

  @Test
  public void testTicketFound() {
    String state = TicketClient.TicketStateType.UNRESOLVED.getState();

    Ticket ticket = new Ticket();
    ticket.setId(MOCK_TICKET_ID);
    ticket.setServiceStreamId(MOCK_SERVICE_STREAM_ID);
    ticket.setClientStreamId(MOCK_CLIENT_STREAM_ID);
    ticket.setState(state);

    SymUser agentUser = new SymUser();
    agentUser.setId(MOCK_USER_ID);
    agentUser.setDisplayName(MOCK_USER_DISPLAY_NAME);

    doReturn(ticket).when(ticketClient).getTicket(MOCK_TICKET_ID);
    doReturn(agentUser).when(symphonyValidationUtil).validateUserId(MOCK_USER_ID);

    TicketResponse response = ticketService.execute(MOCK_TICKET_ID, MOCK_USER_ID);

    verify(symphonyValidationUtil, times(1)).validateStream(MOCK_SERVICE_STREAM_ID);
    verify(symphonyValidationUtil, times(1)).validateStream(MOCK_CLIENT_STREAM_ID);

    assertNotNull(response);
    assertEquals("Success", response.getMessage());
    assertEquals(state, response.getState());
    assertEquals(MOCK_TICKET_ID, response.getTicketId());

    User user = response.getUser();
    assertNotNull(user);
    assertEquals(MOCK_USER_DISPLAY_NAME, user.getDisplayName());
    assertEquals(MOCK_USER_ID, user.getUserId());
  }

  @Test
  public void testAddAgentToStream() throws SymException {
    Ticket ticket = new Ticket();
    ticket.setServiceStreamId(MOCK_SERVICE_STREAM_ID);

    ticketService.addAgentToServiceStream(ticket, MOCK_USER_ID);

    verify(roomMembershipClient, times(1)).addMemberToRoom(MOCK_SERVICE_STREAM_ID, MOCK_USER_ID);
  }
}
