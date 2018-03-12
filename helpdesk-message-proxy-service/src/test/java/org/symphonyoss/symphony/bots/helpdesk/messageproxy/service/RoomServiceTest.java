package org.symphonyoss.symphony.bots.helpdesk.messageproxy.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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

  private static final String TICKET_ID = "WBG23VD1A6";

  private static final long USER_ID = 23011987l;

  private static final String USER_NAME = "Cassiano Repache";

  private static final String ROOM_ID = "2541793";

  private static final String PODNAME = "PODNAME";

  private static final String LONG_PODNAME = "EXCEEDINGLY LONG PODNAME";

  private static final String LONG_GROUP_ID = "exceedinglyLongGroupId";

  private static final String EXPECTED_LONG_NAME = "[EXCEEDINGLY] [exceedingl] Ticket Room #WBG23VD1A6";

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

    Room room = service.createServiceStream(TICKET_ID, GROUP_ID, PODNAME);

    assertEquals(ROOM_ID, room.getId());
    assertEquals(Boolean.TRUE, room.getRoomDetail().getRoomAttributes().getViewHistory());
  }

  @Test
  public void createRoomWithShowHistoryFalse() throws RoomException {
    when(roomService.createRoom(any(SymRoomAttributes.class))).thenThrow(RoomException.class).thenReturn(mockRoom());

    Room room = service.createServiceStream(TICKET_ID, GROUP_ID, PODNAME);

    assertEquals(ROOM_ID, room.getId());
    assertEquals(Boolean.FALSE, room.getRoomDetail().getRoomAttributes().getViewHistory());
  }

  @Test
  public void createRoomWithLongTags() throws RoomException {
    doReturn(mockRoom()).when(roomService).createRoom(any(SymRoomAttributes.class));

    Room room = service.createServiceStream(TICKET_ID, LONG_GROUP_ID, LONG_PODNAME);

    assertEquals(ROOM_ID, room.getId());
    ArgumentCaptor<SymRoomAttributes> captor = ArgumentCaptor.forClass(SymRoomAttributes.class);
    verify(roomService).createRoom(captor.capture());
    assertEquals(EXPECTED_LONG_NAME, captor.getValue().getName());
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
