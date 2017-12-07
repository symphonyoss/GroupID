package org.symphonyoss.symphony.bots.utility.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;

import java.util.List;

/**
 * Created by nick.tarsillo on 12/6/17.
 *
 * Helper class for retrieving Symphony data.
 */
public class SymphonyUtilClient {
  private static final Logger LOG = LoggerFactory.getLogger(SymphonyUtilClient.class);

  private SymphonyClient symphonyClient;

  public SymphonyUtilClient(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
  }

  /**
   * Get a message in a stream, by it's timestamp.
   * @param stream the stream of the message
   * @param timestamp the timestamp of the message
   * @return the sym message
   */
  public SymMessage getByTimestamp(String stream, Long timestamp) {
    try {
      SymStream symStream = new SymStream();
      symStream.setStreamId(stream);
      List<SymMessage> symMessageList =
          symphonyClient.getMessagesClient().getMessagesFromStream(symStream, timestamp, 0, 10);

      for(SymMessage symMessage: symMessageList) {
        if(symMessage.getTimestamp().equals(timestamp.toString())) {
          return symMessage;
        }
      }
    } catch (MessagesException e) {
      LOG.error("Get by timestamp failed: ", e);
    }

    return null;
  }
}
