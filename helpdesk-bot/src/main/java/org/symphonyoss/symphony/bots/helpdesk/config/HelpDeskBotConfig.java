package org.symphonyoss.symphony.bots.helpdesk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.bots.utility.file.FileUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nick.tarsillo on 10/9/17.
 * A help desk bot configuration.
 */
public class HelpDeskBotConfig {
  private static final Logger LOG = LoggerFactory.getLogger(HelpDeskBotConfig.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final Map<String, HelpDeskBotConfig> configMap = new HashMap<>();

  private String groupId;
  private String makerCheckerMessageTemplate;
  private String makerCheckerEntityTemplate;
  private String aiServicePrefix;
  private String aiDefaultPrefix;
  private String closeTicketCommand;
  private String acceptTicketCommand;
  private String addMemberCommand;
  private String acceptTicketAgentSuccessResponse;
  private String acceptTicketClientSuccessResponse;
  private String closeTicketSuccessResponse;
  private String addMemberAgentSuccessResponse;
  private String addMemberClientSuccessResponse;

  public HelpDeskBotConfig(String groupId) {
    this.groupId = groupId;
  }

  public String getMakerCheckerMessageTemplate() {
    if (StringUtils.isBlank(makerCheckerMessageTemplate)) {
      try {
        return FileUtil.readFile(System.getProperty(DefaultBotConfig.TEMPLATE_DIR) +
                System.getProperty(DefaultBotConfig.MAKERCHECKER_MESSAGE_TEMPLATE_NAME));
      } catch (IOException e) {
        LOG.info("Loading maker checker message template failed.");
      }

      return null;
    } else {
      return makerCheckerMessageTemplate;
    }
  }

  public void setMakerCheckerMessageTemplate(String makerCheckerMessageTemplate) {
    this.makerCheckerMessageTemplate = makerCheckerMessageTemplate;
    saveConfig();
  }

  public String getMakerCheckerEntityTemplate() {
    if (StringUtils.isBlank(makerCheckerEntityTemplate)) {
      try {
        return FileUtil.readFile(System.getProperty(DefaultBotConfig.TEMPLATE_DIR) +
                System.getProperty(DefaultBotConfig.MAKERCHECKER_ENTITY_TEMPLATE_NAME));
      } catch (IOException e) {
        LOG.info("Loading maker checker entity template failed.");
      }

      return null;
    } else {
      return makerCheckerEntityTemplate;
    }
  }

  public void setMakerCheckerEntityTemplate(String makerCheckerEntityTemplate) {
    this.makerCheckerEntityTemplate = makerCheckerEntityTemplate;
    saveConfig();
  }

  public String getAiServicePrefix() {
    return aiServicePrefix;
  }

  public void setAiServicePrefix(String aiServicePrefix) {
    this.aiServicePrefix = aiServicePrefix;
    saveConfig();
  }

  public String getAiDefaultPrefix() {
    if(StringUtils.isBlank(aiDefaultPrefix)) {
      return System.getProperty(DefaultBotConfig.AI_DEFAULT_PREFIX);
    } else {
      return aiDefaultPrefix;
    }
  }

  public void setAiDefaultPrefix(String aiDefaultPrefix) {
    this.aiDefaultPrefix = aiDefaultPrefix;
    saveConfig();
  }

  public String getCloseTicketCommand() {
    if(StringUtils.isBlank(closeTicketCommand)) {
      return System.getProperty(DefaultBotConfig.AI_SERVICE_PREFIX);
    } else {
      return closeTicketCommand;
    }
  }

  public void setCloseTicketCommand(String closeTicketCommand) {
    this.closeTicketCommand = closeTicketCommand;
  }

  public String getAcceptTicketCommand() {
    return acceptTicketCommand;
  }

  public void setAcceptTicketCommand(String acceptTicketCommand) {
    this.acceptTicketCommand = acceptTicketCommand;
  }

  public String getCloseTicketSuccessResponse() {
    if(StringUtils.isBlank(closeTicketSuccessResponse)) {
      return System.getProperty(DefaultBotConfig.AI_CLOSE_TICKET_RESPONSE);
    } else {
      return closeTicketSuccessResponse;
    }
  }

  public void setCloseTicketSuccessResponse(String closeTicketSuccessResponse) {
    this.closeTicketSuccessResponse = closeTicketSuccessResponse;
  }

  public String getAddMemberCommand() {
    return addMemberCommand;
  }

  public void setAddMemberCommand(String addMemberCommand) {
    this.addMemberCommand = addMemberCommand;
  }

  public String getAddMemberAgentSuccessResponse() {
    return addMemberAgentSuccessResponse;
  }

  public void setAddMemberAgentSuccessResponse(String addMemberAgentSuccessResponse) {
    this.addMemberAgentSuccessResponse = addMemberAgentSuccessResponse;
  }

  public String getAddMemberClientSuccessResponse() {
    return addMemberClientSuccessResponse;
  }

  public void setAddMemberClientSuccessResponse(String addMemberClientSuccessResponse) {
    this.addMemberClientSuccessResponse = addMemberClientSuccessResponse;
  }

  public String getAcceptTicketClientSuccessResponse() {
    if(StringUtils.isBlank(acceptTicketClientSuccessResponse)) {
      return System.getProperty(DefaultBotConfig.AI_CLIENT_SERVICE_NOTIFICATION_RESPONSE);
    } else {
      return acceptTicketClientSuccessResponse;
    }
  }

  public void setAcceptTicketClientSuccessResponse(String acceptTicketClientSuccessResponse) {
    this.acceptTicketClientSuccessResponse = acceptTicketClientSuccessResponse;
  }

  public String getAcceptTicketAgentSuccessResponse() {
    if(StringUtils.isBlank(acceptTicketAgentSuccessResponse)) {
      return System.getProperty(DefaultBotConfig.AI_AGENT_SERVICE_NOTIFICATION_RESPONSE);
    } else {
      return acceptTicketAgentSuccessResponse;
    }
  }

  public void setAcceptTicketAgentSuccessResponse(String acceptTicketAgentSuccessResponse) {
    this.acceptTicketAgentSuccessResponse = acceptTicketAgentSuccessResponse;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
    saveConfig();
  }

  public static HelpDeskBotConfig getConfig(String groupId) {
    if(configMap.containsKey(groupId)) {
      return configMap.get(groupId);
    }

    HelpDeskBotConfig helpDeskBotConfig = null;
    try {
      helpDeskBotConfig = objectMapper.readValue(
          FileUtil.readFile(System.getProperty(DefaultBotConfig.HELPDESK_CONFIG_DIR) + "/" + groupId),
          HelpDeskBotConfig.class);
      configMap.put(groupId, helpDeskBotConfig);
    } catch (IOException e) {
      LOG.info("Could not load config for: " + groupId);
    }

    if(helpDeskBotConfig == null) {
      helpDeskBotConfig = new HelpDeskBotConfig(groupId);
      configMap.put(groupId, helpDeskBotConfig);
    }

    return helpDeskBotConfig;
  }

  private void saveConfig() {
    try {
      FileUtil.writeFile(objectMapper.writeValueAsString(this),
          System.getProperty(DefaultBotConfig.HELPDESK_CONFIG_DIR) + "/" + groupId);
    } catch (IOException e) {
      LOG.error("Saving config failed for: " + groupId);
    }
  }
}
