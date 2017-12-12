package org.symphonyoss.symphony.bots.helpdesk.bot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.symphonyoss.symphony.bots.utility.config.ServiceInfo;

/**
 * Created by nick.tarsillo on 10/9/17.
 * A help desk bot configuration.
 */
@Configuration
@ConfigurationProperties
public class HelpDeskBotConfig {

  private String email;

  private ServiceInfo sessionAuth;

  private ServiceInfo keyAuth;

  private ServiceInfo agent;

  private ServiceInfo pod;

  private ServiceInfo helpdeskBot;

  private ServiceInfo helpdeskService;

  private AuthenticationConfig authentication;

  private String groupId;

  private String defaultAgentEmail;

  private String agentStreamId;

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

  private String claimEntityHeader;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public ServiceInfo getSessionAuth() {
    return sessionAuth;
  }

  public void setSessionAuth(ServiceInfo sessionAuth) {
    this.sessionAuth = sessionAuth;
  }

  public String getSessionAuthUrl() {
    if (this.sessionAuth == null) {
      throw new IllegalStateException("Unknown session auth address");
    }

    return this.sessionAuth.getUrl("sessionauth");
  }

  public ServiceInfo getKeyAuth() {
    return keyAuth;
  }

  public void setKeyAuth(ServiceInfo keyAuth) {
    this.keyAuth = keyAuth;
  }

  public String getKeyAuthUrl() {
    if (this.keyAuth == null) {
      throw new IllegalStateException("Unknown key auth address");
    }

    return this.keyAuth.getUrl("keyauth");
  }

  public ServiceInfo getAgent() {
    return agent;
  }

  public void setAgent(ServiceInfo agent) {
    this.agent = agent;
  }

  public ServiceInfo getPod() {
    return pod;
  }

  public void setPod(ServiceInfo pod) {
    this.pod = pod;
  }

  public String getAgentUrl() {
    if (this.agent == null) {
      throw new IllegalStateException("Unknown agent address");
    }

    return this.agent.getUrl("agent");
  }

  public String getPodUrl() {
    if (this.pod == null) {
      throw new IllegalStateException("Unknown POD address");
    }

    return this.pod.getUrl("pod");
  }

  public ServiceInfo getHelpdeskBot() {
    return helpdeskBot;
  }

  public void setHelpdeskBot(ServiceInfo helpdeskBot) {
    this.helpdeskBot = helpdeskBot;
  }

  public ServiceInfo getHelpdeskService() {
    return helpdeskService;
  }

  public void setHelpdeskService(ServiceInfo helpdeskService) {
    this.helpdeskService = helpdeskService;
  }

  public String getHelpDeskBotUrl() {
    if (this.helpdeskBot == null) {
      throw new IllegalStateException("Unknown HelpDesk bot address");
    }

    return this.helpdeskBot.getUrl("helpdesk-bot");
  }

  public String getHelpDeskServiceUrl() {
    if (this.helpdeskService == null) {
      throw new IllegalStateException("Unknown HelpDesk service address");
    }

    return this.helpdeskService.getUrl("helpdesk");
  }

  public AuthenticationConfig getAuthentication() {
    return authentication;
  }

  public void setAuthentication(AuthenticationConfig authentication) {
    this.authentication = authentication;
  }

  public String getTrustStoreFile() {
    if (this.authentication == null) {
      throw new IllegalStateException("Unknown authentication config");
    }

    return this.authentication.getTruststoreFile();
  }

  public String getTrustStorePassword() {
    if (this.authentication == null) {
      throw new IllegalStateException("Unknown authentication config");
    }

    return this.authentication.getTruststorePassword();
  }

  public String getKeyStoreFile() {
    if (this.authentication == null) {
      throw new IllegalStateException("Unknown authentication config");
    }

    return this.authentication.getKeystoreFile();
  }

  public String getKeyStorePassword() {
    if (this.authentication == null) {
      throw new IllegalStateException("Unknown authentication config");
    }

    return this.authentication.getKeystorePassword();
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getAgentStreamId() {
    return agentStreamId;
  }

  public void setAgentStreamId(String agentStreamId) {
    this.agentStreamId = agentStreamId;
  }

  public String getAiServicePrefix() {
    return aiServicePrefix;
  }

  public void setAiServicePrefix(String aiServicePrefix) {
    this.aiServicePrefix = aiServicePrefix;
  }

  public String getAiDefaultPrefix() {
    return aiDefaultPrefix;
  }

  public void setAiDefaultPrefix(String aiDefaultPrefix) {
    this.aiDefaultPrefix = aiDefaultPrefix;
  }

  public String getCloseTicketCommand() {
    return closeTicketCommand;
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

  public String getAddMemberCommand() {
    return addMemberCommand;
  }

  public void setAddMemberCommand(String addMemberCommand) {
    this.addMemberCommand = addMemberCommand;
  }

  public String getAcceptTicketAgentSuccessResponse() {
    return acceptTicketAgentSuccessResponse;
  }

  public void setAcceptTicketAgentSuccessResponse(String acceptTicketAgentSuccessResponse) {
    this.acceptTicketAgentSuccessResponse = acceptTicketAgentSuccessResponse;
  }

  public String getAcceptTicketClientSuccessResponse() {
    return acceptTicketClientSuccessResponse;
  }

  public void setAcceptTicketClientSuccessResponse(String acceptTicketClientSuccessResponse) {
    this.acceptTicketClientSuccessResponse = acceptTicketClientSuccessResponse;
  }

  public String getCloseTicketSuccessResponse() {
    return closeTicketSuccessResponse;
  }

  public void setCloseTicketSuccessResponse(String closeTicketSuccessResponse) {
    this.closeTicketSuccessResponse = closeTicketSuccessResponse;
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

  public String getDefaultAgentEmail() {
    return defaultAgentEmail;
  }

  public void setDefaultAgentEmail(String defaultAgentEmail) {
    this.defaultAgentEmail = defaultAgentEmail;
  }

  public String getClaimEntityHeader() {
    return claimEntityHeader;
  }

  public void setClaimEntityHeader(String claimEntityHeader) {
    this.claimEntityHeader = claimEntityHeader;
  }

}
