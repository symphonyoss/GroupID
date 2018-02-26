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
import org.symphonyoss.symphony.bots.utility.message.SymMessageUtil;
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
public class SymMessageUtilTest {

  private static final String CHIME_MESSAGE =
      "<div data-format=\"PresentationML\"data-version=\"2.0\"><audio src=\""
          + "https://asset.symphony.com/symphony/audio/chime.mp3\" autoplay=\"true\"/></div>";

  private static final String TABLE_MESSAGE = "<div data-format=\"PresentationML\"data-version"
      + "=\"2.0\"><table><tr><td>text</td></tr></table></div>";

  @Before
  public void init() {
  }

  @Test
  public void testIsChime() {
    SymMessage testMessage = new SymMessage();

    testMessage.setMessage(CHIME_MESSAGE);

    assertTrue(SymMessageUtil.isChime(testMessage));
    assertTrue(!SymMessageUtil.hasAttachment(testMessage));
    assertTrue(!SymMessageUtil.hasTable(testMessage));
  }

  @Test
  public void testHasAttachment() throws MessagesException {
    SymMessage testMessage = new SymMessage();
    List<SymAttachmentInfo> attachments = new ArrayList<>();
    attachments.add(new SymAttachmentInfo());
    attachments.add(new SymAttachmentInfo());

    testMessage.setAttachments(attachments);

    assertTrue(SymMessageUtil.hasAttachment(testMessage));
    assertTrue(!SymMessageUtil.isChime(testMessage));
    assertTrue(!SymMessageUtil.hasTable(testMessage));
  }

  @Test
  public void testHasTable() throws MessagesException {
    SymMessage testMessage = new SymMessage();

    testMessage.setMessage(TABLE_MESSAGE);

    assertTrue(SymMessageUtil.hasTable(testMessage));
    assertTrue(!SymMessageUtil.isChime(testMessage));
    assertTrue(!SymMessageUtil.hasAttachment(testMessage));
  }
}