package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.check;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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

  private AgentExternalCheck agentExternalCheck;

  @Mock
  private SymphonyClient symphonyClient;

  @Test
  public void testGetApprovedAttachment() {
    mockAgentExternalCheckUnresolved();
    AttachmentMakerCheckerMessage attachmentMakerCheckerMessage = mockAttachmentMakerCheckerMessage();
    SymMessage symMessage = mockSymMessage();


    Optional<SymAttachmentInfo> symAttachmentInfo = agentExternalCheck.getApprovedAttachment(attachmentMakerCheckerMessage, symMessage);

    assertEquals(ATTACHMENT_ID, symAttachmentInfo.get().getId());
    assertEquals(ATTACHMENT_NAME, symAttachmentInfo.get().getName());
  }

  @Test
  public void testGetApprovedAttachmentNull() {
    mockAgentExternalCheckUnresolved();
    AttachmentMakerCheckerMessage attachmentMakerCheckerMessage = mockAttachmentMakerCheckerMessage();
    SymMessage symMessage = new SymMessage();


    Optional<SymAttachmentInfo> symAttachmentInfo = agentExternalCheck.getApprovedAttachment(attachmentMakerCheckerMessage, symMessage);

    assertFalse(symAttachmentInfo.isPresent());
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
