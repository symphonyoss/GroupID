package org.symphonyoss.symphony.bots.helpdesk.bot.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.events.SymConnectionAccepted;
import org.symphonyoss.client.events.SymConnectionRequested;
import org.symphonyoss.client.exceptions.ConnectionsException;
import org.symphonyoss.client.services.ConnectionsEventListener;
import org.symphonyoss.symphony.clients.ConnectionsClient;
import org.symphonyoss.symphony.clients.model.SymUserConnection;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

/**
 * Listener to accept incoming requests from external users.
 *
 * Created by nick.tarsillo on 11/24/17.
 */
@Service
public class AutoConnectionAcceptListener implements ConnectionsEventListener {

  private static final Logger LOG = LoggerFactory.getLogger(AutoConnectionAcceptListener.class);

  private static final Integer DELAY_RETRIES = 5;

  private final SymphonyClient symphonyClient;

  private ScheduledExecutorService executorService;

  public AutoConnectionAcceptListener(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
    this.executorService = Executors.newSingleThreadScheduledExecutor();
  }

  @Override
  public void onSymConnectionRequested(SymConnectionRequested symConnectionRequested) {
    acceptAllIncomingRequests();
  }

  /**
   * Accept incoming requests from external users.
   */
  public void acceptAllIncomingRequests() {
    ConnectionsClient connectionsClient = symphonyClient.getConnectionsClient();

    try {
      List<SymUserConnection> connectionList = connectionsClient.getIncomingRequests();
      for (SymUserConnection userConnection : connectionList) {
        connectionsClient.acceptConnectionRequest(userConnection);
      }
    } catch (ConnectionsException e) {
      LOG.error("Accept pending requests failed: ", e);
      executorService.schedule(() -> acceptAllIncomingRequests(), DELAY_RETRIES, TimeUnit.SECONDS);
    }
  }

  @Override
  public void onSymConnectionAccepted(SymConnectionAccepted symConnectionAccepted) {}
}
