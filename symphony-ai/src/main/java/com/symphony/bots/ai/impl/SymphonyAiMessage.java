package com.symphony.bots.ai.impl;

import com.symphony.bots.ai.model.AiMessage;

import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick.tarsillo on 10/2/17.
 * A Symphony version of an Ai message.
 * Allows the Ai to send and receive entity data and attachments.
 */
public class SymphonyAiMessage extends AiMessage {
  private String entityData;
  private List<SymAttachmentInfo> attachments;
  private String fromUserId;

  public SymphonyAiMessage(String message) {
    super(message);
    this.entityData = "";
    this.attachments = new ArrayList<>();
  }

  public SymphonyAiMessage(SymMessage symMessage) {
    super(symMessage.getMessageText());
    this.entityData = symMessage.getEntityData();
    this.attachments = symMessage.getAttachments();
  }

  public String getEntityData() {
    return entityData;
  }

  public void setEntityData(String entityData) {
    this.entityData = entityData;
  }

  public List<SymAttachmentInfo> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<SymAttachmentInfo> attachments) {
    this.attachments = attachments;
  }

  public String getFromUserId() {
    return fromUserId;
  }

  public void setFromUserId(String fromUserId) {
    this.fromUserId = fromUserId;
  }
}
