package org.symphonyoss.symphony.bots.utility.client;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.symphony.clients.MessagesClient;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;

import java.util.List;
import java.util.Optional;

/**
 * Helper class for retrieving Symphony data.
 * <p>
 * Created by nick.tarsillo on 12/6/17.
 */
public class SymphonyClientUtil {

  private static final int DEFAULT_MAX_MESSAGES = 10;

  private SymphonyClient symphonyClient;

  public SymphonyClientUtil(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
  }

  /**
   * Get a list of messages from a stream
   *
   * @param stream Stream to retrieve messages from
   * @param since Date (long) from point in time
   * @param maxMessages Maximum number of messages to retrieve from the specified time (since)
   * @return List of messages matching the given parameters
   * @throws MessagesException Failure to retrieve messages
   */
  public List<SymMessage> getSymMessages(SymStream stream, Long since, int maxMessages)
      throws MessagesException {
    MessagesClient messagesClient = symphonyClient.getMessagesClient();
    return messagesClient.getMessagesFromStream(stream, since, 0, maxMessages);
  }

  /**
   * Get the message from the given stream according to the conversation ID
   *
   * @param streamId Stream to retrieve messages from
   * @param since Date (long) from point in time
   * @param id Conversation ID
   * @return Message according to the conversation ID
   * @throws MessagesException Failure to retrieve messages
   */
  public Optional<SymMessage> getSymMessageByStreamAndId(String streamId, Long since, String id)
      throws MessagesException {
    SymStream stream = new SymStream();
    stream.setStreamId(streamId);
    List<SymMessage> symMessageList = getSymMessages(stream, since, DEFAULT_MAX_MESSAGES);

    Optional<SymMessage> symMessage = symMessageList.stream()
        .filter(message -> message.getId().equals(id))
        .findFirst();

    return symMessage;
  }
}
