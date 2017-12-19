package org.symphonyoss.symphony.bots.helpdesk.messageproxy;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.clients.model.SymMessage;
import static org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient
    .MembershipType.AGENT;
import static org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient
    .MembershipType.CLIENT;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;

/**
 * Created by alexandre-silva-daitan on 19/12/17
 */

@RunWith(MockitoJUnitRunner.class)
public class TicketManagerServiceTest {

  @Mock
  private MembershipService membershipService;

  @Mock
  private TicketService ticketService;

  @Mock
  private RoomService roomService;

  @Mock
  private MessageProxyService messageProxyService;

  private static final String MOCK_TEXT = "Mock text";

  private static final String STREAM_ID = "STREAM_ID";

  private static final String GROUP_ID = "GROUP_ID";

  @Test
  public void updateMembershipAgent(){
    SymMessage message = new SymMessage();
    message.setMessageText(MOCK_TEXT);
    message.setStreamId(STREAM_ID);

    Ticket mockTicket = mock(Ticket.class);

    TicketManagerServiceTest serviceTest = new TicketManagerServiceTest(STREAM_ID, GROUP_ID,
        membershipService, ticketService, roomService, messageProxyService);

    when(ticketService.getTicketByServiceStreamId(message.getStreamId()).thenReturn(mockTicket);
    Ticket ticket = ticketService.getTicketByServiceStreamId(message.getStreamId();

    when((message.getStreamId().equals(agentStreamId) || ticket != null)).thenReturn(true));
    if((message.getStreamId().equals(agentStreamId) || ticket != null)) {
      membership = membershipService.updateMembership(message, AGENT);
    }

    verify(serviceTest, times(1)).updateMembershipAgent(message, AGENT);
  }

  @Test
  public void updateMembershipClient(){
    SymMessage message = new SymMessage();
    message.setMessageText(MOCK_TEXT);
    message.setStreamId(STREAM_ID);

    TicketManagerServiceTest serviceTest = new TicketManagerServiceTest(STREAM_ID, GROUP_ID,
        membershipService, ticketService, roomService, messageProxyService);

    when(ticketService.getTicketByServiceStreamId(message.getStreamId()).thenReturn(null);
    Ticket ticket = ticketService.getTicketByServiceStreamId(message.getStreamId();

    when((message.getStreamId().equals(agentStreamId) || ticket != null)).thenReturn(false));
    if((message.getStreamId().equals(agentStreamId) || ticket != null)) {
      membership = membershipService.updateMembership(message, CLIENT);
    }

    verify(serviceTest, times(1)).updateMembershipAgent(message, CLIENT);
  }

}