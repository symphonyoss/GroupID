package org.symphonyoss.symphony.bots.helpdesk.service.messageproxy.model;

import org.symphonyoss.symphony.bots.ai.AiResponseIdentifier;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;

import java.util.Set;

/**
 * Created by nick.tarsillo on 10/26/17.
 */
public class ProxyAiMessage extends SymphonyAiMessage {
  protected Set<AiResponseIdentifier> proxyToStreamId;

  public ProxyAiMessage(SymphonyAiMessage message, Set<AiResponseIdentifier> proxyToStreamId) {
    super(message.getAiMessage());
    this.attachments = message.getAttachments();
    this.entityData = message.getEntityData();
    this.fromUserId = message.getFromUserId();
    this.entityData = message.getEntityData();
    this.messageId = message.getMessageId();
    this.timestamp = message.getTimestamp();
    this.proxyToStreamId = proxyToStreamId;
  }

  public Set<AiResponseIdentifier> getProxyToStreamId() {
    return proxyToStreamId;
  }

  public void setProxyToStreamId(
      Set<AiResponseIdentifier> proxyToStreamId) {
    this.proxyToStreamId = proxyToStreamId;
  }
}
