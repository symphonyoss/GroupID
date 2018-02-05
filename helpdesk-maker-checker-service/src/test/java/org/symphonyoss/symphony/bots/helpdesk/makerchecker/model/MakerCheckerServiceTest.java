package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.MakerCheckerService;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.check.AgentExternalCheck;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client.MakercheckerClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Makerchecker;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.validation.SymphonyValidationUtil;
import org.symphonyoss.symphony.clients.MessagesClient;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class MakerCheckerServiceTest {

  private static final String GROUP_ID = "test";

  private static final String ATTACHMENT_ID =
      "internal_9826885173254%2FxtpDCplNtIJluaYgvQkfGg%3D%3D";

  private static final String STREAM_ID = "pT4nMlbsOgqfgPuQTQO9yn___p9c7xwsdA";

  private static final Long TIMESTAMP = 545465456465l;

  private static final String PROXY_TO_STREAM_ID = "pT4nMlbsOgqfgPuQTQO9yn___p9c7xwsdA";

  private static final String TYPE = "ATTACHMENT";

  private static final String MESSAGE_ID = "1322154564545";

  private static final String TICKET_SERVICE_URL = "https://localhost/helpdesk-service";

  private static final String BOT_URL = "https://localhost/helpdesk-bot";

  private static final String SERVICE_URL = "https://localhost/helpdesk-service";

  private static final String MOCK_SERVICE_STREAM_ID = "RU0dsa0XfcE5SNoJKsXSKX___p-qTQ3XdA";

  private static final String MOCK_MAKERCHECKER_ID = "XJW9H3XPCU";

  private static final Long MOCK_MAKER_ID = 10651518946916l;

  private MakerCheckerService makerCheckerService;

  @Mock
  private SymphonyClient symphonyClient;

  @Mock
  private MessagesClient messagesClient;

  @Mock
  private TicketClient ticketClient;

  @Mock
  private SymphonyValidationUtil symphonyValidationUtil;

  @Mock
  private AgentExternalCheck agentExternalCheck;

  @Before
  public void init() {
    doReturn(messagesClient).when(symphonyClient).getMessagesClient();

    makerCheckerService = new MakerCheckerService(mockMakercheckerClient(), symphonyClient);
  }

  @Test
  public void testGetApprovedMessage() {
    agentExternalCheck =
        new AgentExternalCheck(BOT_URL, SERVICE_URL, GROUP_ID, ticketClient, symphonyClient, symphonyValidationUtil);

    makerCheckerService.addCheck(agentExternalCheck);

    SymStream symStream = new SymStream();
    symStream.setStreamId(STREAM_ID);
    try {
      doReturn(mockSymMessageList()).when(messagesClient)
          .getMessagesFromStream(any(SymStream.class), eq(TIMESTAMP - 1), eq(0), eq(10));
    } catch (MessagesException e) {
      fail();
    }

    Set<SymMessage> symMessages =
        makerCheckerService.getApprovedMakercheckerMessage(mockAttachmentMakerCheckerMessage());

    for (SymMessage symMessage : symMessages) {
      assertNotNull(symMessages);
      assertEquals(STREAM_ID, symMessage.getStreamId());
      assertEquals(TIMESTAMP.toString(), symMessage.getTimestamp());
    }
  }

  @Test
  public void testSendActionMakerCheckerMessage() throws MessagesException {
    makerCheckerService.addCheck(agentExternalCheck);

    Makerchecker makerchecker = mockMakerchecker();
    doReturn(mockActionMessage()).when(agentExternalCheck).getActionMessage(makerchecker, MakercheckerClient.AttachmentStateType.APPROVED);

    makerCheckerService.sendActionMakerCheckerMessage(makerchecker, MakercheckerClient.AttachmentStateType.APPROVED);

    verify(messagesClient, times(1)).sendMessage(any(SymStream.class), any(SymMessage.class));
  }

  private AttachmentMakerCheckerMessage mockAttachmentMakerCheckerMessage() {
    AttachmentMakerCheckerMessage attachmentMakerCheckerMessage =
        new AttachmentMakerCheckerMessage();
    attachmentMakerCheckerMessage.setGroupId(GROUP_ID);
    attachmentMakerCheckerMessage.setAttachmentId(ATTACHMENT_ID);
    attachmentMakerCheckerMessage.setMessageId(MESSAGE_ID);
    attachmentMakerCheckerMessage.setStreamId(STREAM_ID);
    attachmentMakerCheckerMessage.setTimeStamp(TIMESTAMP);
    attachmentMakerCheckerMessage.setType(TYPE);
    List<String> proxyToStreamIds = new ArrayList<>();
    proxyToStreamIds.add(PROXY_TO_STREAM_ID);
    attachmentMakerCheckerMessage.setProxyToStreamIds(proxyToStreamIds);

    return attachmentMakerCheckerMessage;
  }

  private MakercheckerClient mockMakercheckerClient() {
    return new MakercheckerClient(GROUP_ID, TICKET_SERVICE_URL);
  }

  private List<SymMessage> mockSymMessageList() {
    List<SymMessage> symMessageList = new ArrayList<>();

    SymStream symStream = new SymStream();
    symStream.setStreamId(STREAM_ID);

    SymMessage symMessage = new SymMessage();
    symMessage.setId(MESSAGE_ID);
    symMessage.setStreamId(STREAM_ID);
    symMessage.setTimestamp(TIMESTAMP.toString());
    symMessage.setStream(symStream);

    symMessageList.add(symMessage);

    return symMessageList;
  }

  private SymMessage mockActionMessage() {
    SymStream symStream = new SymStream();
    symStream.setStreamId(STREAM_ID);

    SymMessage symMessage = new SymMessage();
    symMessage.setId(MESSAGE_ID);
    symMessage.setStreamId(STREAM_ID);
    symMessage.setTimestamp(TIMESTAMP.toString());
    symMessage.setStream(symStream);

    return symMessage;
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

}