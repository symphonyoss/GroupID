package com.symphony.bots.ai.model;

import com.symphony.bots.ai.common.AiConstants;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by nick.tarsillo on 8/20/17.
 * A menu of Ai Commands.
 */
public class AiCommandMenu {
  private Set<AiCommand> commandSet = new LinkedHashSet<>();

  public void addCommand(AiCommand aiCommand) {
    commandSet.add(aiCommand);
  }

  public Set<AiCommand> getCommandSet() {
    return commandSet;
  }

  public String toString() {
    AiCommand[] commands = (AiCommand[]) commandSet.toArray();
    Arrays.sort(commands);

    String toString = AiConstants.MENU_TITLE + ": \n";
    for(AiCommand aiCommand : commands) {
      toString += aiCommand.getCommand() + "\n";
    }

    return toString;
  }
}
