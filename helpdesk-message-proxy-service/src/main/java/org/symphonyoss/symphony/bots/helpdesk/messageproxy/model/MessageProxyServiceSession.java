package org.symphonyoss.symphony.bots.helpdesk.messageproxy.model;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.ai.HelpDeskAi;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.MakerCheckerService;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.MessageProxyServiceConfig;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;

/**
 * Created by nick.tarsillo on 10/27/17.
 */
public class MessageProxyServiceSession {
  private MessageProxyServiceConfig messageProxyServiceConfig;
  private MakerCheckerService agentMakerCheckerService;
  private MakerCheckerService clientMakerCheckerService;
  private SymphonyClient symphonyClient;
  private HelpDeskAi helpDeskAi;
  private MembershipClient membershipClient;
  private TicketClient ticketClient;

  public MakerCheckerService getAgentMakerCheckerService() {
    return agentMakerCheckerService;
  }

  public void setAgentMakerCheckerService(
      MakerCheckerService agentMakerCheckerService) {
    this.agentMakerCheckerService = agentMakerCheckerService;
  }

  public MakerCheckerService getClientMakerCheckerService() {
    return clientMakerCheckerService;
  }

  public void setClientMakerCheckerService(
      MakerCheckerService clientMakerCheckerService) {
    this.clientMakerCheckerService = clientMakerCheckerService;
  }

  public SymphonyClient getSymphonyClient() {
    return symphonyClient;
  }

  public void setSymphonyClient(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
  }

  public HelpDeskAi getHelpDeskAi() {
    return helpDeskAi;
  }

  public void setHelpDeskAi(HelpDeskAi helpDeskAi) {
    this.helpDeskAi = helpDeskAi;
  }

  public MembershipClient getMembershipClient() {
    return membershipClient;
  }

  public void setMembershipClient(
      MembershipClient membershipClient) {
    this.membershipClient = membershipClient;
  }

  public TicketClient getTicketClient() {
    return ticketClient;
  }

  public void setTicketClient(
      TicketClient ticketClient) {
    this.ticketClient = ticketClient;
  }

  public MessageProxyServiceConfig getMessageProxyServiceConfig() {
    return messageProxyServiceConfig;
  }

  public void setMessageProxyServiceConfig(
      MessageProxyServiceConfig messageProxyServiceConfig) {
    this.messageProxyServiceConfig = messageProxyServiceConfig;
  }
}
