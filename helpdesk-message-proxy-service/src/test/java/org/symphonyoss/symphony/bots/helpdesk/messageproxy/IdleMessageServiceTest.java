package org.symphonyoss.symphony.bots.helpdesk.messageproxy;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.HelpDeskBotInfo;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.HelpDeskServiceInfo;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.IdleTicketConfig;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.service.TicketService;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;

@RunWith(MockitoJUnitRunner.class)
public class IdleMessageServiceTest {

  private static final String MOCK_TICKET_ID = "MOCK_TICKET_ID";
  private static final String MESSAGE = "MESSAGE";
  private static final String BOT_HOST = "BOT_HOST";
  private static final String SERVICE_HOST = "SERVICE_HOST";

  private final String agentStreamId = " um63nmfGF24qO2MyBIFvbn___p9nIuKHdA";

  @Mock
  private TicketService ticketService;

  @Mock
  private HelpDeskBotInfo helpDeskBotInfo;

  @Mock
  private HelpDeskServiceInfo helpDeskServiceInfo;

  @Mock
  private IdleTicketConfig idleTicketConfig;


  private IdleMessageService idleMessageService;

  @Before
  public void setUp() throws Exception {
    idleMessageService = new IdleMessageService(ticketService, helpDeskBotInfo, helpDeskServiceInfo, agentStreamId, idleTicketConfig);
  }

  @Test
  public void sendIdleMessage() {
    Ticket ticket = new Ticket();
    ticket.setId(MOCK_TICKET_ID);
    ticket.setState(TicketClient.TicketStateType.UNSERVICED.getState());

    doReturn(MESSAGE).when(idleTicketConfig).getMessage();
    doReturn(BOT_HOST).when(helpDeskBotInfo).getUrl();
    doReturn(SERVICE_HOST).when(helpDeskServiceInfo).getUrl();

    idleMessageService.sendIdleMessage(ticket);

    verify(ticketService, times(1)).sendIdleMessageToAgentStreamId(any());

  }
}