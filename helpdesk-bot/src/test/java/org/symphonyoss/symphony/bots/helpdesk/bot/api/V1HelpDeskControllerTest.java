package org.symphonyoss.symphony.bots.helpdesk.bot.api;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.bots.helpdesk.bot.ticket.AccepTicketService;
import org.symphonyoss.symphony.bots.helpdesk.bot.ticket.JoinConversationService;


/**
 * Created by crepache on 15/12/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class V1HelpDeskControllerTest {

  private static final String MOCK_TICKET_ID = "LOEXALHQFJ";

  private static final Long MOCK_USER_ID = 123456L;

  @Mock
  private AccepTicketService accepTicketService;

  @Mock
  private JoinConversationService joinConversationService;

  @InjectMocks
  private V1HelpDeskController v1HelpDeskController;

  @Test
  public void testAcceptTicket() {
    v1HelpDeskController.acceptTicket(MOCK_TICKET_ID, MOCK_USER_ID);
    verify(accepTicketService, times(1)).execute(MOCK_TICKET_ID, MOCK_USER_ID);
  }

  @Test
  public void testJoinConversation() {
    v1HelpDeskController.joinConversation(MOCK_TICKET_ID, MOCK_USER_ID);
    verify(joinConversationService, times(1)).execute(MOCK_TICKET_ID, MOCK_USER_ID);
  }
}
