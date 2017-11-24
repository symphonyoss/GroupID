package org.symphonyoss.symphony.bots.ai.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.symphonyoss.symphony.bots.ai.model.AiArgumentMap;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nick.tarsillo on 11/21/17.
 */
public class SymphonyAiCommandInterpreter extends AiCommandInterpreterImpl {
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final String MENTION = "@";
  private static final String MENTION_START = "mention";
  private static final String MENTION_ENTITY_START = "<span class=\"entity\"";
  private static final String MENTION_ENTITY_END = "</span>";
  private static final String USER_ID = "id";
  private static final String VALUE = "value";

  private SymUser aiSymUser;

  public SymphonyAiCommandInterpreter(SymUser aiSymUser) {
    this.aiSymUser = aiSymUser;
  }

  @Override
  public boolean isCommand(AiCommand aiCommand, AiMessage command, String commandPrefix) {
    AiMessage aiMessage = parseMentions(command);

    return super.isCommand(aiCommand, aiMessage, parsePrefix(commandPrefix));
  }

  @Override
  public AiArgumentMap readCommandArguments(AiCommand aiCommand, AiMessage command,
      String commandPrefix) {
    AiMessage aiMessage = parseMentions(command);

    return super.readCommandArguments(aiCommand, aiMessage, parsePrefix(commandPrefix));
  }

  @Override
  public boolean hasPrefix(AiMessage command, String commandPrefix) {
    AiMessage aiMessage = parseMentions(command);

    return super.hasPrefix(aiMessage, parsePrefix(commandPrefix));
  }

  private AiMessage parseMentions(AiMessage command) {
    try {
      SymphonyAiMessage aiMessage = (SymphonyAiMessage) command;
      if (StringUtils.isNotBlank(aiMessage.getEntityData())) {
        JsonNode jsonNode = objectMapper.readTree(aiMessage.getEntityData());
        Set<String> uids = new HashSet<>();

        int mention = 1;
        while (jsonNode.get(MENTION_START + mention) != null) {
          jsonNode = jsonNode.get(MENTION_START + mention);
          uids.add(jsonNode.get(USER_ID).get(0).get(VALUE).asText());
          mention++;
        }

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

    return command;
  }

  private String parsePrefix(String commandPrefix) {
    if(commandPrefix != null && commandPrefix.equals(MENTION)) {
      return MENTION + aiSymUser.getId();
    } else {
      return commandPrefix;
    }
  }

}
