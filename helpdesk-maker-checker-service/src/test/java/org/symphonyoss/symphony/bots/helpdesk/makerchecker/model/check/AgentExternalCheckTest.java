package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.check;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.AttachmentMakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class AgentExternalCheckTest {

  private static final String BOT_HOST = "192.168.0.2";

  private static final String SERVICE_HOST = "192.168.0.3";

  private static final String GROUP_ID = "test";

  private static final String TICKET_SERVICE_URL = "https://localhost/helpdesk-service";

  private static final String ATTACHMENT_ID =
      "internal_9826885173254%2FxtpDCplNtIJluaYgvQkfGg%3D%3D";

  private static final String INVALID_ATTACHMENT_ID =
      "internal_8826885173254%2FxtpDCplNtIJluaYgvQkfGg%3D%3D";

  private static final String INVALID_ATTACHMENT_NAME = "INVALID_ATTACHMENT";

  private static final String ATTACHMENT_NAME = "ATTACHMENT_OK";

  private AgentExternalCheck agentExternalCheck;

  @Mock
  private SymphonyClient symphonyClient;

  @Test
  public void testGetApprovedAttachment() {
    mockAgentExternalCheckUnresolved();
    AttachmentMakerCheckerMessage attachmentMakerCheckerMessage = mockAttachmentMakerCheckerMessage();
    SymMessage symMessage = mockSymMessage();


    SymAttachmentInfo symAttachmentInfo = agentExternalCheck.getApprovedAttachment(attachmentMakerCheckerMessage, symMessage);

    assertEquals(ATTACHMENT_ID, symAttachmentInfo.getId());
    assertEquals(ATTACHMENT_NAME, symAttachmentInfo.getName());
  }

  @Test
  public void testGetApprovedAttachmentNull() {
    mockAgentExternalCheckUnresolved();
    AttachmentMakerCheckerMessage attachmentMakerCheckerMessage = mockAttachmentMakerCheckerMessage();
    SymMessage symMessage = new SymMessage();


    SymAttachmentInfo symAttachmentInfo = agentExternalCheck.getApprovedAttachment(attachmentMakerCheckerMessage, symMessage);

    assertNull(symAttachmentInfo.getId());
  }

  private void mockAgentExternalCheckUnresolved() {
    TicketClient ticketClient = new TicketClient(GROUP_ID, TICKET_SERVICE_URL);

    agentExternalCheck = new AgentExternalCheck(BOT_HOST, SERVICE_HOST, GROUP_ID, ticketClient, symphonyClient);
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

}
