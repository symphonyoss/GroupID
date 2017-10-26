package org.symphonyoss.symphony.bots.ai.impl;

import org.symphonyoss.symphony.bots.ai.model.AiMessage;

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
  protected String entityData;
  protected String fromUserId;
  protected String messageId;
  protected String streamId;
  protected String timestamp;
  protected List<SymAttachmentInfo> attachments;

  public SymphonyAiMessage(String message) {
    super(message);
    this.entityData = "";
    this.attachments = new ArrayList<>();
  }

  public SymphonyAiMessage(SymMessage symMessage) {
    super(symMessage.getMessageText());
    this.entityData = symMessage.getEntityData();
    this.attachments = symMessage.getAttachments();
    this.messageId = symMessage.getId();
    this.timestamp = symMessage.getTimestamp();
    this.streamId = symMessage.getStreamId();
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

  public String getMessageId() {
    return messageId;
  }

  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }

  public String getStreamId() {
    return streamId;
  }

  public void setStreamId(String streamId) {
    this.streamId = streamId;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }
}
