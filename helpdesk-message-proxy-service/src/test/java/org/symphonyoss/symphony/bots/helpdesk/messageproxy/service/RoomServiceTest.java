package org.symphonyoss.symphony.bots.helpdesk.messageproxy.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.RoomException;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.symphony.clients.model.SymRoomAttributes;
import org.symphonyoss.symphony.clients.model.SymRoomDetail;
import org.symphonyoss.symphony.clients.model.SymUser;

/**
 * Created by Cassiano Repache on 02/06/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class RoomServiceTest {

  private static final String GROUP_ID = "unitTest";

  private static final String TICKET_ID = "WBG23VD1";

  private static final long USER_ID = 23011987l;

  private static final String USER_NAME = "Cassiano Repache";

  private static final String ROOM_ID = "2541793";

  private RoomService service;

  @Mock
  private SymphonyClient symphonyClient;

  @Mock
  private org.symphonyoss.client.services.RoomService roomService;

  @Before
  public void initMocks() {
    service = new RoomService(symphonyClient);

    doReturn(roomService).when(symphonyClient).getRoomService();
    mockGetLocalUser();
  }

  @Test
  public void createRoomWithShowHistoryTrue() throws RoomException {
    doReturn(mockRoom()).when(roomService).createRoom(any(SymRoomAttributes.class));

    Room room = service.createServiceStream(TICKET_ID, GROUP_ID);

    assertEquals(ROOM_ID, room.getId());
    assertEquals(Boolean.TRUE, room.getRoomDetail().getRoomAttributes().getViewHistory());
  }

  @Test
  public void createRoomWithShowHistoryFalse() throws RoomException {
    when(roomService.createRoom(any(SymRoomAttributes.class))).thenThrow(RoomException.class).thenReturn(mockRoom());

    Room room = service.createServiceStream(TICKET_ID, GROUP_ID);

    assertEquals(ROOM_ID, room.getId());
    assertEquals(Boolean.FALSE, room.getRoomDetail().getRoomAttributes().getViewHistory());
  }

  private void mockGetLocalUser() {
    SymUser symUser = new SymUser();
    symUser.setId(USER_ID);
    symUser.setDisplayName(USER_NAME);

    doReturn(symUser).when(symphonyClient).getLocalUser();
  }

  private Room mockRoom() {
    Room room = new Room();
    room.setId(ROOM_ID);

    SymRoomAttributes symRoomAttributes = new SymRoomAttributes();
    symRoomAttributes.setViewHistory(Boolean.TRUE);

    SymRoomDetail symRoomDetail = new SymRoomDetail();
    symRoomDetail.setRoomAttributes(symRoomAttributes);

    room.setRoomDetail(symRoomDetail);

    return room;
  }
}
