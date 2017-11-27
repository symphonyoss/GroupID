package org.symphonyoss.symphony.bots.helpdesk.makerchecker.config;

/**
 * Created by nick.tarsillo on 11/24/17.
 */
public class MakerCheckerServiceConfig {
  private String groupId;
  private String attachmentMessageTemplate;
  private String attachmentEntityTemplate;

  public String getAttachmentMessageTemplate() {
    return attachmentMessageTemplate;
  }

  public void setAttachmentMessageTemplate(String attachmentMessageTemplate) {
    this.attachmentMessageTemplate = attachmentMessageTemplate;
  }

  public String getAttachmentEntityTemplate() {
    return attachmentEntityTemplate;
  }

  public void setAttachmentEntityTemplate(String attachmentEntityTemplate) {
    this.attachmentEntityTemplate = attachmentEntityTemplate;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }
}
