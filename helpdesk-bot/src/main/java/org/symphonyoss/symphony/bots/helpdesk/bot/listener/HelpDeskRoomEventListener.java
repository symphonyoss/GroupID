package org.symphonyoss.symphony.bots.helpdesk.bot.listener;

import static org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient
    .TicketStateType.UNRESOLVED;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.events.SymRoomCreated;
import org.symphonyoss.client.events.SymRoomDeactivated;
import org.symphonyoss.client.events.SymRoomMemberDemotedFromOwner;
import org.symphonyoss.client.events.SymRoomMemberPromotedToOwner;
import org.symphonyoss.client.events.SymRoomReactivated;
import org.symphonyoss.client.events.SymRoomUpdated;
import org.symphonyoss.client.events.SymUserJoinedRoom;
import org.symphonyoss.client.events.SymUserLeftRoom;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.client.services.RoomServiceEventListener;
import org.symphonyoss.symphony.authenticator.model.Token;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.service.TicketService;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.client.SymphonyClientUtil;
import org.symphonyoss.symphony.bots.utility.message.SymMessageBuilder;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.MemberInfo;

import java.util.List;
import java.util.Optional;

/**
 * Listener for events in bot rooms
 * Created by rsanchez on 13/12/17.
 */
@Service
public class HelpDeskRoomEventListener implements RoomServiceEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(HelpDeskRoomEventListener.class);
  private static final String MESSAGEML_TEMPLATE = "<messageML>%s</messageML>";
  public static final String DEFAULT_QUESTION_VALUE = "N/A";

  private final String runawayAgentMessage;
  private final SymphonyClient symphonyClient;
  private final TicketClient ticketClient;
  private final HelpDeskBotConfig config;
  private final TicketService ticketService;
  private final SymphonyClientUtil symphonyClientUtil;

  public HelpDeskRoomEventListener(@Value("${noAgentsMessage}") String runawayAgentMessage,
      SymphonyClient symphonyClient, TicketClient ticketClient, HelpDeskBotConfig config,
      TicketService ticketService) {
    this.runawayAgentMessage = runawayAgentMessage;
    this.symphonyClient = symphonyClient;
    this.ticketClient = ticketClient;
    this.config = config;
    this.ticketService = ticketService;
    this.symphonyClientUtil = new SymphonyClientUtil(symphonyClient);
  }

  @Override
  public void onNewRoom(Room room) {
    // Do nothing
  }

  @Override
  public void onMessage(SymMessage symMessage) {
    // Do nothing
  }

  @Override
  public void onSymRoomDeactivated(SymRoomDeactivated symRoomDeactivated) {
    // Do nothing
  }

  @Override
  public void onSymRoomMemberDemotedFromOwner(
      SymRoomMemberDemotedFromOwner symRoomMemberDemotedFromOwner) {
    // Do nothing
  }

  @Override
  public void onSymRoomMemberPromotedToOwner(
      SymRoomMemberPromotedToOwner symRoomMemberPromotedToOwner) {
    // Do nothing
  }

  @Override
  public void onSymRoomReactivated(SymRoomReactivated symRoomReactivated) {
    // Do nothing
  }

  @Override
  public void onSymRoomUpdated(SymRoomUpdated symRoomUpdated) {
    // Do nothing
  }

  /**
   * Sends a welcome message when a user joins a room
   * @param symUserJoinedRoom the SymUserJoinedRoom object
   */
  @Override
  public void onSymUserJoinedRoom(SymUserJoinedRoom symUserJoinedRoom) {
    SymStream stream = symUserJoinedRoom.getStream();
    SymUser symUser = symUserJoinedRoom.getAffectedUser();

    LOGGER.info(String.format("User %s joined the room %s", symUser.getDisplayName(),
        stream.getRoomName()));

    sendWelcomeMessage(stream, symUser);
  }

  /**
   * The welcome message builder and sender
   * @param symStream The stream where to send the message
   * @param symUser The user to welcome
   */
  private void sendWelcomeMessage(SymStream symStream, SymUser symUser) {
    if (isBotUser(symUser) && !isAgentStreamId(symStream)) {
      String messageML = String.format(MESSAGEML_TEMPLATE, config.getWelcomeMessage());

      SymMessage symMessage = new SymMessage();
      symMessage.setMessage(messageML);
      symMessage.setSymUser(symUser);
      symMessage.setStream(symStream);

      try {
        symphonyClient.getMessageService().sendMessage(symStream, symMessage);
      } catch (MessagesException e) {
        LOGGER.error("Fail to send welcome message", e);
      }
    }
  }

  /**
   * Checks if a SymUser is the bot
   * @param symUser The SymUser to check
   * @return true if symUser is the bot, false otherwise
   */
  private boolean isBotUser(SymUser symUser) {
    return symUser.getId().equals(symphonyClient.getLocalUser().getId());
  }

  /**
   * Checks if a stream is the agent room for the bot
   * @param symStream The SymStream to check
   * @return true if the symStream is the stream of the agent room, false otherwise
   */
  private boolean isAgentStreamId(SymStream symStream) {
    return symStream.getStreamId().equals(config.getAgentStreamId());
  }

  /**
   * When the last agent leaves the service room, mark the ticket as unserviced, warn the client
   * room, and resend the ticket message to the agent room for the agents to claim
   * @param symUserLeftRoom The SymUserLeftRoom object
   */
  @Override
  public void onSymUserLeftRoom(SymUserLeftRoom symUserLeftRoom) {
    String jwt = symphonyClientUtil.getAuthToken();

    SymStream symStream = symUserLeftRoom.getStream();

    if (!isAgentStreamId(symStream)) {
      Ticket ticket = ticketClient.getTicketByServiceStreamId(jwt, symStream.getStreamId());

      if (ticket != null && UNRESOLVED.getState().equals(ticket.getState())
          && isRoomUnserviced(symStream.getStreamId())) {
        LOGGER.info("Only the bot was left in the ticket room. Reopening ticket in the Agent room");

        // Update ticket to a state that it can be claimed again by another agent
        ticket.setAgent(null);
        ticket.setState(TicketClient.TicketStateType.UNSERVICED.toString());
        ticketClient.updateTicket(jwt, ticket);

        SymUser localUser = symphonyClient.getLocalUser();

        // Retrieve the client question
        String question = DEFAULT_QUESTION_VALUE;
        try {
          Optional<SymMessage> clientMessage =
              symphonyClientUtil.getSymMessageByStreamAndId(ticket.getClientStreamId(),
                  ticket.getQuestionTimestamp() - 1, ticket.getConversationID());
          if (clientMessage.isPresent()) {
            question = clientMessage.get().getMessageText();
          }
        } catch (MessagesException e) {
          LOGGER.error("The client question could not be retrieved.", e);
        }


        // Resend ticket message to the agent room
        SymMessage symMessage =
            SymMessageBuilder.message(String.format(MESSAGEML_TEMPLATE, question)).build();
        symMessage.setFromUserId(ticket.getClient().getUserId());
        ticketService.sendTicketMessageToAgentStreamId(ticket, symMessage);

        // Warn the client that no agents are connected to the ticket
        symMessage = new SymMessage();
        symMessage.setMessageText(runawayAgentMessage);
        symMessage.setFromUserId(localUser.getId());
        ticketService.sendClientMessageToServiceStreamId(ticket.getClientStreamId(), symMessage);

      }
    }
  }

  /**
   * Checks if room is out of agents
   * @param streamId The Stream ID for the room
   * @return true if room is out of agents, false otherwise
   */
  private boolean isRoomUnserviced(String streamId) {
    try {
      List<MemberInfo> membershipList =
          symphonyClient.getRoomMembershipClient().getRoomMembership(streamId);
      return membershipList.size() <= 1;
    } catch (SymException e) {
      LOGGER.error(String.format("Could not find membership list for stream [%s]", streamId));
      return false;
    }
  }

  @Override
  public void onSymRoomCreated(SymRoomCreated symRoomCreated) {
    // Do nothing
  }

}
