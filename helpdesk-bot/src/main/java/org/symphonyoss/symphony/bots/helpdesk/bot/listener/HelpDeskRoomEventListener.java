package org.symphonyoss.symphony.bots.helpdesk.bot.listener;

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
import org.symphonyoss.client.model.Room;
import org.symphonyoss.client.services.RoomServiceEventListener;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.InstructionalMessageConfig;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.service.TicketService;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.message.SymMessageBuilder;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;

/**
 * Created by rsanchez on 13/12/17.
 */
@Service
public class HelpDeskRoomEventListener implements RoomServiceEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(HelpDeskRoomEventListener.class);
  private static final String WELCOME_MESSAGE_TEMPLATE = "<messageML>%s</messageML>";

  private final String runawayAgentMessage;
  private final SymphonyClient symphonyClient;
  private final TicketClient ticketClient;
  private final HelpDeskBotConfig config;
  private final InstructionalMessageConfig instructionalMessageConfig;
  private final TicketService ticketService;

  public HelpDeskRoomEventListener(@Value("${noAgentsMessage}") String runawayAgentMessage,
      SymphonyClient symphonyClient, TicketClient ticketClient,
      HelpDeskBotConfig config, InstructionalMessageConfig instructionalMessageConfig,
      TicketService ticketService) {
    this.runawayAgentMessage = runawayAgentMessage;
    this.symphonyClient = symphonyClient;
    this.ticketClient = ticketClient;
    this.config = config;
    this.instructionalMessageConfig = instructionalMessageConfig;
    this.ticketService = ticketService;
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

  @Override
  public void onSymUserJoinedRoom(SymUserJoinedRoom symUserJoinedRoom) {
    SymStream stream = symUserJoinedRoom.getStream();
    SymUser symUser = symUserJoinedRoom.getAffectedUser();

    LOGGER.info(String.format("User %s joined the room %s", symUser.getDisplayName(),
        stream.getRoomName()));

    sendWelcomeMessage(stream, symUser);
  }

  private void sendWelcomeMessage(SymStream symStream, SymUser symUser) {
    if (isBotUser(symUser) && !isAgentStreamId(symStream)) {
      String messageML = String.format(WELCOME_MESSAGE_TEMPLATE, config.getWelcomeMessage());

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

  private boolean isBotUser(SymUser symUser) {
    return symUser.getId().equals(symphonyClient.getLocalUser().getId());
  }

  private boolean isAgentStreamId(SymStream symStream) {
    return symStream.getStreamId().equals(config.getAgentStreamId());
  }

  @Override
  public void onSymUserLeftRoom(SymUserLeftRoom symUserLeftRoom) {
    SymUser symUser = symUserLeftRoom.getAffectedUser();
    SymStream symStream = symUserLeftRoom.getStream();

    if (isAgentStreamId(symStream)) {
      if (isBotUser(symUser)) {
        LOGGER.warn("Bot user was removed from the agent room");
      }
    } else {
      if (symStream.getMembers().size() <= 1) {

        LOGGER.info("Only the bot was left in the ticket room. Reopening ticket in the Agent room");
        // Update ticket first
        Ticket ticket = ticketClient.getTicketByServiceStreamId(symStream.getStreamId());
        ticket.setAgent(null);
        ticket.setState(TicketClient.TicketStateType.UNSERVICED.toString());
        ticketClient.updateTicket(ticket);

        // Resend ticket message to the agent room
        SymMessage symMessage = SymMessageBuilder.message("").build();
        symMessage.setFromUserId(symUser.getId());
        ticketService.sendTicketMessageToAgentStreamId(ticket, symMessage);

        // Warn the client that no agents are connected to the ticket
        symMessage = new SymMessage();
        symMessage.setMessageText(runawayAgentMessage);
        ticketService.sendClientMessageToServiceStreamId(ticket.getClientStreamId(), symMessage);
      }
    }
  }

  @Override
  public void onSymRoomCreated(SymRoomCreated symRoomCreated) {
    // Do nothing
  }

}
