//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.symphonyoss.symphony.bots.helpdesk.model.session;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.ai.HelpDeskAi;
import org.symphonyoss.symphony.bots.helpdesk.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.MakerCheckerService;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.MessageProxyService;
import org.symphonyoss.symphony.bots.helpdesk.service.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.client.TicketClient;

public class HelpDeskBotSession {
  private SymphonyClient symphonyClient;
  private String groupId;
  private HelpDeskAi helpDeskAi;
  private MakerCheckerService agentMakerCheckerService;
  private MakerCheckerService clientMakerCheckerService;
  private TicketClient ticketClient;
  private MembershipClient membershipClient;
  private MessageProxyService messageProxyService;
  private HelpDeskBotConfig helpDeskBotConfig;

  public SymphonyClient getSymphonyClient() {
    return this.symphonyClient;
  }

  public void setSymphonyClient(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
  }

  public String getGroupId() {
    return this.groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public MakerCheckerService getAgentMakerCheckerService() {
    return this.agentMakerCheckerService;
  }

  public void setAgentMakerCheckerService(MakerCheckerService agentMakerCheckerService) {
    this.agentMakerCheckerService = agentMakerCheckerService;
  }

  public MessageProxyService getMessageProxyService() {
    return this.messageProxyService;
  }

  public void setMessageProxyService(MessageProxyService messageProxyService) {
    this.messageProxyService = messageProxyService;
  }

  public HelpDeskAi getHelpDeskAi() {
    return this.helpDeskAi;
  }

  public void setHelpDeskAi(HelpDeskAi helpDeskAi) {
    this.helpDeskAi = helpDeskAi;
  }

  public MakerCheckerService getClientMakerCheckerService() {
    return this.clientMakerCheckerService;
  }

  public void setClientMakerCheckerService(MakerCheckerService clientMakerCheckerService) {
    this.clientMakerCheckerService = clientMakerCheckerService;
  }

  public HelpDeskBotConfig getHelpDeskBotConfig() {
    return this.helpDeskBotConfig;
  }

  public void setHelpDeskBotConfig(HelpDeskBotConfig helpDeskBotConfig) {
    this.helpDeskBotConfig = helpDeskBotConfig;
  }

  public TicketClient getTicketClient() {
    return ticketClient;
  }

  public void setTicketClient(
      TicketClient ticketClient) {
    this.ticketClient = ticketClient;
  }

  public MembershipClient getMembershipClient() {
    return membershipClient;
  }

  public void setMembershipClient(
      MembershipClient membershipClient) {
    this.membershipClient = membershipClient;
  }
}