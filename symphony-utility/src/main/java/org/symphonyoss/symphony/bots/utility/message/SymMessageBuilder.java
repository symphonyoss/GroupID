package org.symphonyoss.symphony.bots.utility.message;

import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsanchez on 01/12/17.
 */
public class SymMessageBuilder {

  private String message;

  private String entityData;

  private List<SymAttachmentInfo> attachments = new ArrayList<>();

  private SymMessageBuilder(String message) {
    this.message = message;
  }

  public static SymMessageBuilder message(String message) {
    SymMessageBuilder builder = new SymMessageBuilder(message);
    return builder;
  }

  public SymMessageBuilder entityData(String entityData) {
    this.entityData = entityData;
    return this;
  }

  public SymMessageBuilder addAttachment(SymAttachmentInfo attachmentInfo) {
    this.attachments.add(attachmentInfo);
    return this;
  }

  public SymMessage build() {
    SymMessage symMessage = new SymMessage();
    symMessage.setMessage(message);
    symMessage.setEntityData(entityData);
    symMessage.setAttachments(attachments);

    return symMessage;
  }

}
