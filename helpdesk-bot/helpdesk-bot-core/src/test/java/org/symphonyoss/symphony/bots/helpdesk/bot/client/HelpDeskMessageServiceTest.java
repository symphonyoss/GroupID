package org.symphonyoss.symphony.bots.helpdesk.bot.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.symphonyoss.client.events.SymEventTypes.Type.USERJOINEDROOM;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.events.SymEvent;
import org.symphonyoss.client.events.SymEventPayload;
import org.symphonyoss.client.events.SymUserJoinedRoom;
import org.symphonyoss.client.services.RoomServiceEventListener;

/**
 * Created by rsanchez on 14/12/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class HelpDeskMessageServiceTest {

  @Mock
  private SymphonyClient symphonyClient;

  @Mock
  private RoomServiceEventListener roomServiceListener;

  private HelpDeskMessageService messageService;

  @Before
  public void init() {
    this.messageService = new HelpDeskMessageService(symphonyClient);
    this.messageService.addRoomServiceEventListener(roomServiceListener);
  }

  @Test
  public void testEvent() {
    SymUserJoinedRoom symUserJoinedRoom = new SymUserJoinedRoom();

    SymEventPayload payload = new SymEventPayload();
    payload.setUserJoinedRoom(symUserJoinedRoom);

    SymEvent event = new SymEvent();
    event.setType(USERJOINEDROOM.toString());
    event.setPayload(payload);

    assertNull(event.getId());

    messageService.onEvent(event);

    verify(roomServiceListener, times(1)).onSymUserJoinedRoom(symUserJoinedRoom);

    assertEquals(USERJOINEDROOM.toString(), event.getId());
  }

}
