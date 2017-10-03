package com.symphony.bots.helpdesk.config;

/**
 * Created by nick.tarsillo on 9/29/17.
 * Startup config for help desk bot.
 */
public class BotStartupConfiguration {
  private String groupId;
  private String email;

  private String sessionAuthUrl;
  private String keyAuthUrl;
  private String agentUrl;
  private String podUrl;

  private String trustStoreFile;
  private String trustStorePassword;
  private String keyStoreFile;
  private String keyStorePassword;

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getSessionAuthUrl() {
    return sessionAuthUrl;
  }

  public void setSessionAuthUrl(String sessionAuthUrl) {
    this.sessionAuthUrl = sessionAuthUrl;
  }

  public String getKeyAuthUrl() {
    return keyAuthUrl;
  }

  public void setKeyAuthUrl(String keyAuthUrl) {
    this.keyAuthUrl = keyAuthUrl;
  }

  public String getKeyStoreFile() {
    return keyStoreFile;
  }

  public void setKeyStoreFile(String keyStoreFile) {
    this.keyStoreFile = keyStoreFile;
  }

  public String getKeyStorePassword() {
    return keyStorePassword;
  }

  public void setKeyStorePassword(String keyStorePassword) {
    this.keyStorePassword = keyStorePassword;
  }

  public String getTrustStoreFile() {
    return trustStoreFile;
  }

  public void setTrustStoreFile(String trustStoreFile) {
    this.trustStoreFile = trustStoreFile;
  }

  public String getTrustStorePassword() {
    return trustStorePassword;
  }

  public void setTrustStorePassword(String trustStorePassword) {
    this.trustStorePassword = trustStorePassword;
  }

  public String getAgentUrl() {
    return agentUrl;
  }

  public void setAgentUrl(String agentUrl) {
    this.agentUrl = agentUrl;
  }

  public String getPodUrl() {
    return podUrl;
  }

  public void setPodUrl(String podUrl) {
    this.podUrl = podUrl;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
