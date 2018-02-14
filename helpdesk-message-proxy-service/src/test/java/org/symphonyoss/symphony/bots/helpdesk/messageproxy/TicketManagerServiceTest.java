package org.symphonyoss.symphony.bots.helpdesk.messageproxy;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
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
import org.symphonyoss.client.model.Room;
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
import org.symphonyoss.symphony.clients.model.SymRoomAttributes;
import org.symphonyoss.symphony.clients.model.SymRoomDetail;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.invoker.ApiClient;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.invoker.Configuration;
import org.symphonyoss.symphony.pod.model.UserV2;

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

  @Mock
  private ApiClient apiClient;

  private TicketManagerService ticketManagerService;

  private static final String STREAM_ID = "STREAM_ID";

  private static final String GROUP_ID = "GROUP_ID";

  private static final Long AGENT_ID = 012345L;

  private static final Long CLIENT_ID = 67890L;

  private static final String CLIENT_STREAM_ID = "CLIENT_STREAM_ID";

  private static final String AGENT_NAME = "AGENT_NAME";

  private static final String TICKET_ID = "TICKET_ID";

  private static final Long QUESTION_TIMESTAMP = 111111L;

  private static final String SERVICE_STREAM_ID = "SERVICE_STREAM_ID";

  private static final String NEW_STREAM_ID = "NEW_STREAM_ID";

  private static final String NEW_SERVICE_STREAM_ID = "NEW_SERVICE_STRAM_ID";

  private static final String TOKEN = "TOKEN";

  private static final Long SYMUSER = 123456L;

  private static final String COMPANY = "COMPANY";

  private static final String FIRST_NAME = "FISRT_NAME";

  @Before
  public void initMocks() {
    Configuration.setDefaultApiClient(apiClient);
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
    Room serviceStream = mockRoom();

    doReturn(null).when(ticketService).getTicketByServiceStreamId(NEW_STREAM_ID);

    doReturn(membershipClient).when(membershipService).updateMembership(symMessage,
        MembershipClient.MembershipType.CLIENT);

    doReturn(ticket).when(ticketService).getUnresolvedTicket(NEW_STREAM_ID);

    ticketManagerService.messageReceived(symMessage);

    verify(membershipService, times(1)).updateMembership(symMessage,
        MembershipClient.MembershipType.CLIENT);

    verify(ticketService, never()).createTicket(anyString(), eq(symMessage), eq(serviceStream));

    verify(messageProxyService, times(1)).onMessage(membershipClient, ticket, symMessage);
  }

  @Test
  public void updateMembershipClientAndCreateATicket() throws ApiException {
    UserV2 userV2 = getUserV2();

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
    Membership membershipClient = getMembershipClient();
    Room serviceStream = mockRoom();

    doReturn(membershipClient).when(membershipService).updateMembership(symMessage,
        MembershipClient.MembershipType.CLIENT);

    doReturn(mockRoom()).when(roomService).createServiceStream(anyString(), eq(GROUP_ID), eq(COMPANY));

    doReturn(ticket).when(ticketService)
        .createTicket(anyString(), eq(symMessage), eq(serviceStream));

    doReturn(symAuth).when(symphonyClient).getSymAuth();

    doReturn(null).doReturn(userV2)
        .when(apiClient)
        .invokeAPI(any(), eq("GET"), any(), any(), any(), any(), any(), any(), any(), any());

    ticketManagerService.messageReceived(symMessage);

    verify(membershipService, times(1)).updateMembership(symMessage,
        MembershipClient.MembershipType.CLIENT);

    verify(ticketService, times(1)).createTicket(anyString(), eq(symMessage), eq(serviceStream));

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

  private UserInfo getAgentInfo() {
    UserInfo agent = new UserInfo();
    agent.setUserId(AGENT_ID);
    agent.setDisplayName(AGENT_NAME);

    return agent;
  }
  
  private UserV2 getUserV2() {
    UserV2 userV2 = new UserV2();
    userV2.setFirstName(FIRST_NAME);
    userV2.setCompany(COMPANY);

    return userV2;
  }
  private Room mockRoom() {
    Room room = new Room();
    room.setId(NEW_SERVICE_STREAM_ID);

    SymRoomAttributes symRoomAttributes = new SymRoomAttributes();
    symRoomAttributes.setViewHistory(Boolean.TRUE);

    SymRoomDetail symRoomDetail = new SymRoomDetail();
    symRoomDetail.setRoomAttributes(symRoomAttributes);

    room.setRoomDetail(symRoomDetail);

    return room;
  }
}