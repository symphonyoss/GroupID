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

/**
 * Created by nick.tarsillo on 11/24/17.
 */
@Service
public class AutoConnectionAcceptListener implements ConnectionsEventListener {

  private static final Logger LOG = LoggerFactory.getLogger(AutoConnectionAcceptListener.class);

  private final SymphonyClient symphonyClient;

  public AutoConnectionAcceptListener(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
  }

  @Override
  public void onSymConnectionRequested(SymConnectionRequested symConnectionRequested) {
    ConnectionsClient connectionsClient = symphonyClient.getConnectionsClient();
    try {
      List<SymUserConnection> connectionList = connectionsClient.getIncomingRequests();
      for (SymUserConnection userConnection : connectionList) {
        connectionsClient.acceptConnectionRequest(userConnection);
      }
    } catch (ConnectionsException e) {
      LOG.error("Get pending requests failed: ", e);
    }
  }

  @Override
  public void onSymConnectionAccepted(SymConnectionAccepted symConnectionAccepted) {

  }
}
