package org.symphonyoss.symphony.bots.ai.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.model.AiArgumentMap;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.ai.model.SymphonyAiSessionKey;

/**
 * Unit tests for {@link SymphonyAiEventListenerImpl}
 * Created by rsanchez on 05/01/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class SymphonyAiEventListenerImplTest {

  private static final String PREFIX = "@";

  private static final String MSG_ID = "MSG";

  private static final String SESSION_KEY = "SESSION";

  private static final Long USER_ID = 1234L;

  private static final String STREAM_ID = "STREAM";

  @Mock
  private AiCommandInterpreter aiCommandInterpreter;

  @Mock
  private AiResponder aiResponder;

  @Mock
  private AiCommand aiCommand;

  @Mock
  private AiConversation aiConversation;

  private AiCommandMenu commandMenu;

  private SymphonyAiSessionKey sessionKey = new SymphonyAiSessionKey(SESSION_KEY, USER_ID, STREAM_ID);

  private SymphonyAiMessage message = new SymphonyAiMessage("");

  private SymphonyAiEventListenerImpl eventListener;

  @Before
  public void init() {
    this.message.setMessageId(MSG_ID);
    this.commandMenu = new AiCommandMenu(PREFIX);

    doReturn(true).when(aiConversation).isAllowCommands();
    doReturn(commandMenu).when(aiConversation).getAiCommandMenu();

    this.eventListener = new SymphonyAiEventListenerImpl(aiCommandInterpreter, aiResponder);
  }

  @Test
  public void testDoNotAllowCommands() {
    doReturn(false).when(aiConversation).isAllowCommands();

    eventListener.onMessage(sessionKey, message, aiConversation);
    verify(aiCommandInterpreter, never()).isCommand(any(AiCommand.class), any(SymphonyAiMessage.class), anyString());
    verify(aiConversation, times(1)).onMessage(aiResponder, message);
  }

  @Test
  public void testWithoutPrefix() {
    eventListener.onMessage(sessionKey, message, aiConversation);
    verify(aiCommandInterpreter, never()).isCommand(any(AiCommand.class), any(SymphonyAiMessage.class), anyString());
    verify(aiConversation, times(1)).onMessage(aiResponder, message);
  }

  @Test
  public void testEmptyCommandSet() {
    doReturn(true).when(aiCommandInterpreter).hasPrefix(message, PREFIX);

    eventListener.onMessage(sessionKey, message, aiConversation);

    verify(aiCommandInterpreter, never()).isCommand(any(AiCommand.class), any(SymphonyAiMessage.class), anyString());
    verify(aiResponder, times(1)).respondWithUseMenu(sessionKey, commandMenu, message);
    verify(aiConversation, never()).onMessage(aiResponder, message);
  }

  @Test
  public void testCommands() {
    AiArgumentMap args = new AiArgumentMap();

    this.commandMenu.addCommand(aiCommand);

    doReturn(true).when(aiCommandInterpreter).hasPrefix(message, PREFIX);
    doReturn(true).when(aiCommandInterpreter).isCommand(aiCommand, message, PREFIX);
    doReturn(args).when(aiCommandInterpreter).readCommandArguments(aiCommand, message, PREFIX);

    eventListener.onMessage(sessionKey, message, aiConversation);

    verify(aiCommand, times(1)).executeCommand(sessionKey, aiResponder, args);
    verify(aiConversation, never()).onMessage(aiResponder, message);
  }

  @Test
  public void testEqualsMessage() {
    doReturn(MSG_ID).when(aiConversation).getLastMessageId();

    eventListener.onMessage(sessionKey, message, aiConversation);

    verify(aiCommandInterpreter, never()).isCommand(any(AiCommand.class), any(SymphonyAiMessage.class), anyString());
    verify(aiConversation, never()).onMessage(aiResponder, message);
  }

}
