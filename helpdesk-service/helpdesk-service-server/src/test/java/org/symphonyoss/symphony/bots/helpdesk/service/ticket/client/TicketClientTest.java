package org.symphonyoss.symphony.bots.helpdesk.service.ticket.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.bots.helpdesk.service.HelpDeskApiException;
import org.symphonyoss.symphony.bots.helpdesk.service.client.ApiClient;
import org.symphonyoss.symphony.bots.helpdesk.service.client.ApiException;
import org.symphonyoss.symphony.bots.helpdesk.service.client.Configuration;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.model.TicketSearchResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.model.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexandre-silva-daitan on 16/3/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class TicketClientTest {

  private static final String GROUP_ID = "GROUP_ID";
  private static final String TICKET_ID = "TICKET_ID";
  private static final String CLIENT_STREAM_ID = "CLIENT_STREAM_ID";
  private static final Long QUESTION_TIMESTAMP = 123L;
  private static final String SERVICE_STREAM_ID = "SERVICE_STREAM_ID";
  private static final Long AGENT_ID = 001L;
  private static final String AGENT_NAME = "AGENT_NAME";
  private static final Long CLIENT_ID = 002L;
  private static final String CLIENT_NAME = "CLIENT_NAME";
  private static final String TICKET_SERVICE_URL = "TICKET_SERVICE_URL";
  private static final String CONVERSATION_ID = "CONVERSATION_ID";

  @Mock
  private ApiClient apiClient;

  private TicketClient ticketClient;

  @Before
  public void setUp() throws Exception {
    Configuration.setDefaultApiClient(apiClient);
    ticketClient = new TicketClient(GROUP_ID, TICKET_SERVICE_URL);
  }

  @Test
  public void getTicket() throws ApiException {
    doReturn(TICKET_ID).when(apiClient).escapeString(TICKET_ID);

    doReturn(getUnservicedTicket())
        .when(apiClient)
        .invokeAPI(any(), eq("GET"), any(), any(), any(), any(), any(), any(), any(), any());

    Ticket ticket = ticketClient.getTicket(TICKET_ID);

    assertNotNull(ticket);
    assertEquals(getUnservicedTicket(), ticket);
  }

  @Test(expected = HelpDeskApiException.class)
  public void getTicketWithException() {
    ticketClient.getTicket(null);
  }

  @Test
  public void createTicket() throws ApiException {

    doReturn(getUnservicedTicket())
        .when(apiClient)
        .invokeAPI(any(), eq("POST"), any(), any(), any(), any(), any(), any(), any(), any());


    Ticket ticket = ticketClient.createTicket(TICKET_ID, CLIENT_STREAM_ID, SERVICE_STREAM_ID, 123L,
        getInfo("client"), true, CONVERSATION_ID);

    assertNotNull(ticket);
    assertEquals(getUnservicedTicket(), ticket);
  }

  @Test
  public void getTicketByServiceStreamId() throws ApiException {
    TicketSearchResponse response = getTicketSearchResponse();

    doReturn(response)
        .when(apiClient)
        .invokeAPI(any(), eq("GET"), any(), any(), any(), any(), any(), any(), any(), any());

    Ticket ticket = ticketClient.getTicketByServiceStreamId(SERVICE_STREAM_ID);

    assertNotNull(ticket);
    assertEquals(getUnservicedTicket(), ticket);
  }

  @Test
  public void getUnresolvedTicketByClientStreamId() throws ApiException {

    TicketSearchResponse response = getTicketSearchResponse();

    doReturn(response)
        .when(apiClient)
        .invokeAPI(any(), eq("GET"), any(), any(), any(), any(), any(), any(), any(), any());

    Ticket ticket = ticketClient.getUnresolvedTicketByClientStreamId(CLIENT_STREAM_ID);

    assertNotNull(ticket);
    assertEquals(getUnservicedTicket(), ticket);
  }

  @Test
  public void getUnresolvedTickets() throws ApiException {
    TicketSearchResponse response = getTicketSearchResponse();

    doReturn(response)
        .when(apiClient)
        .invokeAPI(any(), eq("GET"), any(), any(), any(), any(), any(), any(), any(), any());

    List<Ticket> ticketList = ticketClient.getUnresolvedTickets();

    assertEquals(ticketList.isEmpty(), false);
    assertEquals(ticketList.size(), 1);


  }

  private TicketSearchResponse getTicketSearchResponse() {
    TicketSearchResponse response = new TicketSearchResponse();
    response.add(getUnservicedTicket());
    return response;
  }

  @Test
  public void getUnresolvedTicketsEmptyList() throws ApiException {

    doReturn(null)
        .when(apiClient)
        .invokeAPI(any(), eq("GET"), any(), any(), any(), any(), any(), any(), any(), any());

    List<Ticket> ticketList = ticketClient.getUnresolvedTickets();

    assertEquals(ticketList.isEmpty(), true);
    assertEquals(ticketList.size(), 0);


  }

  @Test
  public void updateTicket() {
  }

  private List<Ticket> getListTicket() {
    List<Ticket> ticketList = new ArrayList<Ticket>();
    ticketList.add(getUnservicedTicket());
    return ticketList;
  }

  private Ticket getUnservicedTicket() {
    Ticket ticket = new Ticket();
    ticket.setGroupId(GROUP_ID);
    ticket.setAgent(getInfo("agent"));
    ticket.setClient(getInfo("client"));
    ticket.setId(TICKET_ID);
    ticket.setClientStreamId(CLIENT_STREAM_ID);
    ticket.setQuestionTimestamp(QUESTION_TIMESTAMP);
    ticket.setServiceStreamId(SERVICE_STREAM_ID);
    ticket.setState(TicketClient.TicketStateType.UNSERVICED.getState());

    return ticket;
  }

  private UserInfo getInfo(String userInfo) {
    UserInfo user = new UserInfo();
    if (userInfo.toUpperCase().equals("AGENT")) {
      user.setUserId(AGENT_ID);
      user.setDisplayName(AGENT_NAME);
    } else {
      user.setUserId(CLIENT_ID);
      user.setDisplayName(CLIENT_NAME);
    }
    return user;
  }

}