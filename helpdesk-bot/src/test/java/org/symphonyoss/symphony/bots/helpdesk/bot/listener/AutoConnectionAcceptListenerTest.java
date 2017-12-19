package org.symphonyoss.symphony.bots.helpdesk.bot.listener;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.events.SymConnectionRequested;
import org.symphonyoss.client.exceptions.ConnectionsException;
import org.symphonyoss.client.services.MessageService;
import org.symphonyoss.symphony.bots.helpdesk.bot.listener.AutoConnectionAcceptListener;
import org.symphonyoss.symphony.clients.ConnectionsClient;
import org.symphonyoss.symphony.clients.model.SymUserConnection;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by rsanchez on 14/12/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class AutoConnectionAcceptListenerTest {

  @Mock
  private SymphonyClient symphonyClient;

  @Mock
  private MessageService messageService;

  @Mock
  private ConnectionsClient connectionsClient;

  private AutoConnectionAcceptListener listener;

  @Before
  public void init() {
    doReturn(messageService).when(symphonyClient).getMessageService();
    doReturn(connectionsClient).when(symphonyClient).getConnectionsClient();

    this.listener = new AutoConnectionAcceptListener(symphonyClient);
  }

  @Test
  public void testFailRetrievePendingConnections() throws ConnectionsException {
    doThrow(ConnectionsException.class).when(connectionsClient).getPendingRequests();
    listener.onSymConnectionRequested(new SymConnectionRequested());
    verify(connectionsClient, never()).acceptConnectionRequest(any(SymUserConnection.class));
  }

  @Test
  public void testNoPendingConnections() throws ConnectionsException {
    doReturn(Collections.EMPTY_LIST).when(connectionsClient).getPendingRequests();
    listener.onSymConnectionRequested(new SymConnectionRequested());
    verify(connectionsClient, never()).acceptConnectionRequest(any(SymUserConnection.class));
  }

  @Test
  public void testPendingConnections() throws ConnectionsException {
    SymUserConnection userConnection = new SymUserConnection();
    doReturn(Arrays.asList(userConnection)).when(connectionsClient).getPendingRequests();

    listener.onSymConnectionRequested(new SymConnectionRequested());
    verify(connectionsClient, times(1)).acceptConnectionRequest(userConnection);
  }

}
