package org.symphonyoss.symphony.bots.utility.message;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class SymMessageUtilTest {

  private static final String CHIME_MESSAGE =
      "<div data-format=\"PresentationML\"data-version=\"2.0\"><audio src=\""
          + "https://asset.symphony.com/symphony/audio/chime.mp3\" autoplay=\"true\"/></div>";

  private static final String TABLE_MESSAGE = "<div data-format=\"PresentationML\"data-version"
      + "=\"2.0\"><table><tr><td>text</td></tr></table></div>";

  private static final String EMOJI_MESSAGE =
      "<div data-format=\"PresentationML\" data-version=\"2.0\">Emoji: :joy:  No emoji: just "
          + "colon but no emoji : : : yayy: :891 lastly, new emoji: :end:</div>";

  private static final String EXPECTED_EMOJI_MESSAGE =
      "\nEmoji: <emoji shortcode=\"joy\" /> No emoji: just colon but no emoji : : : yayy: :891 "
          + "lastly, new emoji: <emoji shortcode=\"end\" />";

  @Before
  public void init() {
  }

  @Test
  public void testEmojis() {
    SymMessage testMessage = new SymMessage();

    testMessage.setMessage(EMOJI_MESSAGE);

    assertTrue(SymMessageUtil.parseMessage(testMessage).equals(EXPECTED_EMOJI_MESSAGE));
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
  public void testHasAttachment() {
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
  public void testHasTable() {
    SymMessage testMessage = new SymMessage();

    testMessage.setMessage(TABLE_MESSAGE);

    assertTrue(SymMessageUtil.hasTable(testMessage));
    assertTrue(!SymMessageUtil.isChime(testMessage));
    assertTrue(!SymMessageUtil.hasAttachment(testMessage));
  }
}