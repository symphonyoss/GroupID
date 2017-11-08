package org.symphonyoss.symphony.bots.helpdesk.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.PathResource;

/**
 * Created by nick.tarsillo on 11/6/17.
 */
@Configuration
@ConfigurationProperties(prefix = "helpdesk.service")
public class HelpDeskServiceConfig {
  public static final String DATABASE_DRIVER = "${helpdesk.service.databaseDriver}";
  public static final String DATABASE_URL = "${helpdesk.service.databaseUrl}";
  public static final String DATABASE_USER = "${helpdesk.service.databaseUser}";
  public static final String DATABASE_PASSWORD = "${helpdesk.service.databasePassword}";
  public static final String MEMBERSHIP_TABLE_NAME = "${helpdesk.service.membershipTableName}";
  public static final String TICKET_TABLE_NAME = "${helpdesk.service.ticketTableName}";

  @Value(DATABASE_DRIVER) private String databaseDriver;
  @Value(DATABASE_URL) private String databaseUrl;
  @Value(DATABASE_USER) private String databaseUser;
  @Value(DATABASE_PASSWORD) private String databasePassword;
  @Value(MEMBERSHIP_TABLE_NAME) private String membershipTableName;
  @Value(TICKET_TABLE_NAME) private String ticketTableName;

  @Bean
  public static PropertySourcesPlaceholderConfigurer properties() {
    PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
    YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
    yaml.setResources(new PathResource(System.getProperty("app.home") + "/helpdeskservice.yaml"));
    propertySourcesPlaceholderConfigurer.setProperties(yaml.getObject());
    return propertySourcesPlaceholderConfigurer;
  }

  @Override
  public String toString() {
    String config =   "databaseDriver:" + databaseDriver + "\n" +
    "databaseUrl:" + databaseUrl + "\n" +
    "databaseUser:" + databaseUser + "\n" +
    "databasePassword:" + databasePassword + "\n" +
    "membershipTableName:" + membershipTableName + "\n" +
    "ticketTableName:" + ticketTableName + "\n";

    return config;
  }

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
