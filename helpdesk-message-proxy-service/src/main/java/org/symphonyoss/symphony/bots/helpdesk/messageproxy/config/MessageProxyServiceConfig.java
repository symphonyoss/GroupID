package org.symphonyoss.symphony.bots.helpdesk.messageproxy.config;

import java.util.concurrent.TimeUnit;

/**
 * Created by nick.tarsillo on 11/13/17.
 */
public class MessageProxyServiceConfig {
  private String groupId;
  private String agentStreamId;
  private String helpDeskBotHost;
  private String claimMessageTemplate;
  private String claimEntityTemplate;
  private String claimEntityHeader;
  private String idleClaimEntityHeader;
  private int agentIdleTimeValue;
  private TimeUnit agentIdleTimeUnit;
  private String ticketCreationMessage;

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

  public String getTicketCreationMessage() {
    return ticketCreationMessage;
  }

  public void setTicketCreationMessage(String ticketCreationMessage) {
    this.ticketCreationMessage = ticketCreationMessage;
  }

  public String getHelpDeskBotHost() {
    return helpDeskBotHost;
  }

  public void setHelpDeskBotHost(String helpDeskBotHost) {
    this.helpDeskBotHost = helpDeskBotHost;
  }

  public String getClaimEntityHeader() {
    return claimEntityHeader;
  }

  public void setClaimEntityHeader(String claimEntityHeader) {
    this.claimEntityHeader = claimEntityHeader;
  }

  public String getIdleClaimEntityHeader() {
    return idleClaimEntityHeader;
  }

  public void setIdleClaimEntityHeader(String idleClaimEntityHeader) {
    this.idleClaimEntityHeader = idleClaimEntityHeader;
  }

  public int getAgentIdleTimeValue() {
    return agentIdleTimeValue;
  }

  public void setAgentIdleTimeValue(int agentIdleTimeValue) {
    this.agentIdleTimeValue = agentIdleTimeValue;
  }

  public TimeUnit getAgentIdleTimeUnit() {
    return agentIdleTimeUnit;
  }

  public void setAgentIdleTimeUnit(TimeUnit agentIdleTimeUnit) {
    this.agentIdleTimeUnit = agentIdleTimeUnit;
  }
}
