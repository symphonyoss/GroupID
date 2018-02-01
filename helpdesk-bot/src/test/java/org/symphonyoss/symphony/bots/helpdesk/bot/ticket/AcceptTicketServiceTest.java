package org.symphonyoss.symphony.bots.helpdesk.bot.ticket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
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
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.client.services.MessageService;
import org.symphonyoss.symphony.bots.ai.HelpDeskAi;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.TicketResponse;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.User;
import org.symphonyoss.symphony.bots.helpdesk.bot.util.ValidateMembershipService;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.validation.SymphonyValidationUtil;
import org.symphonyoss.symphony.clients.RoomMembershipClient;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;

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
  private HelpDeskAi helpDeskAi;

  @Mock
  private ValidateMembershipService validateMembershipService;

  private AcceptTicketService acceptTicketService;

  @Before
  public void init() {
    doReturn(roomMembershipClient).when(symphonyClient).getRoomMembershipClient();
    doReturn(messageService).when(symphonyClient).getMessageService();

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
    verify(helpDeskAi, times(1)).sendMessage(any(), any(), any());
    verify(messageService, times(1)).sendMessage(any(SymStream.class), any(SymMessage.class));
  }

}
