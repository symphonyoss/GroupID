package org.symphonyoss.symphony.bots.utility.message;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class SymMessageBuilderTest {

  private static final String MESSAGE = "MESSAGE";
  private static final String ATTACHMENT_ID = "ATTACHMENT_ID";
  private static final String NAME = "NAME";

  private final String ENTITY_DATA = "ENTITY_DATA";

  private List<SymAttachmentInfo> attachments = new ArrayList<>();


  private SymMessageBuilder symMessageBuilder;

  @Before
  public void setUp() throws Exception {
    symMessageBuilder = SymMessageBuilder.message(MESSAGE);
  }

  @Test
  public void message() {
    symMessageBuilder.message(MESSAGE);
  }

  @Test
  public void entityData() {
    SymMessageBuilder builder = symMessageBuilder.entityData(ENTITY_DATA);
    assertNotNull(builder);
  }

  @Test
  public void addAttachment() {
    SymMessageBuilder builder = symMessageBuilder.addAttachment(getAttachmentList().get(0));
    assertNotNull(builder);
  }

  @Test
  public void build() {
    symMessageBuilder.message(MESSAGE);
    symMessageBuilder.entityData(ENTITY_DATA);
    symMessageBuilder.addAttachment(getAttachmentList().get(0));

    SymMessage build = symMessageBuilder.build();
    assertNotNull(build);

  }

  private List<SymAttachmentInfo> getAttachmentList() {
    SymAttachmentInfo attachmentInfo = new SymAttachmentInfo();
    attachmentInfo.setId(ATTACHMENT_ID);
    attachmentInfo.setName(NAME);

    attachments.add(attachmentInfo);

    return attachments;

  }
}