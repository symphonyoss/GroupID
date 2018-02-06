package org.symphonyoss.symphony.bots.helpdesk.messageproxy.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.RoomException;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.symphony.clients.model.SymRoomAttributes;

/**
 * Created by rsanchez on 01/12/17.
 */
@Service
public class RoomService {

  private static final Logger LOGGER = LoggerFactory.getLogger(RoomService.class);

  private final SymphonyClient symphonyClient;

  public RoomService(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
  }

  /**
   * Creates a new service stream for a ticket.
   * @param ticketId the ticket ID to create the service stream for
   * @param groupId group Id
   * @return the stream ID for the new service stream
   */
  private Room newServiceStream(String ticketId, String groupId, Boolean viewHistory) {
    SymRoomAttributes roomAttributes = new SymRoomAttributes();
    roomAttributes.setCreatorUser(symphonyClient.getLocalUser());

    roomAttributes.setDescription("Service room for ticket " + ticketId + ".");
    roomAttributes.setDiscoverable(false);
    roomAttributes.setMembersCanInvite(true);
    roomAttributes.setName("[" + groupId + "] Ticket Room #" + ticketId);
    roomAttributes.setReadOnly(false);
    roomAttributes.setPublic(false);
    roomAttributes.setViewHistory(viewHistory);

    Room room;

    try {
      room = symphonyClient.getRoomService().createRoom(roomAttributes);
      LOGGER.info("Created new room: " + roomAttributes.getName());

      return room;
    } catch (RoomException e) {
      LOGGER.error("Create room failed: ", e);
      return null;
    }
  }

  public Room createServiceStream(String ticketId, String groupId) {
    Room serviceStream = newServiceStream(ticketId, groupId, Boolean.TRUE);

    if (serviceStream != null) {
      serviceStream = newServiceStream(ticketId, groupId, Boolean.FALSE);
    }

    return serviceStream;
  }

}
