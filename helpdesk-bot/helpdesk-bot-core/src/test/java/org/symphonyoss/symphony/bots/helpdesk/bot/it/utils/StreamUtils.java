package org.symphonyoss.symphony.bots.helpdesk.bot.it.utils;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.RoomException;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.symphony.clients.model.SymRoomAttributes;

import java.util.UUID;

/**
 * Utility class to create streams and manage stream memberships.
 *
 * Created by robson on 03/03/18.
 */
public class StreamUtils {

  private final SymphonyClient symphonyClient;

  public StreamUtils(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
  }

  /**
   * Create a new room.
   *
   * @param roomName Room name
   * @param showHistory Show history flag
   * @return Room created
   */
  public Room createRoom(String roomName, Boolean showHistory) throws RoomException {
    SymRoomAttributes symRoomAttributes = new SymRoomAttributes();
    symRoomAttributes.setViewHistory(showHistory);

    String randomId = UUID.randomUUID().toString();
    symRoomAttributes.setName(roomName + " " + randomId);
    symRoomAttributes.setDescription(symRoomAttributes.getName());

    Room queueRoom = symphonyClient.getRoomService().createRoom(symRoomAttributes);

    return queueRoom;
  }

  /**
   * Add a membership to room
   *
   * @param streamId Room ID
   * @param userId User ID
   */
  public void addMembershipToRoom(String streamId, Long userId) throws SymException {
    symphonyClient.getRoomMembershipClient().addMemberToRoom(streamId, userId);
  }

}
