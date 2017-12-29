package org.symphonyoss.symphony.bots.helpdesk.bot.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.symphony.bots.ai.HelpDeskAi;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.MakerCheckerMessageDetail;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.MakerCheckerResponse;
import org.symphonyoss.symphony.bots.helpdesk.bot.ticket.AcceptTicketService;
import org.symphonyoss.symphony.bots.helpdesk.bot.ticket.JoinConversationService;
import org.symphonyoss.symphony.bots.helpdesk.bot.util.ValidateMembershipService;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.MakerCheckerService;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.AttachmentMakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client.MakercheckerClient;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Makerchecker;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.validation.SymphonyValidationUtil;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.BadRequestException;


/**
 * Created by crepache on 15/12/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class V1HelpDeskControllerTest {

  private static final String MOCK_TICKET_ID = "LOEXALHQFJ";

  private static final Long MOCK_USER_ID = 123456L;

  private static final String MOCK_SERVICE_STREAM_ID = "RU0dsa0XfcE5SNoJKsXSKX___p-qTQ3XdA";

  private static final String MOCK_GROUP_ID = "HelpDesk";

  private static final String MOCK_CLIENT_STREAM_ID = "m3TYBJ-g-k9VDZLn5YaOuH___qA1PJWtdA";

  private static final String MOCK_MAKERCHECKER_ID = "XJW9H3XPCU";

  private static final Long MOCK_MAKER_ID = 10651518946916l;

  private static final Long MOCK_AGENT_ID = 10651518946915l;

  private static final Long MOCK_TIMESTAMP = 1513770327231l;

  private static final String MOCK_MESSAGE_ID = "BH9ZlIfUaV0ucEUPG8Xrfn___p-MQeNGdA";

  private static final String MOCK_PROXY_TO_STREAM_IDS = "XVc763EIcaSjFmTq0-zK4X___qBZkhQvdA";

  private static final String INACTIVE_OR_EXTERNAL_USER_EXCEPTION = "This action can not be "
      + "performed because agent is inactive or is external.";

  private static final String OPEN_MAKERCHECKER_NOT_FOUND =
      "This action can not be perfomed because this attachment was approved/denied before.";

  private static final String OWN_ATTACHMENT_EXCPETION =
      "You can not perform this action in your own attachment.";

  private static final String MOCKED_GUY = "MOCKED_GUY";
  private static final String SYM_MESSAGE = "SYM_MESSAGE";
  private static final String SYM_MESSAGE_ID = "SYM_MESSAGE_ID";
  private static final String MESSAGE_ACCEPTED = "Maker checker message accepted.";
  private static final String MESSAGE_DENIED = "Maker checker message denied.";


  @Mock
  private AcceptTicketService acceptTicketService;

  @Mock
  private JoinConversationService joinConversationService;

  @Mock
  private MakercheckerClient makercheckerClient;

  @Mock
  private SymphonyValidationUtil symphonyValidationUtil;

  @Mock
  private MembershipClient membershipClient;

  @Mock
  private SymphonyClient symphonyClient;

  @Mock
  private ValidateMembershipService validateMembershipService;

  @Mock
  private MakerCheckerService agentMakerCheckerService;

  @Mock
  private HelpDeskAi helpDeskAi;

  @InjectMocks
  private V1HelpDeskController v1HelpDeskController;

  @Test
  public void testAcceptTicket() {
    v1HelpDeskController.acceptTicket(MOCK_TICKET_ID, MOCK_USER_ID);
    verify(acceptTicketService, times(1)).execute(MOCK_TICKET_ID, MOCK_USER_ID);
  }

  @Test
  public void testJoinConversation() {
    v1HelpDeskController.joinConversation(MOCK_TICKET_ID, MOCK_USER_ID);
    verify(joinConversationService, times(1)).execute(MOCK_TICKET_ID, MOCK_USER_ID);
  }

  @Test()
  public void testApproveMakercheckerSameId() throws SymException {
    Makerchecker makerchecker = mockMakerchecker();
    doReturn(makerchecker).when(makercheckerClient).getMakerchecker(makerchecker.getId());

    MakerCheckerMessageDetail detail = mockMakerCheckerMessageDetail();

    try {
      v1HelpDeskController.approveMakerCheckerMessage(detail);
      fail();
    } catch (BadRequestException e) {
      assertEquals(OWN_ATTACHMENT_EXCPETION, e.getMessage());
    }
  }

  @Test(expected = BadRequestException.class)
  public void testInactiveAgent() throws SymException {
    Makerchecker makerchecker = mockMakerchecker();
    doReturn(makerchecker).when(makercheckerClient).getMakerchecker(makerchecker.getId());

    MakerCheckerMessageDetail detail = mockMakerCheckerMessageDetail();
    detail.setUserId(MOCK_AGENT_ID);

    doThrow(SymException.class).when(validateMembershipService).updateMembership(MOCK_AGENT_ID);

    v1HelpDeskController.approveMakerCheckerMessage(detail);
  }

  @Test()
  public void testDenyMakercheckerSameId() throws SymException {
    Makerchecker makerchecker = mockMakerchecker();
    doReturn(makerchecker).when(makercheckerClient).getMakerchecker(makerchecker.getId());

    MakerCheckerMessageDetail detail = mockMakerCheckerMessageDetail();

    try {
      v1HelpDeskController.denyMakerCheckerMessage(detail);
      fail();
    } catch (BadRequestException e) {
      assertEquals(OWN_ATTACHMENT_EXCPETION, e.getMessage());
    }
  }

  @Test()
  public void DenyUnoppenedAttachment() throws SymException {
    Makerchecker makerchecker = mockMakerchecker();
    makerchecker.setState(MakercheckerClient.AttachmentStateType.APPROVED.getState());
    doReturn(makerchecker).when(makercheckerClient).getMakerchecker(makerchecker.getId());

    MakerCheckerMessageDetail detail = mockMakerCheckerMessageDetail();
    detail.setUserId(MOCK_AGENT_ID);

    try {
      v1HelpDeskController.denyMakerCheckerMessage(detail);
      fail();
    } catch (BadRequestException e) {
      assertEquals(OPEN_MAKERCHECKER_NOT_FOUND, e.getMessage());
    }
  }

  @Test()
  public void ApproveUnoppenedAttachment() throws SymException {
    Makerchecker makerchecker = mockMakerchecker();
    makerchecker.setState(MakercheckerClient.AttachmentStateType.APPROVED.getState());
    doReturn(makerchecker).when(makercheckerClient).getMakerchecker(makerchecker.getId());

    MakerCheckerMessageDetail detail = mockMakerCheckerMessageDetail();
    detail.setUserId(MOCK_AGENT_ID);

    try {
      v1HelpDeskController.approveMakerCheckerMessage(detail);
      fail();
    } catch (BadRequestException e) {
      assertEquals(OPEN_MAKERCHECKER_NOT_FOUND, e.getMessage());
    }
  }

  @Test()
  public void ApproveAttachment() throws SymException {
    AttachmentMakerCheckerMessage attachmentMakerCheckerMessage =
        mockAttachmentMakerCheckerMessage();
    Makerchecker makerchecker = mockMakerchecker();
    doReturn(makerchecker).when(makercheckerClient).getMakerchecker(makerchecker.getId());

    SymUser agent = mockActiveSymUser();
    doReturn(agent).when(symphonyValidationUtil).validateUserId(MOCK_AGENT_ID);

    Set<SymMessage> symMessages = new HashSet<>();
    SymMessage message = mockSysMessage();
    symMessages.add(message);
    doReturn(symMessages).when(agentMakerCheckerService)
        .getAcceptMessages(attachmentMakerCheckerMessage);

    MakerCheckerMessageDetail detail = mockMakerCheckerMessageDetail();
    detail.setUserId(MOCK_AGENT_ID);

    MakerCheckerResponse response = v1HelpDeskController.approveMakerCheckerMessage(detail);
    assertEquals(MESSAGE_ACCEPTED, response.getMessage());

    verify(helpDeskAi, times(1)).getSessionKey(MOCK_AGENT_ID, MOCK_SERVICE_STREAM_ID);

  }

  @Test()
  public void DenyAttachment() throws SymException {
    Makerchecker makerchecker = mockMakerchecker();
    doReturn(makerchecker).when(makercheckerClient).getMakerchecker(makerchecker.getId());

    SymUser agent = mockActiveSymUser();
    doReturn(agent).when(symphonyValidationUtil).validateUserId(MOCK_AGENT_ID);

    MakerCheckerMessageDetail detail = mockMakerCheckerMessageDetail();
    detail.setUserId(MOCK_AGENT_ID);

    MakerCheckerResponse response = v1HelpDeskController.denyMakerCheckerMessage(detail);
    assertEquals(MESSAGE_DENIED, response.getMessage());

    verify(makercheckerClient, times(1)).updateMakerchecker(makerchecker);
    verify(helpDeskAi, never()).getSessionKey(MOCK_AGENT_ID, MOCK_SERVICE_STREAM_ID);

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

  private Makerchecker mockMakerchecker() {
    Makerchecker makerchecker = new Makerchecker();
    makerchecker.setState(MakercheckerClient.AttachmentStateType.OPENED.getState());
    makerchecker.setStreamId(MOCK_SERVICE_STREAM_ID);
    makerchecker.setId(MOCK_MAKERCHECKER_ID);
    makerchecker.setMakerId(MOCK_MAKER_ID);
    makerchecker.checker(null);

    return makerchecker;
  }

  private MakerCheckerMessageDetail mockMakerCheckerMessageDetail() {
    MakerCheckerMessageDetail detail = new MakerCheckerMessageDetail();
    detail.setUserId(MOCK_MAKER_ID);
    detail.setAttachmentId(MOCK_MAKERCHECKER_ID);
    detail.setGroupId(MOCK_GROUP_ID);
    detail.setMessageId(MOCK_MESSAGE_ID);
    detail.setProxyToStreamIds(null);
    detail.setStreamId(MOCK_SERVICE_STREAM_ID);
    detail.setTimeStamp(MOCK_TIMESTAMP);
    detail.setType(null);

    return detail;
  }

  private SymUser mockActiveSymUser() {
    SymUser symUser = new SymUser();
    symUser.setActive(true);
    symUser.setDisplayName(MOCKED_GUY);

    return symUser;
  }

  private SymMessage mockSysMessage() {
    SymMessage message = new SymMessage();
    message.setId(MOCK_MAKERCHECKER_ID);
    message.setMessage(SYM_MESSAGE);

    return message;
  }

  private AttachmentMakerCheckerMessage mockAttachmentMakerCheckerMessage() {
    AttachmentMakerCheckerMessage makerCheckerMessage = new AttachmentMakerCheckerMessage();
    makerCheckerMessage.setAttachmentId(MOCK_MAKERCHECKER_ID);
    makerCheckerMessage.setGroupId(MOCK_GROUP_ID);
    makerCheckerMessage.setMessageId(MOCK_MESSAGE_ID);
    makerCheckerMessage.setProxyToStreamIds(null);
    makerCheckerMessage.setStreamId(MOCK_SERVICE_STREAM_ID);
    makerCheckerMessage.setTimeStamp(MOCK_TIMESTAMP);
    makerCheckerMessage.setType(null);

    return makerCheckerMessage;
  }
}
