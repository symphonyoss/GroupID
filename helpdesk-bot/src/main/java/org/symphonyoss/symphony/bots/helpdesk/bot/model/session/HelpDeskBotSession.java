//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.symphonyoss.symphony.bots.helpdesk.bot.model.session;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.services.ConnectionsEventListener;
import org.symphonyoss.symphony.bots.ai.HelpDeskAi;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.MakerCheckerService;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.MessageProxyService;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;

public class HelpDeskBotSession {
  private HelpDeskBotConfig helpDeskBotConfig;
  private SymphonyClient symphonyClient;
  private HelpDeskAi helpDeskAi;
  private MakerCheckerService agentMakerCheckerService;
  private MakerCheckerService clientMakerCheckerService;
  private TicketClient ticketClient;
  private MembershipClient membershipClient;
  private MessageProxyService messageProxyService;
  private ConnectionsEventListener connectionsEventListener;

  public SymphonyClient getSymphonyClient() {
    return this.symphonyClient;
  }

  public void setSymphonyClient(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
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

  public ConnectionsEventListener getConnectionsEventListener() {
    return connectionsEventListener;
  }

  public void setConnectionsEventListener(ConnectionsEventListener connectionsEventListener) {
    this.connectionsEventListener = connectionsEventListener;
  }
}