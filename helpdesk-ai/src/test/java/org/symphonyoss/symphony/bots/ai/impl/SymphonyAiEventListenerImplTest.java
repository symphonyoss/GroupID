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
import org.symphonyoss.symphony.bots.ai.AiAction;
import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.model.AiArgumentMap;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;

/**
 * Unit tests for {@link SymphonyAiEventListenerImpl}
 * Created by rsanchez on 05/01/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class SymphonyAiEventListenerImplTest {

  private static final String PREFIX = "@";

  private static final String MOCK_COMMAND = "command";

  @Mock
  private AiCommandInterpreter aiCommandInterpreter;

  @Mock
  private AiResponder aiResponder;

  @Mock
  private AiAction aiAction;

  private AiCommandMenu commandMenu;

  private AiSessionContext sessionContext = new AiSessionContext();

  private SymphonyAiMessage message = new SymphonyAiMessage("");

  private SymphonyAiEventListenerImpl eventListener;

  @Before
  public void init() {
    this.commandMenu = new AiCommandMenu(PREFIX);

    this.sessionContext.setAiCommandMenu(commandMenu);

    this.eventListener = new SymphonyAiEventListenerImpl(aiCommandInterpreter, aiResponder, false);
  }

  @Test
  public void testWithoutPrefix() {
    eventListener.onCommand(message, sessionContext);
    verify(aiCommandInterpreter, never()).isCommand(any(AiCommand.class), any(SymphonyAiMessage.class), anyString());
  }

  @Test
  public void testEmptyCommandSet() {
    doReturn(true).when(aiCommandInterpreter).hasPrefix(message, PREFIX);

    eventListener.onCommand(message, sessionContext);

    verify(aiCommandInterpreter, never()).isCommand(any(AiCommand.class), any(SymphonyAiMessage.class), anyString());
    verify(aiResponder, times(1)).respondWithUseMenu(sessionContext, message);
    verify(aiResponder, never()).respondWithSuggestion(sessionContext, aiCommandInterpreter, message);
  }

  @Test
  public void testSuggestCommands() {
    AiCommand command = new AiCommand(MOCK_COMMAND, MOCK_COMMAND);
    this.commandMenu.addCommand(command);

    this.eventListener = new SymphonyAiEventListenerImpl(aiCommandInterpreter, aiResponder, true);

    doReturn(true).when(aiCommandInterpreter).hasPrefix(message, PREFIX);

    eventListener.onCommand(message, sessionContext);

    verify(aiResponder, times(1)).respondWithUseMenu(sessionContext, message);
    verify(aiResponder, times(1)).respondWithSuggestion(sessionContext, aiCommandInterpreter, message);
  }

  @Test
  public void testCommands() {
    AiArgumentMap args = new AiArgumentMap();

    AiCommand command = new AiCommand(MOCK_COMMAND, MOCK_COMMAND);
    command.addAction(aiAction);

    this.commandMenu.addCommand(command);

    doReturn(true).when(aiCommandInterpreter).hasPrefix(message, PREFIX);
    doReturn(true).when(aiCommandInterpreter).isCommand(command, message, PREFIX);
    doReturn(args).when(aiCommandInterpreter).readCommandArguments(command, message, PREFIX);

    eventListener.onCommand(message, sessionContext);

    verify(aiAction, times(1)).doAction(sessionContext, aiResponder, args);
    verify(aiResponder, times(1)).respond(sessionContext);
  }

}
