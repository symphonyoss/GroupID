package org.symphonyoss.symphony.bots.helpdesk.service.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient
    .MembershipType.CLIENT;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client.MakercheckerClient;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.dao.MakercheckerDao;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.dao.MembershipDao;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Makerchecker;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.model.SuccessResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.model.TicketSearchResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.model.UserInfo;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.dao.TicketDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexandre-silva-daitan on 29/12/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class V1HelpDeskServiceControllerTest {

  private static final String MOCK_MAKERCHECKER_ID = "XJW9H3XPCU";

  private static final String MOCK_NEW_MAKERCHECKER_ID = "XJW9H3XP00";

  private static final Long MOCK_MAKER_ID = 10651518946916l;

  private static final String MOCK_SERVICE_STREAM_ID = "RU0dsa0XfcE5SNoJKsXSKX___p-qTQ3XdA";

  private static final String DISPLAY_NAME = "DISPLAY_NAME";

  private static final Long MOCK_USER_ID = 1234567l;

  private static final Long MEMBERSHIP_ID_MOCK = 999999l;

  private static final String GROUP_ID_MOCK = "GROUP_ID";

  private static final String DELETE_MEMBERSHIP_RESPONSE = "Membership deleted.";

  private static final Long NEW_MEMBERSHIP_ID_MOCK = 888888L;

  private static final String MOCK_TICKET_ID = "LOEXALHQFJ";

  private static final String MOCK_CLIENT_STREAM_ID = "m3TYBJ-g-k9VDZLn5YaOuH___qA1PJWtdA";

  private static final String DELETE_TICKET_RESPONSE = "Ticket deleted.";

  private static final String SERVICE_ROOM_ID_MOCK = "SERVICE_ROOM_ID_MOCK";

  private static final String MOCK_NEW_TICKET_ID = "MOCK_NEW_TICKET_ID";

  @Mock
  private MakercheckerDao makercheckerDao;

  @Mock
  private MembershipDao membershipDao;

  @Mock
  private TicketDao ticketDao;

  @InjectMocks
  private V1HelpDeskServiceController v1HelpDeskServiceController;

  @Test
  public void createMembership() throws Exception {
    Membership mockMembership = membershipMock();

    doReturn(mockMembership).when(membershipDao).createMembership(mockMembership);

    Membership membership = v1HelpDeskServiceController.createMembership(mockMembership);
    assertEquals(mockMembership, membership);
  }

  @Test
  public void deleteMembership() throws Exception {
    SuccessResponse response =
        v1HelpDeskServiceController.deleteMembership(GROUP_ID_MOCK, MEMBERSHIP_ID_MOCK);
    assertEquals(DELETE_MEMBERSHIP_RESPONSE, response.getMessage());
  }

  @Test
  public void getMembership() throws Exception {
    Membership mockMembership = membershipMock();
    doReturn(mockMembership).when(membershipDao).getMembership(GROUP_ID_MOCK, MEMBERSHIP_ID_MOCK);
    Membership membership =
        v1HelpDeskServiceController.getMembership(GROUP_ID_MOCK, MEMBERSHIP_ID_MOCK);
    assertEquals(membershipMock(), membership);
  }

  @Test
  public void updateMembership() throws Exception {
    Membership mockMembership = membershipMock();
    mockMembership.setId(NEW_MEMBERSHIP_ID_MOCK);
    doReturn(mockMembership).when(membershipDao)
        .updateMembership(GROUP_ID_MOCK, NEW_MEMBERSHIP_ID_MOCK, membershipMock());
    Membership membership =
        v1HelpDeskServiceController.updateMembership(GROUP_ID_MOCK, NEW_MEMBERSHIP_ID_MOCK,
            membershipMock());
    assertEquals(NEW_MEMBERSHIP_ID_MOCK, membership.getId());
  }

  @Test
  public void createTicket() throws Exception {
    Ticket ticket = mockTicketUnresolved();
    doReturn(ticket).when(ticketDao).createTicket(ticket);
    Ticket createdTicket = v1HelpDeskServiceController.createTicket(ticket);
    assertEquals(ticket, createdTicket);

  }

  @Test
  public void deleteTicket() throws Exception {
    SuccessResponse message = v1HelpDeskServiceController.deleteTicket(MOCK_TICKET_ID);
    assertEquals(DELETE_TICKET_RESPONSE,  message.getMessage());
  }

  @Test
  public void getTicket() throws Exception {
    Ticket ticket = mockTicketUnresolved();
    doReturn(ticket).when(ticketDao).getTicket(MOCK_TICKET_ID);
    Ticket foundTicket = v1HelpDeskServiceController.getTicket(MOCK_TICKET_ID);
    assertEquals(ticket, foundTicket);
  }

  @Test
  public void searchTicket() throws Exception {
    List<Ticket> tickets = new ArrayList<>();
    tickets.add(mockTicketUnresolved());
    doReturn(tickets).when(ticketDao).searchTicket(GROUP_ID_MOCK, SERVICE_ROOM_ID_MOCK, MOCK_CLIENT_STREAM_ID);
    TicketSearchResponse ticketSearchResponse = v1HelpDeskServiceController.searchTicket(GROUP_ID_MOCK, SERVICE_ROOM_ID_MOCK, MOCK_CLIENT_STREAM_ID);
    assertNotNull(ticketSearchResponse);
  }

  @Test
  public void searchTicketEmptyList() throws Exception {
    doReturn(null).when(ticketDao).searchTicket(GROUP_ID_MOCK, SERVICE_ROOM_ID_MOCK, MOCK_CLIENT_STREAM_ID);
    TicketSearchResponse ticketSearchResponse = v1HelpDeskServiceController.searchTicket(GROUP_ID_MOCK, SERVICE_ROOM_ID_MOCK, MOCK_CLIENT_STREAM_ID);
    assertEquals(null, ticketSearchResponse);
  }

  @Test
  public void updateTicket() throws Exception {
    Ticket ticket = mockTicketUnresolved();
    ticket.setId(MOCK_NEW_TICKET_ID);
    doReturn(ticket).when(ticketDao).updateTicket(MOCK_NEW_TICKET_ID, ticket);
    Ticket updatedTicket = v1HelpDeskServiceController.updateTicket(MOCK_NEW_TICKET_ID, ticket);
    assertEquals(ticket, updatedTicket);
  }

  @Test
  public void createMakerchecker() throws Exception {
    Makerchecker makercheckerMock = makercheckerMock();
    doReturn(makercheckerMock).when(makercheckerDao).createMakerchecker(makercheckerMock);
    Makerchecker makerchecker = v1HelpDeskServiceController.createMakerchecker(makercheckerMock());
    assertEquals(makercheckerMock, makerchecker);
  }

  @Test
  public void getMakerchecker() throws Exception {
    Makerchecker makercheckerMock = makercheckerMock();
    doReturn(makercheckerMock).when(makercheckerDao).getMakerchecker(MOCK_MAKERCHECKER_ID);
    Makerchecker makerchecker = v1HelpDeskServiceController.getMakerchecker(MOCK_MAKERCHECKER_ID);
    assertEquals(makercheckerMock, makerchecker);

  }


  @Test
  public void updateMakerchecker() throws Exception {
    Makerchecker makerchecker = makercheckerMock();
    Makerchecker updatedMakerchecker = updatedMakercheckerMock();

    doReturn(updatedMakerchecker).when(makercheckerDao)
        .updateMakerchecker(MOCK_NEW_MAKERCHECKER_ID, makerchecker);

    makerchecker =
        v1HelpDeskServiceController.updateMakerchecker(MOCK_NEW_MAKERCHECKER_ID, makerchecker);
    assertEquals(updatedMakerchecker, makerchecker);
  }

  private Makerchecker makercheckerMock() {
    Makerchecker makerchecker = new Makerchecker();
    makerchecker.setChecker(userInfoMock());
    makerchecker.setState(MakercheckerClient.AttachmentStateType.OPENED.getState());
    makerchecker.setStreamId(MOCK_SERVICE_STREAM_ID);
    makerchecker.setId(MOCK_MAKERCHECKER_ID);
    makerchecker.setMakerId(MOCK_MAKER_ID);

    return makerchecker;
  }

  private Makerchecker updatedMakercheckerMock() {
    Makerchecker makerchecker = new Makerchecker();
    makerchecker.setChecker(userInfoMock());
    makerchecker.setState(MakercheckerClient.AttachmentStateType.OPENED.getState());
    makerchecker.setStreamId(MOCK_SERVICE_STREAM_ID);
    makerchecker.setId(MOCK_NEW_MAKERCHECKER_ID);
    makerchecker.setMakerId(MOCK_MAKER_ID);

    return makerchecker;
  }

  private UserInfo userInfoMock() {
    UserInfo userInfo = new UserInfo();
    userInfo.displayName(DISPLAY_NAME);
    userInfo.setUserId(MOCK_USER_ID);

    return userInfo;
  }

  private Membership membershipMock() {
    Membership membership = new Membership();
    membership.setId(MEMBERSHIP_ID_MOCK);
    membership.setGroupId(GROUP_ID_MOCK);
    membership.setType(CLIENT.getType());

    return membership;
  }

  private Ticket mockTicketUnresolved() {
    Ticket ticket = new Ticket();
    ticket.setId(MOCK_TICKET_ID);
    ticket.setState(TicketClient.TicketStateType.UNRESOLVED.getState());
    ticket.setGroupId(GROUP_ID_MOCK);
    ticket.setClientStreamId(MOCK_CLIENT_STREAM_ID);
    ticket.setQuestionTimestamp(1513266273011l);
    ticket.setServiceStreamId(MOCK_SERVICE_STREAM_ID);

    return ticket;
  }

}