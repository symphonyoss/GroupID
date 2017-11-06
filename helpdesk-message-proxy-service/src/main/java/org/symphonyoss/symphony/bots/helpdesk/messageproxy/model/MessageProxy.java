package org.symphonyoss.symphony.bots.helpdesk.messageproxy.model;

import org.symphonyoss.symphony.bots.ai.conversation.ProxyConversation;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by nick.tarsillo on 10/19/17.
 */
public class MessageProxy {
  private Set<ProxyConversation> proxyConversations;

  public MessageProxy() {
    proxyConversations = new HashSet<>();
  }

  public void addProxyConversation(ProxyConversation proxyConversation) {
    proxyConversations.add(proxyConversation);
  }
}
