package org.symphonyoss.symphony.bots.utility.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.symphony.clients.StreamsClient;
import org.symphonyoss.symphony.clients.UsersClient;
import org.symphonyoss.symphony.clients.model.SymUser;

/**
 * Created by nick.tarsillo on 11/16/17.
 */
public class SymphonyUtilClient {
  private static final Logger LOG = LoggerFactory.getLogger(SymphonyUtilClient.class);

  private SymphonyClient symphonyClient;

  public SymphonyUtilClient(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
  }

  public String getUserDisplayName(Long userId) {
    try {
      SymUser user = symphonyClient.getUsersClient().getUserFromId(userId);
      return user.getDisplayName();
    } catch (UsersClientException e) {
      LOG.warn("Could not get user display name for: " + userId, e);
      return null;
    }
  }

  public String getStreamIdByUserId(Long userId) {
    StreamsClient streamsClient = symphonyClient.getStreamsClient();
    UsersClient usersClient = symphonyClient.getUsersClient();
    try {
      return streamsClient.getStream(usersClient.getUserFromId(userId)).getStreamId();
    } catch (StreamsException | UsersClientException e) {
      LOG.warn("Could not get stream id by user id for: " + userId, e);
      return null;
    }
  }
}
