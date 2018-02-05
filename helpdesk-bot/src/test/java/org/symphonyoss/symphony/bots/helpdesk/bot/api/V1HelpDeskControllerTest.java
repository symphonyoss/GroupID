package org.symphonyoss.symphony.bots.helpdesk.bot.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.symphony.bots.ai.HelpDeskAi;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.MakerCheckerResponse;
import org.symphonyoss.symphony.bots.helpdesk.bot.ticket.AcceptTicketService;
import org.symphonyoss.symphony.bots.helpdesk.bot.ticket.JoinConversationService;
import org.symphonyoss.symphony.bots.helpdesk.bot.util.ValidateMembershipService;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.MakerCheckerService;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.AttachmentMakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client.MakercheckerClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Makerchecker;
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

  private static final String MOCK_MAKERCHECKER_ID = "XJW9H3XPCU";

  private static final String MOCK_ACTION_MESSAGE_ID = "HDW9H9XPWQ";

  private static final String MOCK_ATTACHMENT_ID = "internal_9826885173254";

  private static final Long MOCK_MAKER_ID = 10651518946916l;

  private static final Long MOCK_AGENT_ID = 10651518946915l;

  private static final String OPEN_MAKERCHECKER_NOT_FOUND =
      "This action can not be perfomed because this attachment was approved/denied before.";

  private static final String OWN_ATTACHMENT_EXCPETION =
      "You can not perform this action in your own attachment.";

  private static final String MOCKED_GUY = "MOCKED_GUY";
  private static final String SYM_MESSAGE = "SYM_MESSAGE";
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

    try {
      v1HelpDeskController.approveMakerCheckerMessage(MOCK_MAKERCHECKER_ID, MOCK_MAKER_ID);
      fail();
    } catch (BadRequestException e) {
      assertEquals(OWN_ATTACHMENT_EXCPETION, e.getMessage());
    }
  }

  @Test(expected = BadRequestException.class)
  public void testInactiveAgent() throws SymException {
    Makerchecker makerchecker = mockMakerchecker();
    doReturn(makerchecker).when(makercheckerClient).getMakerchecker(makerchecker.getId());

    doThrow(SymException.class).when(validateMembershipService).updateMembership(MOCK_AGENT_ID);

    v1HelpDeskController.approveMakerCheckerMessage(MOCK_MAKERCHECKER_ID, MOCK_AGENT_ID);
  }

  @Test()
  public void testDenyMakercheckerSameId() throws SymException {
    Makerchecker makerchecker = mockMakerchecker();
    doReturn(makerchecker).when(makercheckerClient).getMakerchecker(makerchecker.getId());

    try {
      v1HelpDeskController.denyMakerCheckerMessage(MOCK_MAKERCHECKER_ID, MOCK_MAKER_ID);
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

    try {
      v1HelpDeskController.denyMakerCheckerMessage(MOCK_MAKERCHECKER_ID, MOCK_AGENT_ID);
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

    try {
      v1HelpDeskController.approveMakerCheckerMessage(MOCK_MAKERCHECKER_ID, MOCK_AGENT_ID);
      fail();
    } catch (BadRequestException e) {
      assertEquals(OPEN_MAKERCHECKER_NOT_FOUND, e.getMessage());
    }
  }

  @Test()
  public void ApproveAttachment() throws SymException {
    Makerchecker makerchecker = mockMakerchecker();
    doReturn(makerchecker).when(makercheckerClient).getMakerchecker(makerchecker.getId());

    SymUser agent = mockActiveSymUser();
    doReturn(agent).when(symphonyValidationUtil).validateUserId(MOCK_AGENT_ID);

    Set<SymMessage> symMessages = mockSymMessages();
    doReturn(symMessages).when(agentMakerCheckerService)
        .getApprovedMakercheckerMessage(any(AttachmentMakerCheckerMessage.class));

    MakerCheckerResponse response = v1HelpDeskController.approveMakerCheckerMessage(MOCK_MAKERCHECKER_ID, MOCK_AGENT_ID);
    assertEquals(MESSAGE_ACCEPTED, response.getMessage());

    verify(helpDeskAi, times(1)).getSessionKey(MOCK_AGENT_ID, MOCK_SERVICE_STREAM_ID);
    verify(agentMakerCheckerService, times(1)).sendActionMakerCheckerMessage(makerchecker,
        MakercheckerClient.AttachmentStateType.APPROVED);
  }

  @Test()
  public void DenyAttachment() throws SymException {
    Makerchecker makerchecker = mockMakerchecker();
    doReturn(makerchecker).when(makercheckerClient).getMakerchecker(makerchecker.getId());

    SymUser agent = mockActiveSymUser();
    doReturn(agent).when(symphonyValidationUtil).validateUserId(MOCK_AGENT_ID);

    MakerCheckerResponse response = v1HelpDeskController.denyMakerCheckerMessage(MOCK_MAKERCHECKER_ID, MOCK_AGENT_ID);
    assertEquals(MESSAGE_DENIED, response.getMessage());

    verify(makercheckerClient, times(1)).updateMakerchecker(makerchecker);
    verify(helpDeskAi, times(0)).getSessionKey(MOCK_AGENT_ID, MOCK_SERVICE_STREAM_ID);
    verify(agentMakerCheckerService, times(1)).sendActionMakerCheckerMessage(makerchecker,
        MakercheckerClient.AttachmentStateType.DENIED);
  }

  private Makerchecker mockMakerchecker() {
    Makerchecker makerchecker = new Makerchecker();
    makerchecker.setState(MakercheckerClient.AttachmentStateType.OPENED.getState());
    makerchecker.setStreamId(MOCK_SERVICE_STREAM_ID);
    makerchecker.setId(MOCK_MAKERCHECKER_ID);
    makerchecker.setMakerId(MOCK_MAKER_ID);
    makerchecker.setAttachmentId(MOCK_ATTACHMENT_ID);
    makerchecker.checker(null);

    return makerchecker;
  }

  private SymUser mockActiveSymUser() {
    SymUser symUser = new SymUser();
    symUser.setActive(true);
    symUser.setDisplayName(MOCKED_GUY);

    return symUser;
  }

  private Set<SymMessage> mockSymMessages() {
    Set<SymMessage> symMessages = new HashSet<>();

    SymMessage message = new SymMessage();
    message.setId(MOCK_MAKERCHECKER_ID);
    message.setMessage(SYM_MESSAGE);

    symMessages.add(message);

    return symMessages;
  }

  private SymMessage mockActionMessage() {
    SymMessage symMessage = new SymMessage();
    symMessage.setId(MOCK_ACTION_MESSAGE_ID);
    symMessage.setMessage(SYM_MESSAGE);

    return symMessage;
  }
}
