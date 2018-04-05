package org.symphonyoss.symphony.bots.ai.model;

import org.symphonyoss.symphony.bots.ai.AiResponder;

/**
 * A command that can triggered using a command line phrase.
 * <p>
 * Created by nick.tarsillo on 8/20/17.
 */
public abstract class AiCommand implements Comparable {

  private String command;

  private ArgumentType[] argumentTypes;

  /**
   * Constructs a new AI command with the command string, the usage, and the argument types. It
   * has no
   * permissions or actions.
   * @param command Command line argument
   * @param argumentTypes array containing the type of each argument
   */
  public AiCommand(String command, ArgumentType... argumentTypes) {
    this.command = command;
    this.argumentTypes = argumentTypes;
  }

  /**
   * Executes the command.
   * @param sessionKey the session key.
   * @param aiResponder a responder the ai can use to respond to users.
   * @param aiArgumentMap a map of arguments to execute the command with.
   */
  public abstract void executeCommand(AiSessionKey sessionKey, AiResponder aiResponder,
      AiArgumentMap aiArgumentMap);

  @Override
  public int compareTo(Object o) {
    if (o instanceof AiCommand) {
      AiCommand obj = (AiCommand) o;
      return command.compareTo(obj.getCommand());
    }

    return -2;
  }

  public String getCommand() {
    return command;
  }

  /**
   * Retrieve the argument types for this command
   * @return argument type array
   */
  public ArgumentType[] getArgumentTypes() {
    return argumentTypes;
  }
}
