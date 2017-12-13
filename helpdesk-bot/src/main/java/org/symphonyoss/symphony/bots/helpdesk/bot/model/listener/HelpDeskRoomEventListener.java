package org.symphonyoss.symphony.bots.helpdesk.bot.model.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.symphonyoss.client.services.MessageService;
import org.symphonyoss.client.services.RoomServiceEventListener;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
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

  private final HelpDeskBotConfig config;

  private final MessageService messageService;

  public HelpDeskRoomEventListener(SymphonyClient symphonyClient, HelpDeskBotConfig config) {
    this.messageService = symphonyClient.getMessageService();
    this.config = config;

    messageService.addRoomServiceEventListener(this);
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
  public void onSymRoomMemberDemotedFromOwner(SymRoomMemberDemotedFromOwner symRoomMemberDemotedFromOwner) {
    // Do nothing
  }

  @Override
  public void onSymRoomMemberPromotedToOwner(SymRoomMemberPromotedToOwner symRoomMemberPromotedToOwner) {
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

    LOGGER.info(String.format("User %s joined the room %s", symUser.getDisplayName(), stream.getRoomName()));

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
        messageService.sendMessage(symStream, symMessage);
      } catch (MessagesException e) {
        LOGGER.error("Fail to send welcome message", e);
      }
    }
  }

  private boolean isBotUser(SymUser symUser) {
    return symUser.getEmailAddress().equals(config.getDefaultAgentEmail());
  }

  private boolean isAgentStreamId(SymStream symStream) {
    return symStream.getStreamId().equals(config.getAgentStreamId());
  }

  @Override
  public void onSymUserLeftRoom(SymUserLeftRoom symUserLeftRoom) {
    SymUser symUser = symUserLeftRoom.getAffectedUser();
    SymStream symStream = symUserLeftRoom.getStream();

    if (isBotUser(symUser) && isAgentStreamId(symStream)) {
      LOGGER.warn("Bot user was removed from the agent room");
    }
  }

  @Override
  public void onSymRoomCreated(SymRoomCreated symRoomCreated) {
    // Do nothing
  }

}
