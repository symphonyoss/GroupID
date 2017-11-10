package org.symphonyoss.symphony.bots.helpdesk.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.PathResource;

/**
 * Created by nick.tarsillo on 11/6/17.
 */
@Configuration
@ConfigurationProperties(prefix = "helpdesk.service")
public class HelpDeskServiceConfig {

  private String databaseDriver;
  private String databaseUrl;
  private String databaseUser;
  private String databasePassword;
  private String membershipTableName;
  private String ticketTableName;



  public String getDatabaseDriver() {
    return databaseDriver;
  }

  public void setDatabaseDriver(String databaseDriver) {
    this.databaseDriver = databaseDriver;
  }

  public String getDatabaseUrl() {
    return databaseUrl;
  }

  public void setDatabaseUrl(String databaseUrl) {
    this.databaseUrl = databaseUrl;
  }

  public String getDatabaseUser() {
    return databaseUser;
  }

  public void setDatabaseUser(String databaseUser) {
    this.databaseUser = databaseUser;
  }

  public String getDatabasePassword() {
    return databasePassword;
  }

  public void setDatabasePassword(String databasePassword) {
    this.databasePassword = databasePassword;
  }

  public String getMembershipTableName() {
    return membershipTableName;
  }

  public void setMembershipTableName(String membershipTableName) {
    this.membershipTableName = membershipTableName;
  }

  public String getTicketTableName() {
    return ticketTableName;
  }

  public void setTicketTableName(String ticketTableName) {
    this.ticketTableName = ticketTableName;
  }
}
