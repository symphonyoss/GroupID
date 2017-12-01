package org.symphonyoss.symphony.bots.helpdesk.messageproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.services.MessageListener;
import org.symphonyoss.symphony.clients.model.SymMessage;

import javax.annotation.PostConstruct;

/**
 * Component responsible for listening the messages sent to bot.
 *
 * Created by rsanchez on 01/12/17.
 */
@Component
public class ChatListener implements MessageListener {

  private final SymphonyClient symphonyClient;

  private final ProxyService proxyService;

  public ChatListener(SymphonyClient symphonyClient, ProxyService proxyService) {
    this.symphonyClient = symphonyClient;
    this.proxyService = proxyService;
  }

  @PostConstruct
  public void init() {
    this.symphonyClient.getMessageService().addMessageListener(this);
  }

  @Override
  public void onMessage(SymMessage symMessage) {
    proxyService.messageReceived(symMessage);
  }

}
