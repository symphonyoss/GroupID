package org.symphonyoss.symphony.bots.helpdesk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by nick.tarsillo on 10/9/17.
 * A help desk bot configuration.
 */
@Configuration
@ConfigurationProperties
public class HelpDeskBotConfig {

  private String email;

  private String sessionAuthUrl;

  private String keyAuthUrl;

  private String agentUrl;

  private String podUrl;

  private String trustStoreFile;

  private String trustStorePassword;

  private String keyStoreFile;

  private String keyStorePassword;

  private String groupId;

  private String agentStreamId;

  private String claimMessageTemplate;

  private String claimEntityTemplate;

  private String memberServiceUrl;

  private String ticketServiceUrl;

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

  private String ticketCreationMessage;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
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

  public String getClaimMessageTemplate() {
    return claimMessageTemplate;
  }

  public void setClaimMessageTemplate(String claimMessageTemplate) {
    this.claimMessageTemplate = claimMessageTemplate;
  }

  public String getClaimEntityTemplate() {
    return claimEntityTemplate;
  }

  public void setClaimEntityTemplate(String claimEntityTemplate) {
    this.claimEntityTemplate = claimEntityTemplate;
  }

  public String getMemberServiceUrl() {
    return memberServiceUrl;
  }

  public void setMemberServiceUrl(String memberServiceUrl) {
    this.memberServiceUrl = memberServiceUrl;
  }

  public String getTicketServiceUrl() {
    return ticketServiceUrl;
  }

  public void setTicketServiceUrl(String ticketServiceUrl) {
    this.ticketServiceUrl = ticketServiceUrl;
  }

  public String getMakerCheckerMessageTemplate() {
    return makerCheckerMessageTemplate;
  }

  public void setMakerCheckerMessageTemplate(String makerCheckerMessageTemplate) {
    this.makerCheckerMessageTemplate = makerCheckerMessageTemplate;
  }

  public String getMakerCheckerEntityTemplate() {
    return makerCheckerEntityTemplate;
  }

  public void setMakerCheckerEntityTemplate(String makerCheckerEntityTemplate) {
    this.makerCheckerEntityTemplate = makerCheckerEntityTemplate;
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

  public String getTicketCreationMessage() {
    return ticketCreationMessage;
  }

  public void setTicketCreationMessage(String ticketCreationMessage) {
    this.ticketCreationMessage = ticketCreationMessage;
  }
}
