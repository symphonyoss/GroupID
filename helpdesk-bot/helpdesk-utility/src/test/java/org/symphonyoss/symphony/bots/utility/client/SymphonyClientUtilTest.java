package org.symphonyoss.symphony.bots.utility.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.AttachmentsException;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.symphony.clients.AttachmentsClient;
import org.symphonyoss.symphony.clients.MessagesClient;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;

@RunWith(MockitoJUnitRunner.class)
public class SymphonyClientUtilTest {

  public static final String MESSAGE_ID = "1";
  public static final String ANY_STREAM = "anyStream";
  @Mock
  private SymphonyClient symphonyClient;

  @Mock
  private MessagesClient messagesClient;

  @Mock
  private AttachmentsClient attachmentsClient;

  private SymphonyClientUtil symphonyClientUtil;

  @Before
  public void init() {
    symphonyClientUtil = new SymphonyClientUtil(symphonyClient);
    doReturn(messagesClient).when(symphonyClient).getMessagesClient();
  }

  @Test
  public void testGetFileAttachment()
      throws InternalServerErrorException, BadRequestException, AttachmentsException, IOException {
    String tmpDir = System.getProperty("java.io.tmpdir");
    byte[] fileBytes = "filebytes".getBytes();
    SymAttachmentInfo symAttachmentInfo = new SymAttachmentInfo();
    SymMessage symMessage = new SymMessage();
    File testFile = new File(tmpDir + File.separator + symAttachmentInfo.getId() + File.separator
        + symAttachmentInfo.getName());

    doReturn(attachmentsClient).when(symphonyClient).getAttachmentsClient();
    doReturn(fileBytes).when(attachmentsClient)
        .getAttachmentData(any(SymAttachmentInfo.class), any(SymMessage.class));

    File attachment =
        symphonyClientUtil.getFileAttachment(symAttachmentInfo, symMessage);

    verify(symphonyClient, times(1)).getAttachmentsClient();
    verify(attachmentsClient, times(1)).getAttachmentData(any(SymAttachmentInfo.class),
        any(SymMessage.class));

    assertTrue(attachment.exists());
    assertTrue(attachment.isFile());
    assertTrue(Arrays.equals(fileBytes, FileUtils.readFileToByteArray(attachment)));
    assertTrue(testFile.compareTo(attachment) == 0);
  }

  @Test
  public void testGetSymMessages() throws MessagesException {
    symphonyClientUtil.getSymMessages(new SymStream(), 0L, -3);

    verify(symphonyClient, times(1)).getMessagesClient();
    verify(messagesClient, times(1)).getMessagesFromStream(any(SymStream.class), anyLong(),
        anyInt(), anyInt());
  }

  @Test
  public void testGetSymMessageByStreamAndId() throws MessagesException {
    doReturn(mockSymMessageList(4)).when(messagesClient)
        .getMessagesFromStream(any(SymStream.class), anyLong(), anyInt(), anyInt());

    Optional<SymMessage> message =
        symphonyClientUtil.getSymMessageByStreamAndId(ANY_STREAM, 0L, MESSAGE_ID);

    assertTrue(message.isPresent());
    assertEquals(MESSAGE_ID, message.get().getId());

    verify(symphonyClient, times(1)).getMessagesClient();
    verify(messagesClient, times(1)).getMessagesFromStream(any(SymStream.class), anyLong(),
        anyInt(), anyInt());
  }

  private List<SymMessage> mockSymMessageList(int count) {
    List<SymMessage> messageList = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      SymMessage message = new SymMessage();
      message.setId(Integer.toString(i));
      messageList.add(message);
    }
    return messageList;
  }
}