package org.symphonyoss.symphony.bots.helpdesk.messageproxy.service;

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
   *
   * @param ticketId the ticket ID to create the service stream for
   * @param groupId group Id
   * @param podName Pod Name
   * @param viewHistory View history flag
   * @return the created stream
   */
  private Room newServiceStream(String ticketId, String groupId, String podName,
      Boolean viewHistory)
      throws RoomException {
    SymRoomAttributes roomAttributes = new SymRoomAttributes();
    roomAttributes.setCreatorUser(symphonyClient.getLocalUser());

    roomAttributes.setDescription("Service room for ticket " + ticketId + ".");
    roomAttributes.setDiscoverable(false);
    roomAttributes.setMembersCanInvite(true);

    if (podName != null) {
      roomAttributes.setName("[" + podName + "] [" + groupId + "] Ticket Room #" + ticketId);
    } else {
      roomAttributes.setName("[" + groupId + "] Ticket Room #" + ticketId);
    }

    roomAttributes.setReadOnly(false);
    roomAttributes.setPublic(false);
    roomAttributes.setViewHistory(viewHistory);

    Room room = symphonyClient.getRoomService().createRoom(roomAttributes);
    room.getRoomDetail().getRoomAttributes().setViewHistory(viewHistory);
    LOGGER.info("Created new room: " + roomAttributes.getName());

    return room;
  }

  /**
   * Creates a new service stream for a ticket. There is a retry behavior to avoid errors when
   * the POD doesn't support to create private room with the view history flag set to TRUE.
   * @param ticketId the ticket ID to create the service stream for
   * @param groupId group Id
   * @return the created stream
   */
  public Room createServiceStream(String ticketId, String groupId) throws RoomException {
    return createServiceStream(ticketId, groupId, null);
  }

  /**
   * Creates a new service stream for a ticket. There is a retry behavior to avoid errors when
   * the POD doesn't support to create private room with the view history flag set to TRUE.
   * @param ticketId the ticket ID to create the service stream for
   * @param groupId group Id
   * @param podName Pod Name
   * @return the created stream
   */
  public Room createServiceStream(String ticketId, String groupId, String podName)
      throws RoomException {
    try {
      return newServiceStream(ticketId, groupId, podName, Boolean.TRUE);
    } catch (RoomException e) {
      return newServiceStream(ticketId, groupId, podName, Boolean.FALSE);
    }
  }
}
