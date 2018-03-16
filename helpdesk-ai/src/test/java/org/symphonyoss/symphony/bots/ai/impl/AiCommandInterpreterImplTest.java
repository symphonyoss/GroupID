package org.symphonyoss.symphony.bots.ai.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.symphonyoss.symphony.bots.ai.model.AiArgumentMap;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.ai.model.ArgumentType;

/**
 * Created by rsanchez on 05/01/18.
 */
public class AiCommandInterpreterImplTest {

  private static final String PREFIX = "@";

  private static final String MOCK_COMMAND = "command";

  private static final String MOCK_ARGS = "{arg}";

  private static final String INVALID = "invalid";

  private AiCommandInterpreterImpl aiCommandInterpreter = new AiCommandInterpreterImpl();

  @Test
  public void testEmptyCommand() {
    AiCommand command = new AiCommand(MOCK_COMMAND, MOCK_COMMAND);
    AiMessage message = new AiMessage(PREFIX);

    assertFalse(aiCommandInterpreter.isCommand(command, message, PREFIX));
  }

  @Test
  public void testInvalidCommand() {
    AiCommand command = new AiCommand(MOCK_COMMAND, MOCK_COMMAND);
    AiMessage message = new AiMessage(PREFIX + " " + INVALID);

    assertFalse(aiCommandInterpreter.isCommand(command, message, PREFIX));
  }

  @Test
  public void testValidCommand() {
    AiCommand command = new AiCommand(MOCK_COMMAND, MOCK_COMMAND);
    AiMessage message = new AiMessage(PREFIX + " " + MOCK_COMMAND);

    assertTrue(aiCommandInterpreter.isCommand(command, message, PREFIX));
  }

  @Test
  public void testInvalidNumberOfArguments() {
    AiCommand command = new AiCommand(MOCK_COMMAND + " " + MOCK_ARGS, MOCK_COMMAND, ArgumentType.STRING);
    AiMessage message = new AiMessage(PREFIX + " " + MOCK_COMMAND);

    assertFalse(aiCommandInterpreter.isCommand(command, message, PREFIX));
  }

  @Test
  public void testInvalidArguments() {
    AiCommand command = new AiCommand(MOCK_COMMAND + " " + MOCK_ARGS, MOCK_COMMAND, ArgumentType.LONG);
    AiMessage message = new AiMessage(PREFIX + " " + MOCK_COMMAND + " " + INVALID);

    assertFalse(aiCommandInterpreter.isCommand(command, message, PREFIX));
  }

  @Test
  public void testValidArguments() {
    AiCommand command = new AiCommand(MOCK_COMMAND + " " + MOCK_ARGS, MOCK_COMMAND, ArgumentType.STRING);
    AiMessage message = new AiMessage(PREFIX + " " + MOCK_COMMAND + " " + INVALID);

    assertTrue(aiCommandInterpreter.isCommand(command, message, PREFIX));
  }

  @Test
  public void testEmptyPrefix() {
    AiMessage message = new AiMessage(StringUtils.EMPTY);
    assertFalse(aiCommandInterpreter.hasPrefix(message, PREFIX));
  }

  @Test
  public void testInvalidPrefix() {
    AiMessage message = new AiMessage(INVALID);
    assertFalse(aiCommandInterpreter.hasPrefix(message, PREFIX));
  }

  @Test
  public void testValidPrefix() {
    AiMessage message = new AiMessage(PREFIX);
    assertTrue(aiCommandInterpreter.hasPrefix(message, PREFIX));
  }

  @Test
  public void testReadCommandArgsEmpty() {
    AiCommand command = new AiCommand(MOCK_COMMAND + " " + MOCK_ARGS, MOCK_COMMAND);
    AiMessage message = new AiMessage(PREFIX + " " + MOCK_COMMAND);

    AiArgumentMap result = aiCommandInterpreter.readCommandArguments(command, message, PREFIX);
    assertTrue(result.getKeySet().isEmpty());
  }

  @Test
  public void testReadCommandArgs() {
    AiCommand command = new AiCommand(MOCK_COMMAND + " " + MOCK_ARGS, MOCK_COMMAND, ArgumentType.STRING);
    AiMessage message = new AiMessage(PREFIX + " " + MOCK_COMMAND + " " + INVALID);

    AiArgumentMap result = aiCommandInterpreter.readCommandArguments(command, message, PREFIX);
    assertEquals(1, result.getKeySet().size());

    String arg = result.getArgumentAsString("arg");
    assertEquals(arg, INVALID);
  }

}
