package org.symphonyoss.symphony.bots.helpdesk.bot.bootstrap;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.symphonyoss.client.exceptions.InitException;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.client.services.MessageService;
import org.symphonyoss.symphony.bots.ai.HelpDeskAi;
import org.symphonyoss.symphony.bots.ai.conversation.IdleTimerManager;
import org.symphonyoss.symphony.bots.helpdesk.bot.HelpDeskBot;
import org.symphonyoss.symphony.bots.helpdesk.bot.authentication.HelpDeskAuthenticationException;
import org.symphonyoss.symphony.bots.helpdesk.bot.authentication.HelpDeskAuthenticationService;
import org.symphonyoss.symphony.bots.helpdesk.bot.client.HelpDeskHttpClient;
import org.symphonyoss.symphony.bots.helpdesk.bot.client.HelpDeskSymphonyClient;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.listener.AutoConnectionAcceptListener;
import org.symphonyoss.symphony.bots.helpdesk.bot.listener.HelpDeskRoomEventListener;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.ChatListener;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.IdleMessage;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.IdleTicketConfig;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;

/**
 * Created by rsanchez on 18/12/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class HelpDeskBootstrapTest {

  private static final String MOCK_EMAIL = "email@test.com";

  private static final String MOCK_AGENT_URL = "https://test.symphony.com/agent";

  private static final String MOCK_POD_URL = "https://test.symphony.com/pod";

  @Mock
  private ApplicationReadyEvent event;

  @Mock
  private ConfigurableApplicationContext applicationContext;

  @Mock
  private HelpDeskAuthenticationService authenticationService;

  @Mock
  private HelpDeskSymphonyClient symphonyClient;

  @Mock
  private HelpDeskBotConfig config;

  @Mock
  private ChatListener chatListener;

  @Mock
  private HelpDeskRoomEventListener roomEventListener;

  @Mock
  private AutoConnectionAcceptListener connectionListener;

  @Mock
  private HelpDeskBot helpDeskBot;

  @Mock
  private HelpDeskAi helpDeskAi;

  @Mock
  private MessageService messageService;

  @Mock
  private HelpDeskHttpClient httpClient;

  @Mock
  private IdleTimerManager idleTimerManager;

  @Mock
  private TicketClient ticketClient;

  @Mock
  private IdleTicketConfig idleTicketConfig;

  @Mock
  private IdleMessage idleMessage;

  @Before
  public void init() {
    doReturn(applicationContext).when(event).getApplicationContext();

    doReturn(authenticationService).when(applicationContext).getBean(HelpDeskAuthenticationService.class);
    doReturn(symphonyClient).when(applicationContext).getBean(HelpDeskSymphonyClient.class);
    doReturn(config).when(applicationContext).getBean(HelpDeskBotConfig.class);
    doReturn(roomEventListener).when(applicationContext).getBean(HelpDeskRoomEventListener.class);
    doReturn(connectionListener).when(applicationContext).getBean(AutoConnectionAcceptListener.class);
    doReturn(chatListener).when(applicationContext).getBean(ChatListener.class);
    doReturn(helpDeskBot).when(applicationContext).getBean(HelpDeskBot.class);
    doReturn(helpDeskAi).when(applicationContext).getBean(HelpDeskAi.class);
    doReturn(httpClient).when(applicationContext).getBean(HelpDeskHttpClient.class);

    doReturn(messageService).when(symphonyClient).getMessageService();
    doReturn(idleTimerManager).when(applicationContext).getBean(IdleTimerManager.class);
    doReturn(ticketClient).when(applicationContext).getBean(TicketClient.class);
    doReturn(idleTicketConfig).when(applicationContext).getBean(IdleTicketConfig.class);
    doReturn(idleMessage).when(applicationContext).getBean(IdleMessage.class);
  }

  @Test
  public void testRetries() throws InitException {
    SymAuth symAuth = new SymAuth();

    doThrow(Exception.class).doReturn(symAuth).when(authenticationService).authenticate();
    doThrow(HelpDeskAuthenticationException.class).doThrow(InitException.class)
        .doNothing()
        .when(symphonyClient)
        .init(symAuth, MOCK_EMAIL, MOCK_AGENT_URL, MOCK_POD_URL);
    doThrow(Exception.class).doReturn(new Membership()).when(helpDeskBot).registerDefaultAgent();
    doThrow(Exception.class).doNothing().when(helpDeskAi).init();

    HelpDeskBootstrap helpDeskBootstrap = new HelpDeskBootstrap();
    helpDeskBootstrap.onApplicationEvent(event);

    verify(helpDeskBot, times(1)).ready();
  }

}
