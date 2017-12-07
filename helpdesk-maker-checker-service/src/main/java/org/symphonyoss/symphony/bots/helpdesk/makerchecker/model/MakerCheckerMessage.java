package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model;

import java.util.List;

/**
 * Created by nick.tarsillo on 10/26/17.
 */
public class MakerCheckerMessage {
  private String type;
  private String streamId;
  private String messageId;
  private String groupId;
  private Long timeStamp;
  private List<String> proxyToStreamIds;

  public String getStreamId() {
    return streamId;
  }

  public void setStreamId(String streamId) {
    this.streamId = streamId;
  }

  public Long getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(Long timeStamp) {
    this.timeStamp = timeStamp;
  }

  public String getMessageId() {
    return messageId;
  }

  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }

  public List<String> getProxyToStreamIds() {
    return proxyToStreamIds;
  }

  public void setProxyToStreamIds(List<String> proxyToStreamIds) {
    this.proxyToStreamIds = proxyToStreamIds;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
