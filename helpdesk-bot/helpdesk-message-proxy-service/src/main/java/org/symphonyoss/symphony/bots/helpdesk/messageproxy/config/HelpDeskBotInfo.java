package org.symphonyoss.symphony.bots.helpdesk.messageproxy.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.symphonyoss.symphony.bots.utility.config.ServiceInfo;

/**
 * Created by robson on 20/11/17.
 */
@Configuration
@ConfigurationProperties(prefix = "helpdesk-bot")
public class HelpDeskBotInfo extends ServiceInfo {

  private static final String HELPDESK_BOT_CONTEXT = "helpdesk-bot";

  public String getUrl() {
    return getUrl(HELPDESK_BOT_CONTEXT);
  }

}
