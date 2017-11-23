package org.symphonyoss.symphony.bots.ai.impl;

import org.apache.commons.lang3.StringUtils;
import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.common.AiConstants;
import org.symphonyoss.symphony.bots.ai.model.AiArgumentMap;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.ai.model.ArgumentType;

import javax.ws.rs.ProcessingException;

/**
 * Created by nick.tarsillo on 8/20/17.
 */
public class AiCommandInterpreterImpl implements AiCommandInterpreter {
  @Override
  public boolean isCommand(AiCommand aiCommand, AiMessage command, String commandPrefix) {
    String potentialCommand = command.getAiMessage().toLowerCase().substring(commandPrefix.length()).trim();
    String[] actualCommand = aiCommand.getCommand().toLowerCase().split(" ");

    int commandIndex = 0;
    int commandPartIndex = 0;
    int argTypeIndex = 0;
    for (int index = 0; index < potentialCommand.length(); index++) {
      if (potentialCommand.charAt(index) != ' ' &&
          potentialCommand.charAt(index) != actualCommand[commandPartIndex].charAt(commandIndex)) {
        return false;
      }
      commandIndex ++;

      if (potentialCommand.charAt(index) == ' ') {
        commandPartIndex++;
        commandIndex = 0;
        if (isArgument(actualCommand[commandPartIndex])) {
          String arg;
          try {
            arg = getArgument(actualCommand[commandPartIndex],
                aiCommand.getArgumentTypes()[argTypeIndex], potentialCommand, index);
          } catch(ProcessingException e) {
            return false;
          }
          argTypeIndex ++;

          if(StringUtils.isBlank(arg)) {
            return false;
          }
          int endArgumentIndex = potentialCommand.indexOf(arg) + arg.length();
          int relativeIndex = potentialCommand.substring(endArgumentIndex).indexOf(" ");
          if(relativeIndex == -1) {
            break;
          }
          index = relativeIndex + endArgumentIndex - 1;
        }
      }
    }

    return true;
  }

  @Override
  public boolean hasPrefix(AiMessage command, String commandPrefix) {
    return StringUtils.isNotBlank(commandPrefix) && command.getAiMessage().startsWith(commandPrefix);
  }


  @Override
  public AiArgumentMap readCommandArguments(AiCommand aiCommand, AiMessage command, String commandPrefix) {
    AiArgumentMap aiArgumentMap = new AiArgumentMap();
    String potentialCommand = command.getAiMessage().substring(commandPrefix.length()).trim();
    String[] actualCommand = aiCommand.getCommand().split(" ");

    int commandPartIndex = 0;
    int argTypeIndex = 0;
    for (int index = 0; index < potentialCommand.length(); index++) {
      if (potentialCommand.charAt(index) == ' ') {
        commandPartIndex++;
        if (isArgument(actualCommand[commandPartIndex])) {
          String arg = getArgument(actualCommand[commandPartIndex],
              aiCommand.getArgumentTypes()[argTypeIndex], potentialCommand, index);
          aiArgumentMap.addArgument(actualCommand[commandPartIndex].substring(
              actualCommand[commandPartIndex].lastIndexOf(AiConstants.ARGUMENT_START_CHAR) + 1,
              actualCommand[commandPartIndex].lastIndexOf(AiConstants.ARGUMENT_END_CHAR)),
              arg);
          argTypeIndex ++;

          int endArgumentIndex = potentialCommand.indexOf(arg) + arg.length();
          int relativeIndex = potentialCommand.substring(endArgumentIndex).indexOf(" ");
          if(relativeIndex == -1) {
            break;
          }
          index = relativeIndex + endArgumentIndex - 1;
        }
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

  private String getArgument(String actualCommandPart, ArgumentType argumentType, String potentialCommand, int currentIndex) {
    String prefix = actualCommandPart.substring(0,
        actualCommandPart.lastIndexOf(AiConstants.ARGUMENT_START_CHAR));
    String sufix = actualCommandPart.substring(
        actualCommandPart.lastIndexOf(AiConstants.ARGUMENT_END_CHAR) + 1,
        actualCommandPart.length());
    if (!potentialCommand.substring(currentIndex + 1, currentIndex + 1 + prefix.length()).equals(prefix)) {
      throw new ProcessingException("Argument prefix is not: " + prefix);
    }

    String fromCurrent = potentialCommand.substring(currentIndex + 1 + prefix.length());
    int endIndex = 0;
    if(StringUtils.isNotBlank(sufix)) {
      endIndex = fromCurrent.indexOf(sufix);
    } else {
      endIndex = fromCurrent.indexOf(" ");
    }
    if(endIndex == -1) {
      endIndex = fromCurrent.length();
    }

    String arg = fromCurrent.substring(0, endIndex);
    if (!isArgumentType(argumentType, arg)) {
      throw new ProcessingException("Argument content is not of type: " + argumentType.name());
    }

    return arg;
  }

  private boolean isArgument(String commandPart) {
    return commandPart.contains(AiConstants.ARGUMENT_START_CHAR) &&
        commandPart.contains(AiConstants.ARGUMENT_END_CHAR);
  }

  private boolean isArgumentType(ArgumentType argumentType, String argVal) {
    try {
      switch (argumentType) {
        case LONG:
          Long.parseLong(argVal);
          break;
        case DOUBLE:
          Double.parseDouble(argVal);
          break;
        case INTEGER:
          Integer.parseInt(argVal);
          break;
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
}