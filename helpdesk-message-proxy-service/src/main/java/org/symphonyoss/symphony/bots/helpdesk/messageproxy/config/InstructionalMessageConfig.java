package org.symphonyoss.symphony.bots.helpdesk.messageproxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by nick.tarsillo on 12/8/17.
 */
@Configuration
@ConfigurationProperties(prefix = "instructional-ticket")
public class InstructionalMessageConfig {
  private String message;
  private String command;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }
}
