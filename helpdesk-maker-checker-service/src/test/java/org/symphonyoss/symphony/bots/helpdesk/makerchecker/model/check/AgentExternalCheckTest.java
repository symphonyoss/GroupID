package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.check;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.AttachmentMakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client.MakercheckerClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Makerchecker;
import org.symphonyoss.symphony.bots.helpdesk.service.model.UserInfo;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.validation.SymphonyValidationUtil;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class AgentExternalCheckTest {

  private static final String BOT_HOST = "https://nexus2.symphony.com/helpdesk-bot";

  private static final String SERVICE_HOST = "https://nexus2.symphony.com/helpdesk-service";

  private static final String GROUP_ID = "test";

  private static final String TICKET_SERVICE_URL = "https://localhost/helpdesk-service";

  private static final String ATTACHMENT_ID =
      "internal_9826885173254%2FxtpDCplNtIJluaYgvQkfGg%3D%3D";

  private static final String INVALID_ATTACHMENT_ID =
      "internal_8826885173254%2FxtpDCplNtIJluaYgvQkfGg%3D%3D";

  private static final String INVALID_ATTACHMENT_NAME = "INVALID_ATTACHMENT";

  private static final String ATTACHMENT_NAME = "ATTACHMENT_OK";

  private static final String MOCK_SERVICE_STREAM_ID = "RU0dsa0XfcE5SNoJKsXSKX___p-qTQ3XdA";

  private static final String MOCK_MAKERCHECKER_ID = "XJW9H3XPCU";

  private static final Long MOCK_MAKER_ID = 10651518946916l;

  private static final String MOCK_DISPLAY_NAME = "Financial Agent";

  public static final long CHECKER_ID = 230187l;

  private static final String CHECKER_DISPLAY_NAME = "Financial Agent";

  private static final String ACTION_MESSAGE_APPROVED_ENTITY_DATA = "{\"makerchecker\":{\"type\":\"com"
      + ".symphony.bots.helpdesk.event.makerchecker.action.performed\",\"version\":\"1.0\","
      + "\"checker\":{\"userId\":10651518946916,\"displayName\":\"Financial Agent\"},"
      + "\"makerCheckerId\":\"XJW9H3XPCU\",\"state\":\"APPROVED\",\"messageToAgents\":\"Financial"
      + " Agent approved this message. It has been delivered to the client(s).\"}}";

  private static final String ACTION_MESSAGE_DENIED_ENTITY_DATA = "{\"makerchecker\":{\"type"
      + "\":\"com.symphony.bots.helpdesk.event.makerchecker.action.performed\","
      + "\"version\":\"1.0\",\"checker\":{\"userId\":10651518946916,\"displayName\":\"Financial "
      + "Agent\"},\"makerCheckerId\":\"XJW9H3XPCU\",\"state\":\"DENIED\","
      + "\"messageToAgents\":\"Financial Agent denied this message. It has not been delivered to "
      + "the client(s).\"}}";

  private static final String ACTION_MESSAGE = "<messageML>    <div class=\"entity\" "
      + "data-entity-id=\"makerchecker\">        <card class=\"barStyle\">            <header>   "
      + "             ${entity['makerchecker'].messageToAgents}            </header>        "
      + "</card>    </div></messageML>";

  private AgentExternalCheck agentExternalCheck;

  @Mock
  private SymphonyClient symphonyClient;

  @Mock
  private SymphonyValidationUtil symphonyValidationUtil;

  @Before
  public void init() {
    TicketClient ticketClient = new TicketClient(GROUP_ID, TICKET_SERVICE_URL);
    agentExternalCheck = new AgentExternalCheck(BOT_HOST, SERVICE_HOST, GROUP_ID, ticketClient, symphonyClient, symphonyValidationUtil);
  }

  @Test
  public void testGetApprovedAttachment() {
    AttachmentMakerCheckerMessage attachmentMakerCheckerMessage = mockAttachmentMakerCheckerMessage();
    SymMessage symMessage = mockSymMessage();


    Optional<SymAttachmentInfo> symAttachmentInfo = agentExternalCheck.getApprovedAttachment(attachmentMakerCheckerMessage, symMessage);

    assertEquals(ATTACHMENT_ID, symAttachmentInfo.get().getId());
    assertEquals(ATTACHMENT_NAME, symAttachmentInfo.get().getName());
  }

  @Test
  public void testGetApprovedAttachmentNull() {
    AttachmentMakerCheckerMessage attachmentMakerCheckerMessage = mockAttachmentMakerCheckerMessage();
    SymMessage symMessage = new SymMessage();


    Optional<SymAttachmentInfo> symAttachmentInfo = agentExternalCheck.getApprovedAttachment(attachmentMakerCheckerMessage, symMessage);

    assertFalse(symAttachmentInfo.isPresent());
  }

  @Test
  public void testGetActionMessageApproved() {
    Makerchecker makerchecker = mockMakercheckerApproved();

    doReturn(mockSymUser()).when(symphonyValidationUtil).validateUserId(any());

    SymMessage symMessage = agentExternalCheck.getActionMessage(makerchecker, MakercheckerClient.AttachmentStateType.APPROVED);

    Assert.assertEquals(ACTION_MESSAGE, symMessage.getMessage());
    Assert.assertEquals(ACTION_MESSAGE_APPROVED_ENTITY_DATA, symMessage.getEntityData());
  }

  @Test
  public void testGetActionMessageDenied() {
    Makerchecker makerchecker = mockMakercheckerApproved();

    doReturn(mockSymUser()).when(symphonyValidationUtil).validateUserId(any());

    SymMessage symMessage = agentExternalCheck.getActionMessage(makerchecker, MakercheckerClient.AttachmentStateType.DENIED);

    Assert.assertEquals(ACTION_MESSAGE_DENIED_ENTITY_DATA, symMessage.getEntityData());
  }

  private AttachmentMakerCheckerMessage mockAttachmentMakerCheckerMessage() {
    AttachmentMakerCheckerMessage attachmentMakerCheckerMessage = new AttachmentMakerCheckerMessage();
    attachmentMakerCheckerMessage.setAttachmentId(ATTACHMENT_ID);
    attachmentMakerCheckerMessage.setGroupId(GROUP_ID);

    return attachmentMakerCheckerMessage;
  }

  private SymMessage mockSymMessage() {
    SymMessage symMessage = new SymMessage();
    symMessage.setAttachments(mockSymAttachmentInfoList());

    return symMessage;
  }

  private List<SymAttachmentInfo> mockSymAttachmentInfoList() {
    List<SymAttachmentInfo> attachmentInfoList = new ArrayList<>();

    SymAttachmentInfo symAttachmentInfo = new SymAttachmentInfo();
    symAttachmentInfo.setId(INVALID_ATTACHMENT_ID);
    symAttachmentInfo.setName(INVALID_ATTACHMENT_NAME);
    attachmentInfoList.add(symAttachmentInfo);

    symAttachmentInfo = new SymAttachmentInfo();
    symAttachmentInfo.setId(ATTACHMENT_ID);
    symAttachmentInfo.setName(ATTACHMENT_NAME);
    attachmentInfoList.add(symAttachmentInfo);

    return attachmentInfoList;
  }

  private Makerchecker mockMakercheckerApproved() {
    Makerchecker makerchecker = new Makerchecker();
    makerchecker.setState(MakercheckerClient.AttachmentStateType.APPROVED.getState());
    makerchecker.setStreamId(MOCK_SERVICE_STREAM_ID);
    makerchecker.setId(MOCK_MAKERCHECKER_ID);
    makerchecker.setMakerId(MOCK_MAKER_ID);

    UserInfo checker = new UserInfo();
    checker.setUserId(CHECKER_ID);
    checker.setDisplayName(CHECKER_DISPLAY_NAME);

    makerchecker.checker(checker);

    return makerchecker;
  }

  private SymUser mockSymUser() {
    SymUser symUser = new SymUser();
    symUser.setId(MOCK_MAKER_ID);
    symUser.setDisplayName(MOCK_DISPLAY_NAME);

    return symUser;
  }

}