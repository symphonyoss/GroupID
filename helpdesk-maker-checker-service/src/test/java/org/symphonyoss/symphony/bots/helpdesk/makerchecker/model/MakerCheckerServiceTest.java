package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;

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
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
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

  private MakerCheckerService makerCheckerService;

  @Mock
  private SymphonyClient symphonyClient;

  @Mock
  private MessagesClient messagesClient;

  @Mock
  private TicketClient ticketClient;

  @Before
  public void init() {
    doReturn(messagesClient).when(symphonyClient).getMessagesClient();

    makerCheckerService = new MakerCheckerService(mockMakercheckerClient(), symphonyClient);

    AgentExternalCheck agentExternalCheck =
        new AgentExternalCheck(BOT_URL, SERVICE_URL, GROUP_ID, ticketClient, symphonyClient);

    makerCheckerService.addCheck(agentExternalCheck);
  }

  @Test
  public void testGetApprovedMessage() {
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

}
