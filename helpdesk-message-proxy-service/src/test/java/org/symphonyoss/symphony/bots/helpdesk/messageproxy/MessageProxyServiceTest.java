package org.symphonyoss.symphony.bots.helpdesk.messageproxy;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.ai.helpdesk.HelpDeskAi;
import org.symphonyoss.symphony.bots.ai.helpdesk.conversation.IdleTimerManager;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.MakerCheckerService;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.IdleTicketConfig;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;

@RunWith(MockitoJUnitRunner.class)
public class MessageProxyServiceTest {

  private static final String STREAM_ID = "STREAM_ID";
  private static final String MESSAGE_ID = "MESSAGE_ID";
  private static final String GROUP_ID = "GROUP_ID";
  private static final Long AGENT_ID = 123L;
  private static final Long BOT_ID = 321L;
  private static final Long TEST_TIMESTAMP = 1L;
  private static final Long USER_ID = 2L;
  private static final String MOCK_TICKET_ID = "TICKET_ID";
  private static final String SESSION_KEY = "SESSION_KEY";

  @Mock
  private HelpDeskAi helpDeskAi;

  @Mock
  private MakerCheckerService agentMakerCheckerService;

  @Mock
  private MakerCheckerService clientMakerCheckerService;

  @Mock
  private IdleTicketConfig idleTicketConfig;

  @Mock
  private IdleTimerManager idleTimerManager;

  @Mock
  private IdleMessageService idleMessageService;

  @Mock
  private MessageProxyService messageProxyService;

  @Mock
  private SymphonyClient symphonyClient;

  @Before
  public void setUp() throws Exception {
    SymUser user = new SymUser();
    user.setId(USER_ID);

    doReturn(user).when(symphonyClient).getLocalUser();

    messageProxyService =
        new MessageProxyService(helpDeskAi, agentMakerCheckerService, clientMakerCheckerService,
            idleTicketConfig, idleTimerManager, idleMessageService, symphonyClient);
  }

  @Test
  public void onMessageCreateAgentProxy() {
    AiSessionKey aiSessionKey = new AiSessionKey(SESSION_KEY, USER_ID, STREAM_ID);
    doReturn(aiSessionKey).when(helpDeskAi).getSessionKey(USER_ID,STREAM_ID);

    messageProxyService.onMessage(getMembershipAgent(), getTicket(), getTestSymMessage());

    verify(idleTimerManager, times(1)).put(eq(MOCK_TICKET_ID), any());
  }

  @Test
  public void onMessageAddAgentToProxy() {
    onMessageCreateAgentProxy();

    messageProxyService.onMessage(getMembershipAgent(), getTicket(), getTestSymMessage());

    verify(idleTimerManager, times(1)).get(MOCK_TICKET_ID);
  }

  @Test
  public void onMessageCreateClientProxy() {
    Membership membership = getMembershipAgent();
    membership.setType(MembershipClient.MembershipType.CLIENT.getType());

    AiSessionKey aiSessionKey = new AiSessionKey(SESSION_KEY, USER_ID, STREAM_ID);
    doReturn(aiSessionKey).when(helpDeskAi).getSessionKey(USER_ID,STREAM_ID);

    messageProxyService.onMessage(membership, getTicket(), getTestSymMessage());

    verify(idleTimerManager, never()).get(MOCK_TICKET_ID);
    verify(idleTimerManager, times(1)).put(eq(MOCK_TICKET_ID), any());
  }

  @Test
  public void onMessageCreateClientProxyTicketAlreadyExists() {
    onMessageCreateClientProxy();

    Membership membership = getMembershipAgent();
    membership.setType(MembershipClient.MembershipType.CLIENT.getType());

    messageProxyService.onMessage(membership, getTicket(), getTestSymMessage());

    verify(idleTimerManager, never()).get(MOCK_TICKET_ID);
    verify(idleTimerManager, times(1)).put(eq(MOCK_TICKET_ID), any());
  }

  private Membership getMembershipAgent() {
    Membership membership = new Membership();
    membership.setType(MembershipClient.MembershipType.AGENT.getType());
    membership.setGroupId(GROUP_ID);
    membership.setId(AGENT_ID);

    return membership;
  }

  private SymMessage getTestSymMessage() {
    SymMessage symMessage = new SymMessage();
    symMessage.setFromUserId(USER_ID);
    symMessage.setStreamId(STREAM_ID);
    symMessage.setTimestamp(TEST_TIMESTAMP.toString());
    symMessage.setId(MESSAGE_ID);

    return symMessage;
  }

  private Ticket getTicket() {
    Ticket ticket = new Ticket();
    ticket.setId(MOCK_TICKET_ID);
    ticket.setState(TicketClient.TicketStateType.UNRESOLVED.getState());
    return ticket;
  }
}