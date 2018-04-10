package org.symphonyoss.symphony.bots.ai.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.common.AiConstants;
import org.symphonyoss.symphony.bots.ai.model.AiArgumentMap;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.ai.model.ArgumentType;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Command interpreter for Symphony supported commands.
 * <p>
 * Created by nick.tarsillo on 11/21/17.
 */
public class SymphonyAiCommandInterpreter implements AiCommandInterpreter {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private static final String PATTERN_ARGUMENT_START = "\\{";

  private static final String PATTERN_ARGUMENT_END = "\\}";

  private static final String MENTION = "@";

  private static final String MENTION_START = "mention";

  private static final String MENTION_TYPE = "com.symphony.user.mention";

  private static final String MENTION_ENTITY_START = "<span class=\"entity\"";

  private static final String MENTION_ENTITY_END = "</span>";

  private static final String TYPE = "type";

  private static final String USER_ID = "id";

  private static final String VALUE = "value";

  private final SymphonyClient symphonyClient;

  public SymphonyAiCommandInterpreter(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
  }

  @Override
  public boolean isCommand(AiCommand aiCommand, AiMessage command, String commandPrefix) {
    AiMessage aiMessage = parseMentions(command);
    String prefixNormalized = parsePrefix(commandPrefix);

    String commandNormalized = aiMessage.getAiMessage().toLowerCase();
    String aiCommandNormalized = aiCommand.getCommand().toLowerCase();

    String potentialCommand = commandNormalized.substring(prefixNormalized.length()).trim();

    if (StringUtils.isEmpty(potentialCommand)) {
      return false;
    }

    String actualCommand = aiCommandNormalized.split(" ")[0];
    String[] commandAndArgs = potentialCommand.split(" ");

    ArgumentType[] argumentTypes = aiCommand.getArgumentTypes();

    if ((argumentTypes != null) && (commandAndArgs.length <= aiCommand.getArgumentTypes().length)) {
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
      int argumentIndex = i + 1;
      if (argumentIndex <= potentialCommand.length - 1) {
        String commandArg = potentialCommand[argumentIndex];

        if (!isValidArgument(command, commandArg, i)) {
          return false;
        }
      }
    }

    return true;
  }

  private boolean isValidArgument(AiCommand command, String commandArg, int index) {
    return isArgumentType(command.getArgumentTypes()[index], commandArg);
  }

  private boolean isArgumentType(ArgumentType argumentType, String argVal) {
    return argumentType.checkArgument(argVal);
  }

  @Override
  public boolean hasPrefix(AiMessage command, String commandPrefix) {
    AiMessage aiMessage = parseMentions(command);
    String prefixNormalized = parsePrefix(commandPrefix);

    return StringUtils.isNotBlank(commandPrefix) && aiMessage.getAiMessage()
        .startsWith(prefixNormalized);
  }

  @Override
  public AiArgumentMap readCommandArguments(AiCommand aiCommand, AiMessage command,
      String commandPrefix) {
    AiMessage aiMessage = parseMentions(command);
    String prefixNormalized = parsePrefix(commandPrefix);

    AiArgumentMap aiArgumentMap = new AiArgumentMap();

    String potentialCommand = aiMessage.getAiMessage().substring(prefixNormalized.length()).trim();

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

    for (String potentialArg : actualCommand) {
      if (potentialArg.contains(AiConstants.ARGUMENT_START_CHAR) &&
          potentialArg.contains(AiConstants.ARGUMENT_END_CHAR)) {
        command = command.replace(potentialArg, "");
      }
    }

    return command.trim();
  }

  private AiMessage parseMentions(AiMessage aiMessage) {
    try {
      if (StringUtils.isNotBlank(aiMessage.getEntityData())
          && StringUtils.isNotBlank(aiMessage.getMessageData())) {
        Set<String> uids = getUIds(aiMessage);

        String parseMention = aiMessage.getMessageData();
        for (String uid : uids) {
          String replace = parseMention.substring(parseMention.indexOf(MENTION_ENTITY_START),
              parseMention.indexOf(MENTION_ENTITY_END) + MENTION_ENTITY_END.length());
          parseMention = parseMention.replace(replace, MENTION + uid);
        }

        SymMessage symMessage = new SymMessage();
        symMessage.setMessage(parseMention);

        AiMessage symphonyAiMessage = new AiMessage(symMessage.getMessageText());
        symphonyAiMessage.setEntityData(aiMessage.getEntityData());
        symphonyAiMessage.setFromUserId(aiMessage.getFromUserId());
        symphonyAiMessage.setMessageData(parseMention);
        symphonyAiMessage.setMessageId(aiMessage.getMessageId());
        symphonyAiMessage.setStreamId(aiMessage.getStreamId());
        symphonyAiMessage.setTimestamp(aiMessage.getTimestamp());

        return symphonyAiMessage;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return aiMessage;
  }

  private Set<String> getUIds(AiMessage aiMessage) throws IOException {
    JsonNode jsonNode = OBJECT_MAPPER.readTree(aiMessage.getEntityData());
    Set<String> uids = new HashSet<>();

    int mention = 1;
    if(jsonNode.get(MENTION_START + mention) != null) {
      while (jsonNode.get(MENTION_START + mention) != null) {
        JsonNode mentionNode = jsonNode.get(MENTION_START + mention);
        uids.add(mentionNode.get(USER_ID).get(0).get(VALUE).asText());
        mention++;
      }
    } else {
      int entity = 0;
      while (jsonNode.get(String.valueOf(entity)) != null) {
        JsonNode mentionNode = jsonNode.get(String.valueOf(entity));
        if (mentionNode.get(TYPE).asText().equals(MENTION_TYPE)) {
          uids.add(mentionNode.get(USER_ID).get(0).get(VALUE).asText());
        }
        entity++;
      }
    }
    return uids;
  }

  private String parsePrefix(String commandPrefix) {
    if (commandPrefix != null && commandPrefix.equals(MENTION)) {
      SymUser aiSymUser = symphonyClient.getLocalUser();
      return MENTION + aiSymUser.getId();
    } else {
      return commandPrefix;
    }
  }

}
