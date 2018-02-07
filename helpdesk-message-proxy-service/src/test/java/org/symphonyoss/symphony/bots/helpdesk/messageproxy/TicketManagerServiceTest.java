package org.symphonyoss.symphony.bots.helpdesk.messageproxy;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.authenticator.model.Token;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.service.MembershipService;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.service.RoomService;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.service.TicketService;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.model.UserInfo;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.api.UsersApi;
import org.symphonyoss.symphony.pod.invoker.ApiClient;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.invoker.Configuration;

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

  @Mock
  private  SymphonyClient symphonyClient;

  private TicketManagerService ticketManagerService;

  private static final String STREAM_ID = "STREAM_ID";

  private static final String GROUP_ID = "GROUP_ID";

  private static final Long AGENT_ID = 012345L;

  private static final Long CLIENT_ID = 67890L;

  private static final String CLIENT_STREAM_ID = "CLIENT_STREAM_ID";

  private static final String AGENT_NAME = "AGENT_NAME";

  private static final String TICKET_ID = "TICKET_ID";

  private static final String CLIENT_NAME = "CLIENT_NAME";

  private static final Long QUESTION_TIMESTAMP = 111111L;

  private static final String SERVICE_STREAM_ID = "SERVICE_STREAM_ID";

  private static final String NEW_STREAM_ID = "NEW_STREAM_ID";

  private static final String NEW_SERVICE_STREAM_ID = "NEW_SERVICE_STRAM_ID";

  private static final int TICKET_ID_LENGTH = 10;

  private static final String TOKEN = "TOKEN";

  private static final Long SYMUSER = 123456L;

  @Before
  public void initMocks() {
    ticketManagerService =
        new TicketManagerService(STREAM_ID, GROUP_ID, membershipService, ticketService, roomService,
            messageProxyService, symphonyClient);

  }

  @Test
  public void updateMembershipAgent() {
    SymMessage symMessage = new SymMessage();
    symMessage.setStreamId(STREAM_ID);
    Ticket ticket = getTicket();
    Membership membershipAgent = getMembershipAgent();

    doReturn(ticket).when(ticketService).getTicketByServiceStreamId(STREAM_ID);

    doReturn(membershipAgent).when(membershipService).updateMembership(symMessage,
        MembershipClient.MembershipType.AGENT);

    ticketManagerService.messageReceived(symMessage);

    verify(membershipService, times(1)).updateMembership(symMessage,
        MembershipClient.MembershipType.AGENT);

    verify(messageProxyService, times(1)).onMessage(membershipAgent, ticket, symMessage);
  }

  @Test
  public void updateMembershipClient() {
    SymMessage symMessage = new SymMessage();
    symMessage.setStreamId(NEW_STREAM_ID);
    Ticket ticket = getTicket();
    Membership membershipClient = getMembershipClient();

    doReturn(null).when(ticketService).getTicketByServiceStreamId(NEW_STREAM_ID);

    doReturn(membershipClient).when(membershipService).updateMembership(symMessage,
        MembershipClient.MembershipType.CLIENT);

    doReturn(ticket).when(ticketService).getUnresolvedTicket(NEW_STREAM_ID);

    ticketManagerService.messageReceived(symMessage);

    verify(membershipService, times(1)).updateMembership(symMessage,
        MembershipClient.MembershipType.CLIENT);

    verify(ticketService, never()).createTicket(anyString(), eq(symMessage), eq(NEW_STREAM_ID));

    verify(messageProxyService, times(1)).onMessage(membershipClient, ticket, symMessage);
  }

  @Ignore @Test
  //this test needs a real token to pass
  public void updateMembershipClientAndCreateATicket() throws ApiException {
    ApiClient apiClient = Configuration.getDefaultApiClient();
    UsersApi usersApi = new UsersApi(apiClient);

    SymUser symUser = new SymUser();
    symUser.setId(SYMUSER);

    SymMessage symMessage = new SymMessage();
    symMessage.setStreamId(NEW_STREAM_ID);
    symMessage.setSymUser(symUser);

    Token token = new Token();
    token.setToken(TOKEN);

    SymAuth symAuth = new SymAuth();
    symAuth.setSessionToken(token);


    Ticket ticket = getTicket();
    String podName = "pod182";
    Membership membershipClient = getMembershipClient();

    doReturn(null).when(ticketService).getTicketByServiceStreamId(NEW_STREAM_ID);

    doReturn(membershipClient).when(membershipService).updateMembership(symMessage,
        MembershipClient.MembershipType.CLIENT);

    doReturn(null).when(ticketService).getUnresolvedTicket(NEW_STREAM_ID);

    doReturn(NEW_SERVICE_STREAM_ID).when(roomService).newServiceStream(anyString(), eq(GROUP_ID), eq(podName));

    doReturn(ticket).when(ticketService)
        .createTicket(anyString(), eq(symMessage), eq(NEW_SERVICE_STREAM_ID));

    doReturn(symAuth).when(symphonyClient).getSymAuth();

    ticketManagerService.messageReceived(symMessage);

    verify(membershipService, times(1)).updateMembership(symMessage,
        MembershipClient.MembershipType.CLIENT);

    verify(ticketService, times(1)).createTicket(anyString(), eq(symMessage), eq(
        NEW_SERVICE_STREAM_ID));

    verify(messageProxyService, times(1)).onMessage(membershipClient, ticket, symMessage);
  }

  private Membership getMembershipAgent() {
    Membership membership = new Membership();
    membership.setType(MembershipClient.MembershipType.AGENT.getType());
    membership.setGroupId(GROUP_ID);
    membership.setId(AGENT_ID);

    return membership;
  }

  private Membership getMembershipClient() {
    Membership membership = new Membership();
    membership.setType(MembershipClient.MembershipType.CLIENT.getType());
    membership.setGroupId(GROUP_ID);
    membership.setId(CLIENT_ID);

    return membership;
  }

  private Ticket getTicket() {
    Ticket ticket = new Ticket();
    ticket.setGroupId(GROUP_ID);
    ticket.setAgent(getAgentInfo());
    ticket.setClient(getAgentInfo());
    ticket.setId(TICKET_ID);
    ticket.setClientStreamId(CLIENT_STREAM_ID);
    ticket.setQuestionTimestamp(QUESTION_TIMESTAMP);
    ticket.setServiceStreamId(SERVICE_STREAM_ID);
    ticket.setState(TicketClient.TicketStateType.UNSERVICED.getState());

    return ticket;
  }

  private UserInfo getClientInfo() {
    UserInfo client = new UserInfo();
    client.setUserId(CLIENT_ID);
    client.setDisplayName(CLIENT_NAME);

    return client;
  }

  private UserInfo getAgentInfo() {
    UserInfo agent = new UserInfo();
    agent.setUserId(AGENT_ID);
    agent.setDisplayName(AGENT_NAME);

    return agent;
  }


}