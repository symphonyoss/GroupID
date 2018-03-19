package org.symphonyoss.symphony.bots.helpdesk.messageproxy.model;

import org.symphonyoss.symphony.bots.ai.helpdesk.conversation.ProxyConversation;
import org.symphonyoss.symphony.bots.ai.helpdesk.conversation.ProxyIdleTimer;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by nick.tarsillo on 10/19/17.
 */
public class MessageProxy {
  private Set<ProxyConversation> proxyConversations;
  private ProxyIdleTimer agentProxyTimer;

  public MessageProxy() {
    proxyConversations = new HashSet<>();
  }

  public void addProxyConversation(ProxyConversation proxyConversation) {
    proxyConversations.add(proxyConversation);
  }

  public ProxyIdleTimer getAgentProxyTimer() {
    return agentProxyTimer;
  }

  public void setAgentProxyTimer(
      ProxyIdleTimer agentProxyTimer) {
    this.agentProxyTimer = agentProxyTimer;
  }
}
