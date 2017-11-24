package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model;

/**
 * Created by nick.tarsillo on 11/24/17.
 */
public class AttachmentMakerCheckerMessage extends MakerCheckerMessage {
  private String attachmentId;

  public String getAttachmentId() {
    return attachmentId;
  }

  public void setAttachmentId(String attachmentId) {
    this.attachmentId = attachmentId;
  }
}
