package org.symphonyoss.symphony.bots.helpdesk.service.ticket.client;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

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

@RunWith(MockitoJUnitRunner.class)
public class TicketClientTest {

  private static final String TICKET_ID = "TICKET_ID";
  private static final String MOCK_CLIENT_STREAM_ID = "MOCK_CLIENT_STREAM_ID";
  private static final Long TIMESTAMP = 1L;
  private static final Long USER_ID = 123L;
  private static final String DISPLAY_NAME = "DISPLAY_NAME";
  private static final String CONVERSATION_ID = "CONVERSATION_ID";
  private static final String MOCK_SERVICE_STREAM_ID = "MOCK_SERVICE_STREAM_ID";
  private static final String GROUP_ID = "GROUP_ID";

  private static final String TICKET_SERVICE_URL = "https://localhost/helpdesk-service";

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
    doReturn(getMockTicket()).when(apiClient)
        .invokeAPI(eq("/v1/ticket/TICKET_ID"), eq("GET"), any(), any(), any(), any(), any(), any(), any(), any());
    Ticket ticket = ticketClient.getTicket(TICKET_ID);
    assertNotNull(ticket);
    assertEquals(getMockTicket(), ticket);
  }

  @Test
  public void getTicketWithError() {
    try {
      ticketClient.getTicket(null);
      fail();
    } catch (HelpDeskApiException e) {
      assertEquals("Get ticket failed: " + null, e.getMessage());
    }
  }

  @Test
  public void createTicket() throws ApiException {

    doReturn(getMockTicket()).when(apiClient)
        .invokeAPI(eq("/v1/ticket"), eq("POST"), any(), any(), any(), any(), any(), any(), any(), any());
    Ticket createdTicket =
        ticketClient.createTicket(TICKET_ID, MOCK_CLIENT_STREAM_ID, MOCK_SERVICE_STREAM_ID,
            TIMESTAMP, mockClient(), Boolean.FALSE, CONVERSATION_ID);

    assertNotNull(createdTicket);
    assertEquals(getMockTicket(), createdTicket);
  }

  @Test
  public void createTicketFail() throws ApiException {
    doThrow(ApiException.class).when(apiClient)
        .invokeAPI(eq("/v1/ticket"), eq("POST"), any(), any(), any(), any(), any(), any(), any(), any());
    try {
      ticketClient.createTicket(TICKET_ID, MOCK_CLIENT_STREAM_ID, MOCK_SERVICE_STREAM_ID,
          TIMESTAMP, mockClient(), Boolean.FALSE, CONVERSATION_ID);
      fail();
    } catch (HelpDeskApiException e) {
      assertEquals("Creating ticket failed: " + TICKET_ID, e.getMessage());
    }
  }

  @Test
  public void getTicketByServiceStreamId() throws ApiException {
    TicketSearchResponse response = getTicketSearchResponse();

    doReturn(response)
        .when(apiClient)
        .invokeAPI(eq("/v1/ticket/search"), eq("GET"), any(), any(), any(), any(), any(), any(), any(), any());

    Ticket ticket = ticketClient.getTicketByServiceStreamId(MOCK_SERVICE_STREAM_ID);

    assertNotNull(ticket);
    assertEquals(getMockTicket(), ticket);
  }

  @Test
  public void getNoTicketByServiceStreamId() throws ApiException {
    doReturn(null)
        .when(apiClient)
        .invokeAPI(eq("/v1/ticket/search"), eq("GET"), any(), any(), any(), any(), any(), any(), any(), any());

    Ticket ticket = ticketClient.getTicketByServiceStreamId(MOCK_SERVICE_STREAM_ID);

    assertNull(ticket);
  }

  @Test
  public void getTicketByServiceStreamIdFail() throws ApiException {
    doThrow(ApiException.class)
        .when(apiClient)
        .invokeAPI(eq("/v1/ticket/search"), eq("GET"), any(), any(), any(), any(), any(), any(), any(), any());

    try {
      ticketClient.getTicketByServiceStreamId(MOCK_SERVICE_STREAM_ID);
      fail();
    } catch (HelpDeskApiException e) {
      assertEquals("Failed to search for room: " + MOCK_SERVICE_STREAM_ID, e.getMessage());
    }
  }

  @Test
  public void getUnresolvedTicketByClientStreamId() throws ApiException {
    TicketSearchResponse response = getTicketSearchResponse();

    doReturn(response)
        .when(apiClient)
        .invokeAPI(eq("/v1/ticket/search"), eq("GET"), any(), any(), any(), any(), any(), any(), any(), any());

     Ticket ticket =  ticketClient.getUnresolvedTicketByClientStreamId(MOCK_CLIENT_STREAM_ID);

     assertNotNull(ticket);
     assertEquals(getMockTicket(),ticket);
  }

  @Test
  public void getNotUnresolvedTicketByClientStreamId() throws ApiException {
    Ticket ticket = getMockTicket();
    ticket.setState(TicketClient.TicketStateType.RESOLVED.getState());

    TicketSearchResponse response = new TicketSearchResponse();
    response.add(ticket);

    doReturn(response)
        .when(apiClient)
        .invokeAPI(eq("/v1/ticket/search"), eq("GET"), any(), any(), any(), any(), any(), any(), any(), any());

    ticket =  ticketClient.getUnresolvedTicketByClientStreamId(MOCK_CLIENT_STREAM_ID);

    assertNull(ticket);
  }

  @Test
  public void getUnresolvedTicketByServiceStreamIdFail() throws ApiException {
    doThrow(ApiException.class)
        .when(apiClient)
        .invokeAPI(eq("/v1/ticket/search"), eq("GET"), any(), any(), any(), any(), any(), any(), any(), any());

    try {
      ticketClient.getUnresolvedTicketByClientStreamId(MOCK_CLIENT_STREAM_ID);
      fail();
    } catch (HelpDeskApiException e) {
      assertEquals("Failed to search for room: " + MOCK_CLIENT_STREAM_ID, e.getMessage());
    }
  }

  @Test
  public void updateTicket() throws ApiException {
    Ticket ticket = getMockTicket();
    ticket.setState(TicketClient.TicketStateType.RESOLVED.getState());

    doReturn(TICKET_ID).when(apiClient).escapeString(TICKET_ID);

    doReturn(ticket).when(apiClient)
        .invokeAPI(eq("/v1/ticket/TICKET_ID"), eq("PUT"), any(), any(), any(), any(), any(), any(), any(), any());

    Ticket updatedTicket = ticketClient.updateTicket(ticket);

    assertNotNull(updatedTicket);
    assertNotEquals(getMockTicket().getState(),updatedTicket.getState());
  }

  @Test
  public void updateTicketWithError() {
    try {
      ticketClient.updateTicket(new Ticket());
      fail();
    } catch (HelpDeskApiException e) {
      assertEquals("Updating ticket failed: " + null, e.getMessage());
    }
  }

  private Ticket getMockTicket() {
    Ticket mock = new Ticket();

    mock.setId(TICKET_ID);
    mock.setServiceStreamId(MOCK_SERVICE_STREAM_ID);
    mock.setClientStreamId(MOCK_CLIENT_STREAM_ID);
    mock.setState(TicketClient.TicketStateType.UNRESOLVED.getState());
    mock.setShowHistory(Boolean.FALSE);
    mock.setQuestionTimestamp(TIMESTAMP);
    mock.setClient(mockClient());

    return mock;
  }

  private UserInfo mockClient() {
    UserInfo userInfo = new UserInfo();
    userInfo.setUserId(USER_ID);
    userInfo.setDisplayName(DISPLAY_NAME);
    return userInfo;
  }

  private TicketSearchResponse getTicketSearchResponse() {
    TicketSearchResponse response = new TicketSearchResponse();
    response.add(getMockTicket());
    return response;
  }

}