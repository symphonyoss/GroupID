package org.symphonyoss.symphony.bots.ai.impl;

import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.common.AiConstants;
import org.symphonyoss.symphony.bots.ai.model.AiArgumentMap;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.ai.model.ArgumentType;

/**
 * Created by nick.tarsillo on 8/20/17.
 */
public class AiCommandInterpreterImpl implements AiCommandInterpreter {
  @Override
  public boolean isCommand(AiCommand aiCommand, AiMessage command, String commandPrefix) {
    //TODO APP-1489 (Need to fix this)

    String withoutPrefix = aiCommand.getCommand().substring(commandPrefix.length());
    String[] potentialCommand = withoutPrefix.split(" ");
    String[] actualCommand = withoutPrefix.split(" ");
    int argIndex = 0;
    if (potentialCommand.length == actualCommand.length) {
      for (int index = 0; index < potentialCommand.length; index++) {
        if (argIndex < aiCommand.getArgumentTypes().length &&
            actualCommand[index].contains(AiConstants.ARGUMENT_START_CHAR) &&
            actualCommand[index].contains(AiConstants.ARGUMENT_END_CHAR)) {
          if (!isArgument(potentialCommand[index], actualCommand[index],
              aiCommand.getArgumentTypes()[argIndex])) {
            return false;
          }
          argIndex++;
        } else if (!actualCommand[index].equals(potentialCommand[index])) {
          return false;
        }
      }
    }

    return true;
  }

  private boolean isArgument(String potentialArgument, String actualArgument, ArgumentType argumentType) {
    try {
      String prefix = actualArgument.substring(0,
          actualArgument.lastIndexOf(AiConstants.ARGUMENT_START_CHAR) - 1);
      String argVal = potentialArgument.substring(potentialArgument.indexOf(prefix) + 1);
      switch (argumentType) {
        case LONG:
          Long.parseLong(argVal);
        case DOUBLE:
          Double.parseDouble(argVal);
        case INTEGER:
          Integer.parseInt(argVal);
        case STRING:
          break;
        default:
          break;
      }
    } catch (Exception e) {
      return false;
    }

    return true;
  }

  @Override
  public AiArgumentMap readCommandArguments(AiCommand aiCommand, AiMessage command) {
    String[] actualCommand = aiCommand.getCommand().split(" ");
    String[] splitCommand = command.getAiMessage().split(" ");

    AiArgumentMap aiArgumentMap = new AiArgumentMap();
    for (int index = 0; index < splitCommand.length; index++) {
      if (actualCommand[index].contains(AiConstants.ARGUMENT_START_CHAR) &&
          actualCommand[index].contains(AiConstants.ARGUMENT_END_CHAR)) {
        String prefix = actualCommand[index].substring(0,
            actualCommand[index].lastIndexOf(AiConstants.ARGUMENT_START_CHAR) - 1);
        aiArgumentMap.addArgument(
            actualCommand[index].substring(
                actualCommand[index].lastIndexOf(AiConstants.ARGUMENT_START_CHAR) + 1,
                actualCommand[index].lastIndexOf(AiConstants.ARGUMENT_END_CHAR) - 1),
            splitCommand[index].substring(splitCommand[index].indexOf(prefix) + 1));
      }
    }

    return aiArgumentMap;
  }

  @Override
  public String readCommandWithoutArguments(AiCommand aiCommand) {
    String[] actualCommand = aiCommand.getCommand().split(" ");
    String command = aiCommand.getCommand();

    for(String potentialArg : actualCommand) {
      if(potentialArg.contains(AiConstants.ARGUMENT_START_CHAR) &&
          potentialArg.contains(AiConstants.ARGUMENT_END_CHAR)) {
        command = command.replace(potentialArg, "");
      }
    }

    return command.trim();
  }
}