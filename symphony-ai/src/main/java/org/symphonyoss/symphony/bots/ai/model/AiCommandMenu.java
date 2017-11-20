package org.symphonyoss.symphony.bots.ai.model;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by nick.tarsillo on 8/20/17.
 * A menu of Ai Commands.
 */
public class AiCommandMenu {
  private Set<AiCommand> commandSet = new LinkedHashSet<>();
  private String commandPrefix;

  public AiCommandMenu(String commandPrefix) {
    this.commandPrefix = commandPrefix;
  }

  public AiCommandMenu() {}

  public void addCommand(AiCommand aiCommand) {
    commandSet.add(aiCommand);
  }

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

  public String getCommandPrefix() {
    return commandPrefix;
  }

  public void setCommandPrefix(String commandPrefix) {
    this.commandPrefix = commandPrefix;
  }
}
