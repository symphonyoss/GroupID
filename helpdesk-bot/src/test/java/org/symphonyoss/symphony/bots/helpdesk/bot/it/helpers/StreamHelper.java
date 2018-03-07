package org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers;

import org.springframework.stereotype.Component;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.client.services.RoomService;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.TestContext;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.clients.StreamsClient;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.MemberInfo;
import org.symphonyoss.symphony.pod.model.Stream;

import java.util.List;
import java.util.Optional;

/**
 * Helper class to deal with stream stuff.
 *
 * Created by rsanchez on 01/03/18.
 */
@Component
public class StreamHelper {

  private final TestContext context = TestContext.getInstance();

  private final UserHelper userHelper;

  private final TicketHelper ticketHelper;

  private final SymphonyClient symphonyClient;

  public StreamHelper(UserHelper userHelper, TicketHelper ticketHelper,
      SymphonyClient symphonyClient) {
    this.userHelper = userHelper;
    this.ticketHelper = ticketHelper;
    this.symphonyClient = symphonyClient;
  }

  /**
   * Returns the IM between the client and the bot.
   *
   * @param username Client username
   * @return IM between the client and the bot.
   * @throws StreamsException Failure to retrieve the stream
   */
  public SymStream getClientStream(String username) throws StreamsException {
    SymUser botUser = symphonyClient.getLocalUser();
    SymphonyClient userClient = userHelper.getUserContext(username);
    return userClient.getStreamsClient().getStream(botUser);
  }

  /**
   * Returns the ticket room.
   *
   * @param agentId Agent id
   * @return Ticket room
   */
  public Optional<SymStream> getTicketStream(Long agentId) {
    Optional<Ticket> claimedTicket = ticketHelper.getClaimedTicket(agentId);

    if (claimedTicket.isPresent()) {
      String serviceStreamId = claimedTicket.get().getServiceStreamId();

      SymStream stream = new SymStream();
      stream.setStreamId(serviceStreamId);

      return Optional.of(stream);
    }

    return Optional.empty();
  }

  /**
   * Returns the ticket room.
   *
   * @return Ticket room
   */
  public Optional<SymStream> getTicketStream() {
    Optional<Ticket> claimedTicket = ticketHelper.getClaimedTicket();

    if (claimedTicket.isPresent()) {
      String serviceStreamId = claimedTicket.get().getServiceStreamId();

      SymStream stream = new SymStream();
      stream.setStreamId(serviceStreamId);

      return Optional.of(stream);
    }

    return Optional.empty();
  }

  /**
   * Returns the ticket room.
   *
   * @param ticket The claimed ticket
   * @return Ticket room
   */
  public SymStream getTicketStream(Ticket ticket) {
    String serviceStreamId = ticket.getServiceStreamId();
    SymStream stream = new SymStream();
    stream.setStreamId(serviceStreamId);

    return stream;
  }

  /**
   * Returns the stream's membership list.
   *
   * @param symStream SymStream
   * @return List of MemberInfo of specified stream
   */
  public List<MemberInfo> getStreamMembershipList(SymStream symStream) throws SymException {
    return symphonyClient.getRoomMembershipClient().getRoomMembership(symStream.getStreamId());
  }

  /**
   * Returns the queue room.
   *
   * @return Queue room
   */
  public Stream getQueueRoom() {
    return context.getQueueRoom().getStream();
  }

  /**
   * Remove a membership from room
   *
   * @param streamId Room ID
   * @param userId User ID
   */
  public void removeMembershipFromRoom(String streamId, Long userId) throws SymException {
    symphonyClient.getRoomMembershipClient().removeMemberFromRoom(streamId, userId);
  }

}
