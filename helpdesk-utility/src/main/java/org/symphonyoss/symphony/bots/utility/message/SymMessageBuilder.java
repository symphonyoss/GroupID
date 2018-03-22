package org.symphonyoss.symphony.bots.utility.message;

import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder class to create Symphony message object.
 * <p>
 * Created by rsanchez on 12/01/17.
 */
public class SymMessageBuilder {

  private String message;

  private String entityData;

  private List<SymAttachmentInfo> attachments = new ArrayList<>();

  private SymMessageBuilder(String message) {
    this.message = message;
  }

  /**
   * Define message content. It should follow the MessageML v2 spec.
   * @param message Message content
   * @return Builder class
   */
  public static SymMessageBuilder message(String message) {
    SymMessageBuilder builder = new SymMessageBuilder(message);
    return builder;
  }

  /**
   * Define entity data. It should be a serialized JSON entity
   * @param entityData Entity JSON
   * @return Builder class
   */
  public SymMessageBuilder entityData(String entityData) {
    this.entityData = entityData;
    return this;
  }

  /**
   * Add new attachment.
   * @param attachmentInfo Attachment info
   * @return Builder class
   */
  public SymMessageBuilder addAttachment(SymAttachmentInfo attachmentInfo) {
    this.attachments.add(attachmentInfo);
    return this;
  }

  /**
   * Builds new Symphony message object.
   * @return Symphony Message
   */
  public SymMessage build() {
    SymMessage symMessage = new SymMessage();
    symMessage.setMessage(message);
    symMessage.setEntityData(entityData);
    symMessage.setAttachments(attachments);

    return symMessage;
  }

}
