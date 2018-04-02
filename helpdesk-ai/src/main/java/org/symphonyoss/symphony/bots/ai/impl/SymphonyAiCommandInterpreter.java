package org.symphonyoss.symphony.bots.ai.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.common.AiConstants;
import org.symphonyoss.symphony.bots.ai.model.AiArgumentMap;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
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

  private static final ObjectMapper objectMapper = new ObjectMapper();

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

  private SymUser aiSymUser;

  public SymphonyAiCommandInterpreter(SymUser aiSymUser) {
    this.aiSymUser = aiSymUser;
  }

  @Override
  public boolean isCommand(AiCommand aiCommand, SymphonyAiMessage command, String commandPrefix) {
    SymphonyAiMessage aiMessage = parseMentions(command);
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
  public boolean hasPrefix(SymphonyAiMessage command, String commandPrefix) {
    SymphonyAiMessage aiMessage = parseMentions(command);
    String prefixNormalized = parsePrefix(commandPrefix);

    return StringUtils.isNotBlank(commandPrefix) && aiMessage.getAiMessage()
        .startsWith(prefixNormalized);
  }

  @Override
  public AiArgumentMap readCommandArguments(AiCommand aiCommand, SymphonyAiMessage command,
      String commandPrefix) {
    SymphonyAiMessage aiMessage = parseMentions(command);
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

  private SymphonyAiMessage parseMentions(SymphonyAiMessage aiMessage) {
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

        SymphonyAiMessage symphonyAiMessage = new SymphonyAiMessage(symMessage.getMessageText());
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

  private Set<String> getUIds(SymphonyAiMessage aiMessage) throws IOException {
    JsonNode jsonNode = objectMapper.readTree(aiMessage.getEntityData());
    Element elementMessageML = Jsoup.parse(aiMessage.getMessageData()).select("div").first();
    Set<String> uids = new HashSet<>();

    if(elementMessageML.getElementsByAttributeValue("class", "wysiwyg").size() > 0) {
      int entity = 0;
      while (jsonNode.get(String.valueOf(entity)) != null) {
        jsonNode = jsonNode.get(String.valueOf(entity));
        if (jsonNode.get(TYPE).asText().equals(MENTION_TYPE)) {
          uids.add(jsonNode.get(USER_ID).get(0).get(VALUE).asText());
        }
        entity++;
      }
    } else {
      int mention = 1;
      while (jsonNode.get(MENTION_START + mention) != null) {
        jsonNode = jsonNode.get(MENTION_START + mention);
        uids.add(jsonNode.get(USER_ID).get(0).get(VALUE).asText());
        mention++;
      }
    }
    return uids;
  }

  private String parsePrefix(String commandPrefix) {
    if (commandPrefix != null && commandPrefix.equals(MENTION)) {
      return MENTION + aiSymUser.getId();
    } else {
      return commandPrefix;
    }
  }

}
