package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model;

import java.util.List;

/**
 * Created by nick.tarsillo on 10/26/17.
 */
public class MakerCheckerMessage {
  private String streamId;
  private List<String> proxyToStreamIds;
  private String timeStamp;
  private String messageId;

  public MakerCheckerMessage(String streamId, List<String> proxyToStreamIds, String timeStamp, String messageId) {
    this.streamId = streamId;
    this.proxyToStreamIds = proxyToStreamIds;
    this.timeStamp = timeStamp;
    this.messageId = messageId;
  }

  public String getStreamId() {
    return streamId;
  }

  public void setStreamId(String streamId) {
    this.streamId = streamId;
  }

  public String getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(String timeStamp) {
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
}
