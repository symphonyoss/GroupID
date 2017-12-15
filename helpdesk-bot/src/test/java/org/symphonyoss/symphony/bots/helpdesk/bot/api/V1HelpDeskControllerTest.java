package org.symphonyoss.symphony.bots.helpdesk.bot.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;

import javax.ws.rs.BadRequestException;


/**
 * Created by crepache on 15/12/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class V1HelpDeskControllerTest {

  private static final String MOCK_TICKET_ID = "LOEXALHQFJ";

  private static final String MOCK_GROUP_ID = "HelpDesk";

  private static final String MOCK_CLIENT_STREAM_ID = "m3TYBJ-g-k9VDZLn5YaOuH___qA1PJWtdA";

  private static final String MOCK_SERVICE_STREAM_ID = "RU0dsa0XfcE5SNoJKsXSKX___p-qTQ3XdA";

  private static final String EXPECTED_MESSAGE_TO_TICKET_WAS_CLAIMED = "Ticket was claimed.";

  @Mock
  private TicketClient ticketClient;

  @InjectMocks
  private V1HelpDeskController v1HelpDeskController;

  @Test()
  public void testAcceptTicketAlreadyAccepted() {
    Ticket ticket = mockTicketUnresolved();
    doReturn(ticket).when(ticketClient).getTicket(ticket.getId());

    try {
      v1HelpDeskController.acceptTicket(MOCK_TICKET_ID, 1l);
      fail();
    } catch (BadRequestException e) {
      assertEquals(EXPECTED_MESSAGE_TO_TICKET_WAS_CLAIMED, e.getMessage());
    }
  }

  private Ticket mockTicketUnresolved() {
    Ticket ticket = new Ticket();
    ticket.setId(MOCK_TICKET_ID);
    ticket.setState(TicketClient.TicketStateType.UNRESOLVED.getState());
    ticket.setGroupId(MOCK_GROUP_ID);
    ticket.setClientStreamId(MOCK_CLIENT_STREAM_ID);
    ticket.setQuestionTimestamp(1513266273011l);
    ticket.setServiceStreamId(MOCK_SERVICE_STREAM_ID);

    return ticket;
  }

}
