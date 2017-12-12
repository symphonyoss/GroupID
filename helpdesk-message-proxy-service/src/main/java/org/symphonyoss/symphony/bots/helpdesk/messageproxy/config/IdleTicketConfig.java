package org.symphonyoss.symphony.bots.helpdesk.messageproxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Created by robson on 07/12/17.
 */
@Configuration
@ConfigurationProperties(prefix = "idle-ticket")
public class IdleTicketConfig {

  private String message;

  private Long timeout;

  private TimeUnit unit;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Long getTimeout() {
    return timeout;
  }

  public void setTimeout(Long timeout) {
    this.timeout = timeout;
  }

  public TimeUnit getUnit() {
    return unit;
  }

  public void setUnit(TimeUnit unit) {
    this.unit = unit;
  }
}
