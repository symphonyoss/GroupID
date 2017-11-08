package org.symphonyoss.symphony.bots.helpdesk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.PathResource;

/**
 * Created by nick.tarsillo on 10/9/17.
 * A help desk bot configuration.
 */
@Configuration
@ConfigurationProperties
public class HelpDeskBotConfig {
  public static final String GROUP_ID = "${groupId}";
  public static final String TICKET_SERVICE_URL = "${ticketServiceUrl}";
  public static final String EMAIL = "${email}";
  public static final String SESSION_AUTH_URL = "${sessionAuthUrl}";
  public static final String KEY_AUTH_URL = "${keyAuthUrl}";
  public static final String AGENT_URL = "${agentUrl}";
  public static final String POD_URL = "${podUrl}";
  public static final String TRUST_STORE_FILE = "${trustStoreFile}";
  public static final String TRUST_STORE_PASSWORD = "${trustStorePassword}";
  public static final String KEY_STORE_FILE = "${keyStoreFile}";
  public static final String KEY_STORE_PASSWORD = "${keyStorePassword}";
  public static final String AGENT_STREAM_ID = "${agentStreamId}";
  public static final String CLAIM_MESSAGE_TEMPLATE_DATA = "${claimMessageTemplate}";
  public static final String CLAIM_ENTITY_TEMPLATE_DATA = "${claimEntityTemplate}";
  public static final String MEMBER_SERVICE_URL = "${memberServiceUrl}";
  public static final String MAKER_CHECKER_MESSAGE_TEMPLATE = "${makerCheckerMessageTemplate}";
  public static final String MAKER_CHECKER_ENTITY_TEMPLATE = "${makerCheckerEntityTemplate}";
  public static final String AI_SERVICE_PREFIX = "${aiServicePrefix}";
  public static final String AI_DEFAULT_PREFIX = "${aiDefaultPrefix}";
  public static final String CLOSE_TICKET_COMMAND = "${closeTicketCommand}";
  public static final String ACCEPT_TICKET_COMMAND = "${acceptTicketCommand}";
  public static final String ADD_MEMBER_COMMAND = "${addMemberCommand}";
  public static final String ACCEPT_TICKET_AGENT_RESPONSE = "${acceptTicketAgentSuccessResponse}";
  public static final String ACCEPT_TICKET_CLIENT_RESPONSE = "${acceptTicketClientSuccessResponse}";
  public static final String CLOSE_TICKET_RESPONSE = "${closeTicketSuccessResponse}";
  public static final String ADD_MEMBER_AGENT_RESPONSE = "${addMemberAgentSuccessResponse}";
  public static final String ADD_MEMBER_CLIENT_RESPONSE = "${addMemberClientSuccessResponse}";

  @Value(EMAIL) private String email;
  @Value(SESSION_AUTH_URL) private String sessionAuthUrl;
  @Value(KEY_AUTH_URL) private String keyAuthUrl;
  @Value(AGENT_URL) private String agentUrl;
  @Value(POD_URL) private String podUrl;
  @Value(TRUST_STORE_FILE) private String trustStoreFile;
  @Value(TRUST_STORE_PASSWORD) private String trustStorePassword;
  @Value(KEY_STORE_FILE) private String keyStoreFile;
  @Value(KEY_STORE_PASSWORD) private String keyStorePassword;

  @Value(GROUP_ID) private String groupId;
  @Value(AGENT_STREAM_ID) private String agentStreamId;
  @Value(CLAIM_MESSAGE_TEMPLATE_DATA) private String claimMessageTemplate;
  @Value(CLAIM_ENTITY_TEMPLATE_DATA) private String claimEntityTemplate;
  @Value(MEMBER_SERVICE_URL) private String memberServiceUrl;
  @Value(TICKET_SERVICE_URL) private String ticketServiceUrl;
  @Value(MAKER_CHECKER_MESSAGE_TEMPLATE) private String makerCheckerMessageTemplate;
  @Value(MAKER_CHECKER_ENTITY_TEMPLATE) private String makerCheckerEntityTemplate;
  @Value(AI_SERVICE_PREFIX) private String aiServicePrefix;
  @Value(AI_DEFAULT_PREFIX) private String aiDefaultPrefix;
  @Value(CLOSE_TICKET_COMMAND) private String closeTicketCommand;
  @Value(ACCEPT_TICKET_COMMAND) private String acceptTicketCommand;
  @Value(ADD_MEMBER_COMMAND) private String addMemberCommand;
  @Value(ACCEPT_TICKET_AGENT_RESPONSE) private String acceptTicketAgentSuccessResponse;
  @Value(ACCEPT_TICKET_CLIENT_RESPONSE) private String acceptTicketClientSuccessResponse;
  @Value(CLOSE_TICKET_RESPONSE) private String closeTicketSuccessResponse;
  @Value(ADD_MEMBER_AGENT_RESPONSE) private String addMemberAgentSuccessResponse;
  @Value(ADD_MEMBER_CLIENT_RESPONSE) private String addMemberClientSuccessResponse;

  @Bean
  public static PropertySourcesPlaceholderConfigurer properties() {
    PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
    YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
    yaml.setResources(new PathResource(System.getProperty("app.home") + "/helpdeskbot.yaml"));
    propertySourcesPlaceholderConfigurer.setProperties(yaml.getObject());
    return propertySourcesPlaceholderConfigurer;
  }

  @Override
  public String toString() {
    String propFile = "" +
    "email:" + email + "\n" +
    "sessionAuthUrl:" + sessionAuthUrl + "\n" +
    "keyAuthUrl:" + keyAuthUrl + "\n" +
    "agentUrl:" + agentUrl + "\n" +
    "podUrl:" + podUrl + "\n" +
    "trustStoreFile:" + trustStoreFile + "\n" +
    "trustStorePassword:" + trustStorePassword + "\n" +
    "keyStoreFile:" + keyStoreFile + "\n" +
    "keyStorePassword:" + keyStorePassword + "\n" +

    "groupId:" + groupId + "\n" +
    "agentStreamId:" + agentStreamId + "\n" +
    "claimMessageTemplate:" + claimMessageTemplate + "\n" +
    "claimEntityTemplate:" + claimEntityTemplate + "\n" +
    "memberServiceUrl:" + memberServiceUrl + "\n" +
    "ticketServiceUrl:" + ticketServiceUrl + "\n" +
    "makerCheckerMessageTemplate:" + makerCheckerMessageTemplate + "\n" +
    "makerCheckerEntityTemplate:" + makerCheckerEntityTemplate + "\n" +
    "aiServicePrefix:" + aiServicePrefix + "\n" +
    "aiDefaultPrefix:" + aiDefaultPrefix + "\n" +
    "closeTicketCommand:" + closeTicketCommand + "\n" +
    "acceptTicketCommand:" + acceptTicketCommand + "\n" +
    "addMemberCommand:" + addMemberCommand + "\n" +
    "acceptTicketAgentSuccessResponse:" + acceptTicketAgentSuccessResponse + "\n" +
    "acceptTicketClientSuccessResponse:" + acceptTicketClientSuccessResponse + "\n" +
    "closeTicketSuccessResponse:" + closeTicketSuccessResponse + "\n" +
    "addMemberAgentSuccessResponse:" + addMemberAgentSuccessResponse + "\n" +
    "addMemberClientSuccessResponse:" + addMemberClientSuccessResponse;

    return propFile;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
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

  public String getAgentStreamId() {
    return agentStreamId;
  }

  public void setAgentStreamId(String agentStreamId) {
    this.agentStreamId = agentStreamId;
  }
}
