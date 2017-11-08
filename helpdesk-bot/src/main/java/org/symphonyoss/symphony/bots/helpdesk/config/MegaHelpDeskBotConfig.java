package org.symphonyoss.symphony.bots.helpdesk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

/**
 * Created by nick.tarsillo on 11/6/17.
 *
 * This will be used later when it comes time to create the mega bot.
 */
@Configuration
@PropertySource("file:${app.home}/megahelpdeskbot.properties")
@ConfigurationProperties(prefix = "helpdesk")
public class MegaHelpDeskBotConfig {
  private List<HelpDeskBotConfig> helpDeskBotConfigList;
  private List<String> initHelpDeskBots;
  private HelpDeskBotConfig defaultHelpDeskBotConfig;

  public HelpDeskBotConfig getConfig(String groupId) {
    for(HelpDeskBotConfig helpDeskBotConfig: helpDeskBotConfigList) {
      if(helpDeskBotConfig.getGroupId().equals(groupId)) {
        return helpDeskBotConfig;
      }
    }

    return null;
  }

  public List<HelpDeskBotConfig> getHelpDeskBotConfigList() {
    return helpDeskBotConfigList;
  }

  public void setHelpDeskBotConfigList(
      List<HelpDeskBotConfig> helpDeskBotConfigList) {
    this.helpDeskBotConfigList = helpDeskBotConfigList;
  }

  public HelpDeskBotConfig getDefaultHelpDeskBotConfig() {
    return defaultHelpDeskBotConfig;
  }

  public void setDefaultHelpDeskBotConfig(
      HelpDeskBotConfig defaultHelpDeskBotConfig) {
    this.defaultHelpDeskBotConfig = defaultHelpDeskBotConfig;
  }

  public List<String> getInitHelpDeskBots() {
    return initHelpDeskBots;
  }

  public void setInitHelpDeskBots(List<String> initHelpDeskBots) {
    this.initHelpDeskBots = initHelpDeskBots;
  }
}
