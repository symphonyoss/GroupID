package org.symphonyoss.symphony.bots.helpdesk.messageproxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.symphonyoss.symphony.bots.utility.config.ServiceInfo;

/**
 * Created by robson on 20/11/17.
 */
@Configuration
@ConfigurationProperties(prefix = "helpdesk-service")
public class HelpDeskServiceInfo extends ServiceInfo {

  private static final String HELPDESK_SERVICE_CONTEXT = "helpdesk";

  public String getUrl() {
    return getUrl(HELPDESK_SERVICE_CONTEXT);
  }

}
