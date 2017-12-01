package org.symphonyoss.symphony.bots.helpdesk.messageproxy.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by robson on 20/11/17.
 */
@Configuration
@ConfigurationProperties(prefix = "helpdesk-bot")
public class HelpDeskServiceInfo {

  private String host;

  private Integer port;

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public String getUrl(String context) {
    if (StringUtils.isEmpty(host)) {
      return StringUtils.EMPTY;
    }

    if (port != null) {
      return String.format("https://%s:%d/%s", host, port, context);
    } else {
      return String.format("https://%s/%s", host, context);
    }
  }

}
