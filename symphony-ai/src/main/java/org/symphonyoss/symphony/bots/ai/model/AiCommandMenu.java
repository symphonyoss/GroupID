package org.symphonyoss.symphony.bots.ai.model;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class contains a set of {@link AiCommand commands}, working like a menu.
 * <p>
 * Created by nick.tarsillo on 8/20/17.
 */
public class AiCommandMenu {
  private Set<AiCommand> commandSet = new LinkedHashSet<>();
  private String commandPrefix;

  /**
   * Creates an {@link AiCommand} instance with a command prefix
   * @param commandPrefix a string containg a command prefix
   */
  public AiCommandMenu(String commandPrefix) {
    this.commandPrefix = commandPrefix;
  }

  public AiCommandMenu() {}

  /**
   * Adds a command to the "menu"
   * @param aiCommand {@link AiCommand} to be added
   */
  public void addCommand(AiCommand aiCommand) {
    commandSet.add(aiCommand);
  }

  /**
   * Retrieve all commands for this menu instance
   * @return
   */
  public Set<AiCommand> getCommandSet() {
    return commandSet;
  }

  public String toString() {
    AiCommand[] commands = commandSet.toArray(new AiCommand[commandSet.size()]);
    Arrays.sort(commands);

    String toString = "";
    for(AiCommand aiCommand : commands) {
      toString += aiCommand.getCommand() + "\n";
    }

    return toString;
  }

  /**
   * Retrieves this menu command prefix
   * @return string containing the command prefix
   */
  public String getCommandPrefix() {
    return commandPrefix;
  }

  /**
   * Set the menu command prefix
   * @param commandPrefix
   */
  public void setCommandPrefix(String commandPrefix) {
    this.commandPrefix = commandPrefix;
  }
}
