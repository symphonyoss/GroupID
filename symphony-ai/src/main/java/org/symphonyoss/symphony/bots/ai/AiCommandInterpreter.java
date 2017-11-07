package org.symphonyoss.symphony.bots.ai;

import org.symphonyoss.symphony.bots.ai.model.AiArgumentMap;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
import org.symphonyoss.symphony.bots.ai.model.AiMessage;

/**
 * Created by nick.tarsillo on 8/20/17.
 * Interprets commands.
 */
public interface AiCommandInterpreter {
  /**
   * Decides if an ai message is the command line of a specific command.
   */
  boolean isCommand(AiCommand aiCommand, AiMessage command, String commandPrefix);

  /**
   * Reads a command line message for a set of arguments.
   * Ex: hello world 1
   * Arguments: {1}
   */
  AiArgumentMap readCommandArguments(AiCommand aiCommand, AiMessage command);

  /**
   * Extracts the pure command from a ai command.
   * Example:
   * hello world 1
   * Result: "hello world"
   */
  String readCommandWithoutArguments(AiCommand aiCommand);
}
