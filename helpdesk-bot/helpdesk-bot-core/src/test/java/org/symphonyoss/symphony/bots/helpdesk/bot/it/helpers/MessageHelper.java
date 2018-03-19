package org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers;

import org.springframework.stereotype.Component;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.exception.TicketRoomNotFoundException;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.Stream;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Helper class to deal with message stuff.
 *
 * Created by rsanchez on 01/03/18.
 */
@Component
public class MessageHelper {

  private final StreamHelper streamHelper;

  private final UserHelper userHelper;

  private final SymphonyClient symphonyClient;

  public MessageHelper(StreamHelper streamHelper, UserHelper userHelper,
      SymphonyClient symphonyClient) {
    this.streamHelper = streamHelper;
    this.userHelper = userHelper;
    this.symphonyClient = symphonyClient;
  }

  /**
   * Send the client message through the IM between client and bot.
   *
   * @param userRef Client reference
   * @param message Message to be sent out
   * @throws StreamsException Failure to retrieve the stream
   * @throws MessagesException Failure to send out the message
   */
  public void sendClientMessage(String userRef, SymMessage message)
      throws StreamsException, MessagesException {
    String username = userHelper.getUser(userRef).getUsername();

    SymStream stream = streamHelper.getClientStream(username);
    SymphonyClient userClient = userHelper.getUserContext(username);
    userClient.getMessageService().sendMessage(stream, message);
  }

  /**
   * Send the agent message through the ticket room.
   *
   * @param userRef Agent reference
   * @param message Message to be sent out
   * @throws MessagesException Failure to send out the message
   */
  public void sendAgentMessage(String userRef, SymMessage message)
      throws MessagesException {
    String username = userHelper.getUser(userRef.toUpperCase()).getUsername();

    SymphonyClient userAgent = userHelper.getUserContext(username);
    SymUser agentUser = userAgent.getLocalUser();

    Optional<SymStream> stream = streamHelper.getTicketStream();

    if (!stream.isPresent()) {
      throw new TicketRoomNotFoundException("Ticket room not found");
    }
    userAgent.getMessageService().sendMessage(stream.get(), message);
  }

  /**
   * Send the agent message through the ticket room.
   *
   * @param userRef Agent reference
   * @param message Message to be sent out
   * @throws MessagesException Failure to send out the message
   */
  public void sendAgentMessageToOther(String userRef, SymMessage message)
      throws MessagesException {
    String username = userHelper.getUser(userRef.toUpperCase()).getUsername();

    SymphonyClient userAgent = userHelper.getUserContext(username);
    SymUser agentUser = userAgent.getLocalUser();

    Optional<SymStream> stream = streamHelper.getFirstTicketStream(agentUser.getId());

    if (stream.isPresent()) {
      userAgent.getMessageService().sendMessage(stream.get(), message);
    } else {
      throw new TicketRoomNotFoundException("Ticket room not found");
    }
  }

  /**
   * Retrieve the latest client message.
   *
   * @param user Client user
   * @param initialTime Initial time to retrieve messages
   * @return Latest message
   * @throws StreamsException Failure to retrieve the stream
   * @throws MessagesException Failure to retrieve messages
   */
  public Optional<SymMessage> getLatestClientMessage(String user, Long initialTime)
      throws StreamsException, MessagesException {
    String username = userHelper.getUser(user).getUsername();

    SymStream stream = streamHelper.getClientStream(username);
    SymphonyClient userClient = userHelper.getUserContext(username);

    List<SymMessage> messagesFromStream =
        userClient.getMessagesClient().getMessagesFromStream(stream, initialTime, 0, 100);

    return messagesFromStream.stream()
        .sorted(Comparator.comparing(SymMessage::getTimestamp).reversed())
        .findFirst();
  }

  /**
   * Retrieve the latest agent message.
   *
   * @param SymUser Agent user
   * @param initialTime Initial time to retrieve messages
   * @return Latest message
   * @throws MessagesException Failure to retrieve messages
   */
  public Optional<SymMessage> getLatestTicketMessage(SymUser agent, Long initialTime)
      throws MessagesException {
    Optional<SymStream> stream = streamHelper.getTicketStream();

    if (!stream.isPresent()) {
      throw new TicketRoomNotFoundException("Ticket room not found");
    }

    SymphonyClient userAgent = userHelper.getUserContext(agent.getUsername());

    List<SymMessage> messagesFromStream =
        userAgent.getMessagesClient().getMessagesFromStream(stream.get(), initialTime, 0, 100);

    return messagesFromStream.stream()
        .sorted(Comparator.comparing(SymMessage::getTimestamp).reversed())
        .findFirst();
  }

  /**
   * Retrieve the latest message in the queue room.
   *
   * @param initialTime Initial time to retrieve messages
   * @return Latest message
   * @throws MessagesException Failure to retrieve messages
   */
  public Optional<SymMessage> getLatestQueueRoomMessage(Long initialTime) throws MessagesException {
    Stream queueRoom = streamHelper.getQueueRoom();

    List<SymMessage> messagesFromStream =
        symphonyClient.getMessagesClient().getMessagesFromStream(queueRoom, initialTime, 0, 100);

    return messagesFromStream.stream()
        .sorted(Comparator.comparing(SymMessage::getTimestamp).reversed())
        .findFirst();
  }

  /**
   * Retrieve all messages in the ticket room.
   *
   * @param initialTime Initial time to retrieve messages
   * @param streamId Ticket room id
   * @return Room messages
   * @throws MessagesException Failure to retrieve messages
   */
  public List<SymMessage> getTicketRoomMessages(Long initialTime, String streamId) throws MessagesException {
    Stream ticketRoom = new Stream();
    ticketRoom.setId(streamId);

    return symphonyClient.getMessagesClient().getMessagesFromStream(ticketRoom, initialTime, 0, 100);
  }
}
