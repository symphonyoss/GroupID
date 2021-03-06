package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.check;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient
    .TicketStateType.UNRESOLVED;
import static org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient
    .TicketStateType.UNSERVICED;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.authenticator.model.Token;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.AttachmentMakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client.MakercheckerClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Makerchecker;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.model.UserInfo;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.validation.SymphonyValidationUtil;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class AgentExternalCheckTest {

  private static final String BOT_HOST = "https://nexus2.symphony.com/helpdesk-bot";

  private static final String SERVICE_HOST = "https://nexus2.symphony.com/helpdesk-service";

  private static final String GROUP_ID = "test";

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

  private static final String MOCK_ATTACHMENT_NAME = "Symphony.png";

  private static final String ACTION_MESSAGE_APPROVED_ENTITY_DATA =
      "{\"makerchecker\":{\"type\":\"com"
          + ".symphony.bots.helpdesk.event.makerchecker.action.performed\",\"version\":\"1.0\","
          + "\"checker\":{\"userId\":10651518946916,\"displayName\":\"Financial Agent\"},"
          + "\"makerCheckerId\":\"XJW9H3XPCU\",\"state\":\"APPROVED\","
          + "\"messageToAgents\":\"Financial"
          + " Agent approved Symphony.png attachment. It has been delivered to the client(s).\"}}";

  private static final String ACTION_MESSAGE_DENIED_ENTITY_DATA = "{\"makerchecker\":{\"type"
      + "\":\"com.symphony.bots.helpdesk.event.makerchecker.action.performed\","
      + "\"version\":\"1.0\",\"checker\":{\"userId\":10651518946916,\"displayName\":\"Financial "
      + "Agent\"},\"makerCheckerId\":\"XJW9H3XPCU\",\"state\":\"DENIED\","
      + "\"messageToAgents\":\"Financial Agent denied Symphony.png attachment. It has not been "
      + "delivered to "
      + "the client(s).\"}}";

  private static final String ACTION_MESSAGE = "<messageML>    <div class=\"entity\" "
      + "data-entity-id=\"makerchecker\">        <card class=\"barStyle\">            <header>   "
      + "             ${entity['makerchecker'].messageToAgents}            </header>        "
      + "</card>    </div></messageML>";

  private static final String JWT = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9."
      + "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9";

  public static final long FROM_USER_ID = 99129L;

  private AgentExternalCheck agentExternalCheck;

  @Mock
  private SymphonyClient symphonyClient;

  @Mock
  private TicketClient ticketClient;

  @Mock
  private SymphonyValidationUtil symphonyValidationUtil;

  @Before
  public void init() {
    agentExternalCheck =
        new AgentExternalCheck(BOT_HOST, SERVICE_HOST, GROUP_ID, ticketClient, symphonyClient,
            symphonyValidationUtil);

    Token sessionToken = new Token();
    sessionToken.setToken(JWT);

    SymAuth symAuth = new SymAuth();
    symAuth.setSessionToken(sessionToken);

    doReturn(symAuth).when(symphonyClient).getSymAuth();
  }

  @Test
  public void testGetApprovedAttachment() {
    AttachmentMakerCheckerMessage attachmentMakerCheckerMessage =
        mockAttachmentMakerCheckerMessage();
    SymMessage symMessage = mockSymMessage();


    Optional<SymAttachmentInfo> symAttachmentInfo =
        agentExternalCheck.getApprovedAttachment(attachmentMakerCheckerMessage, symMessage);

    assertEquals(ATTACHMENT_ID, symAttachmentInfo.get().getId());
    assertEquals(ATTACHMENT_NAME, symAttachmentInfo.get().getName());
  }

  @Test
  public void testGetApprovedAttachmentNull() {
    AttachmentMakerCheckerMessage attachmentMakerCheckerMessage =
        mockAttachmentMakerCheckerMessage();
    SymMessage symMessage = new SymMessage();


    Optional<SymAttachmentInfo> symAttachmentInfo =
        agentExternalCheck.getApprovedAttachment(attachmentMakerCheckerMessage, symMessage);

    assertFalse(symAttachmentInfo.isPresent());
  }

  @Test
  public void testGetActionMessageApproved() {
    Makerchecker makerchecker = mockMakercheckerApproved();

    doReturn(mockSymUser()).when(symphonyValidationUtil).validateUserId(any());

    SymMessage symMessage = agentExternalCheck.getActionMessage(makerchecker,
        MakercheckerClient.AttachmentStateType.APPROVED);

    Assert.assertEquals(ACTION_MESSAGE, symMessage.getMessage());
    Assert.assertEquals(ACTION_MESSAGE_APPROVED_ENTITY_DATA, symMessage.getEntityData());
  }

  @Test
  public void testGetActionMessageDenied() {
    Makerchecker makerchecker = mockMakercheckerApproved();

    doReturn(mockSymUser()).when(symphonyValidationUtil).validateUserId(any());

    SymMessage symMessage = agentExternalCheck.getActionMessage(makerchecker,
        MakercheckerClient.AttachmentStateType.DENIED);

    Assert.assertEquals(ACTION_MESSAGE_DENIED_ENTITY_DATA, symMessage.getEntityData());
  }

  @Test
  public void testAfterSendApprovedMsg() throws IOException {
    String tmpDir = System.getProperty("java.io.tmpdir");

    File directory = new File(tmpDir + File.separator + ATTACHMENT_ID);
    File directory2 = new File(tmpDir + File.separator + INVALID_ATTACHMENT_ID);
    directory.mkdir();
    directory2.mkdir();

    agentExternalCheck.afterSendApprovedMessage(mockSymMessage());

    assertFalse(directory.exists());
    assertFalse(directory2.exists());
  }

  @Test
  public void testBuildSymCheckerMessages() {
    Set<String> ids = new HashSet<>();
    ids.add(ATTACHMENT_ID);

    String timestamp = String.valueOf(new Date().getTime());
    SymMessage symMessage = mockSymMessage();
    symMessage.setFromUserId(FROM_USER_ID);
    symMessage.setTimestamp(timestamp);


    Set<SymMessage> symMessages = agentExternalCheck.buildSymCheckerMessages(symMessage, ids);

    assertEquals(2, symMessages.size());
    symMessages.stream().forEach(msg -> assertEquals(timestamp, msg.getTimestamp()));
  }

  @Test
  public void testCheckHasOpenedTicket() {
    SymMessage symMessage = mockSymMessage();
    symMessage.setStreamId(MOCK_SERVICE_STREAM_ID);

    Ticket ticket = new Ticket();
    ticket.setState(UNRESOLVED.getState());

    when(ticketClient.getTicketByServiceStreamId(JWT, MOCK_SERVICE_STREAM_ID)).thenReturn(ticket);

    Set<Object> flagged = agentExternalCheck.check(symMessage);
    assertEquals(2, flagged.size());
  }

  @Test
  public void testCheckHasNoOpenedTicket() {
    SymMessage symMessage = mockSymMessage();
    symMessage.setStreamId(MOCK_SERVICE_STREAM_ID);

    Ticket ticket = new Ticket();
    ticket.setState(UNSERVICED.getState());

    when(ticketClient.getTicketByServiceStreamId(JWT, MOCK_SERVICE_STREAM_ID)).thenReturn(ticket);

    Set<Object> flagged = agentExternalCheck.check(symMessage);
    assertNull(flagged);
  }

  private AttachmentMakerCheckerMessage mockAttachmentMakerCheckerMessage() {
    AttachmentMakerCheckerMessage attachmentMakerCheckerMessage =
        new AttachmentMakerCheckerMessage();
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
    makerchecker.setAttachmentName(MOCK_ATTACHMENT_NAME);

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