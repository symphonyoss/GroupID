package org.symphonyoss.symphony.bots.helpdesk.bot.listener;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.events.SymUserJoinedRoom;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.services.MessageService;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.listener.HelpDeskRoomEventListener;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;

/**
 * Created by rsanchez on 14/12/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class HelpDeskRoomEventListenerTest {

  private static final Long MOCK_USER = 123456L;

  private static final String MOCK_STREAM = "Yc-my4qYo4-ZoQyR6C16o3___q_zYfhtWB";

  private static final Long MOCK_BOT_USER = 654321L;

  private static final String MOCK_BOT_STREAM = "Zs-nx3pQh3-XyKlT5B15m3___p_zHfetdA";

  private static final String WELCOME_MESSAGE = "Thanks for contacting the helpdesk bot";

  @Mock
  private HelpDeskBotConfig config;

  @Mock
  private SymphonyClient symphonyClient;

  @Mock
  private MessageService messageService;

  private HelpDeskRoomEventListener listener;

  @Before
  public void init() {
    doReturn(WELCOME_MESSAGE).when(config).getWelcomeMessage();
    doReturn(MOCK_BOT_STREAM).when(config).getAgentStreamId();

    doReturn(messageService).when(symphonyClient).getMessageService();

    SymUser symUser = new SymUser();
    symUser.setId(MOCK_BOT_USER);

    doReturn(symUser).when(symphonyClient).getLocalUser();

    this.listener = new HelpDeskRoomEventListener(symphonyClient, config);
  }

  @Test
  public void testJoinedRoomIsNotABotUser() throws MessagesException {
    SymUserJoinedRoom joinedRoom = mockEvent(MOCK_USER, MOCK_STREAM);

    listener.onSymUserJoinedRoom(joinedRoom);

    verify(messageService, never()).sendMessage(any(SymStream.class), any(SymMessage.class));
  }

  @Test
  public void testJoinedRoomIsAgentStream() throws MessagesException {
    SymUserJoinedRoom joinedRoom = mockEvent(MOCK_BOT_USER, MOCK_BOT_STREAM);

    listener.onSymUserJoinedRoom(joinedRoom);

    verify(messageService, never()).sendMessage(any(SymStream.class), any(SymMessage.class));
  }

  @Test
  public void testJoinedRoom() throws MessagesException {
    SymUserJoinedRoom joinedRoom = mockEvent(MOCK_BOT_USER, MOCK_STREAM);

    listener.onSymUserJoinedRoom(joinedRoom);

    verify(messageService, times(1)).sendMessage(any(SymStream.class), any(SymMessage.class));
  }

  private SymUserJoinedRoom mockEvent(Long userId, String stream) {
    SymUser symUser = new SymUser();
    symUser.setId(userId);
    symUser.setDisplayName(StringUtils.EMPTY);

    SymStream symStream = new SymStream();
    symStream.setRoomName(StringUtils.EMPTY);
    symStream.setStreamId(stream);

    SymUserJoinedRoom symUserJoinedRoom = new SymUserJoinedRoom();
    symUserJoinedRoom.setAffectedUser(symUser);
    symUserJoinedRoom.setStream(symStream);

    return symUserJoinedRoom;
  }

}
