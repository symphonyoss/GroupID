package org.symphonyoss.symphony.bots.ai.impl;

import org.apache.commons.lang3.StringUtils;
import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.common.AiConstants;
import org.symphonyoss.symphony.bots.ai.model.AiArgumentMap;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.ai.model.ArgumentType;

import java.util.Arrays;

import javax.ws.rs.ProcessingException;

/**
 * Created by nick.tarsillo on 8/20/17.
 */
public class AiCommandInterpreterImpl implements AiCommandInterpreter {

  private static final String PATTERN_ARGUMENT_START = "\\{";

  private static final String PATTERN_ARGUMENT_END = "\\}";

  @Override
  public boolean isCommand(AiCommand aiCommand, AiMessage command, String commandPrefix) {
    String commandNormalized = command.getAiMessage().toLowerCase();
    String aiCommandNormalized = aiCommand.getCommand().toLowerCase();

    String potentialCommand = commandNormalized.substring(commandPrefix.length()).trim();

    if (StringUtils.isEmpty(potentialCommand)) {
      return false;
    }

    String actualCommand = aiCommandNormalized.split(" ")[0];
    String[] commandAndArgs = potentialCommand.split(" ");

    ArgumentType[] argumentTypes = aiCommand.getArgumentTypes();

    if ((argumentTypes != null) && (commandAndArgs.length < aiCommand.getArgumentTypes().length + 1)) {
      return false;
    }

    return checkCommand(aiCommand, commandAndArgs, actualCommand);
  }

  private boolean checkCommand(AiCommand command, String[] potentialCommand, String actualCommand) {
    ArgumentType[] argumentTypes = command.getArgumentTypes();

    if ((argumentTypes == null) || (argumentTypes.length == 0)) {
      return actualCommand.equals(potentialCommand[0]);
    }

    return actualCommand.equals(potentialCommand[0]) && checkArguments(command, potentialCommand);
  }

  private boolean checkArguments(AiCommand command, String[] potentialCommand) {
    for (int i = 0; i < command.getArgumentTypes().length; i++) {
      String commandArg = potentialCommand[i+1];

      if (!isValidArgument(command, commandArg, i)) {
        return false;
      }
    }

    return true;
  }

  private boolean isValidArgument(AiCommand command, String commandArg, int index) {
    return isArgumentType(command.getArgumentTypes()[index], commandArg);
  }

  @Override
  public boolean hasPrefix(AiMessage command, String commandPrefix) {
    return StringUtils.isNotBlank(commandPrefix) && command.getAiMessage().startsWith(commandPrefix);
  }

  @Override
  public AiArgumentMap readCommandArguments(AiCommand aiCommand, AiMessage command, String commandPrefix) {
    AiArgumentMap aiArgumentMap = new AiArgumentMap();

    String potentialCommand = command.getAiMessage().substring(commandPrefix.length()).trim();

    String[] actualCommand = aiCommand.getCommand().toLowerCase().split(" ");
    String[] commandAndArgs = potentialCommand.split(" ");

    ArgumentType[] argumentTypes = aiCommand.getArgumentTypes();

    if ((argumentTypes == null) || (argumentTypes.length == 0)) {
      return aiArgumentMap;
    }

    for (int i = 0; i < argumentTypes.length; i++) {
      int index = i + 1;
      String argName = actualCommand[index].replaceAll(PATTERN_ARGUMENT_START, StringUtils.EMPTY)
          .replaceAll(PATTERN_ARGUMENT_END, StringUtils.EMPTY);
      String commandArg = commandAndArgs[index];

      aiArgumentMap.addArgument(argName, commandArg);
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

  private boolean isArgumentType(ArgumentType argumentType, String argVal) {
    return argumentType.checkArgument(argVal);
  }
}