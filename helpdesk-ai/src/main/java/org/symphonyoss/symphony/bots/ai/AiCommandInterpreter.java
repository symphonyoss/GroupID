package org.symphonyoss.symphony.bots.ai;

import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiArgumentMap;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;

/**
 * This should be used to read and interpret AI commands.
 * <p>
 * Created by nick.tarsillo on 8/20/17.
 */
public interface AiCommandInterpreter {

  /**
   * Decides if an ai message is the command line of a specific command.
   * @param aiCommand Base command used to interpret the AI message
   * @param command Message to be checked
   * @param commandPrefix the command prefix
   * @return True if the message is a call for the given command, false otherwise.
   */
  boolean isCommand(AiCommand aiCommand, AiMessage command, String commandPrefix);

  /**
   * Checks if AiMessage starts with menu prefix, and therefore should be treated as a command.
   * @param command the message to be checked
   * @param commandPrefix command prefix to be checked in the message
   * @return True if the a message has the given command prefix
   */
  boolean hasPrefix(AiMessage command, String commandPrefix);

  /**
   * Reads the arguments contained in a command line message.
   * <p>
   * <b>Example:</b>
   * <br>
   * Command: hello world 1
   * <br>
   * Arguments: {1}
   * @param aiCommand Command pattern to be used in the message parsing
   * @param command message to be analysed.
   * @param commandPrefix command prefix to be checked in the message
   * @return The arguments contained in a command message
   */
  AiArgumentMap readCommandArguments(AiCommand aiCommand, AiMessage command, String commandPrefix);

  /**
   * Extracts the command from an {@link AiCommand}.
   * <p>
   * <b>Example:</b>
   * <br>
   * Command: hello world 1
   * <br>
   * Result: "hello world"
   * @param aiCommand
   * @return The command without arguments
   */
  String readCommandWithoutArguments(AiCommand aiCommand);
}
