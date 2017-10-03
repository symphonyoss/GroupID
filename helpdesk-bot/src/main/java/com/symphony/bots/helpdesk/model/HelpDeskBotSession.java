package com.symphony.bots.helpdesk.model;

import com.symphony.bots.helpdesk.model.ai.HelpDeskAi;
import com.symphony.bots.helpdesk.service.makerchecker.MakerCheckerService;
import com.symphony.bots.helpdesk.service.membership.MembershipService;
import com.symphony.bots.helpdesk.service.messageproxy.MessageProxyService;
import com.symphony.bots.helpdesk.service.ticket.TicketService;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.clients.model.SymUser;

/**
 * Created by nick.tarsillo on 9/29/17.
 * Represents a session of a single help desk bot.
 */
public class HelpDeskBotSession {
  private SymUser botUser;
  private SymphonyClient symphonyClient;
  private HelpDeskAi helpDeskAi;
  private String agentRoomId;
  private String groupId;

  private MakerCheckerService agentMakerCheckerService;
  private MakerCheckerService clientMakerCheckerService;
  private TicketService ticketService;
  private MembershipService membershipService;
  private MessageProxyService messageProxyService;

  public SymUser getBotUser() {
    return botUser;
  }

  public void setBotUser(SymUser botUser) {
    this.botUser = botUser;
  }

  public SymphonyClient getSymphonyClient() {
    return symphonyClient;
  }

  public void setSymphonyClient(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getAgentRoomId() {
    return agentRoomId;
  }

  public void setAgentRoomId(String agentRoomId) {
    this.agentRoomId = agentRoomId;
  }

  public MakerCheckerService getAgentMakerCheckerService() {
    return agentMakerCheckerService;
  }

  public void setAgentMakerCheckerService(
      MakerCheckerService agentMakerCheckerService) {
    this.agentMakerCheckerService = agentMakerCheckerService;
  }

  public TicketService getTicketService() {
    return ticketService;
  }

  public void setTicketService(TicketService ticketService) {
    this.ticketService = ticketService;
  }

  public MembershipService getMembershipService() {
    return membershipService;
  }

  public void setMembershipService(
      MembershipService membershipService) {
    this.membershipService = membershipService;
  }

  public MessageProxyService getMessageProxyService() {
    return messageProxyService;
  }

  public void setMessageProxyService(
      MessageProxyService messageProxyService) {
    this.messageProxyService = messageProxyService;
  }

  public HelpDeskAi getHelpDeskAi() {
    return helpDeskAi;
  }

  public void setHelpDeskAi(HelpDeskAi helpDeskAi) {
    this.helpDeskAi = helpDeskAi;
  }

  public MakerCheckerService getClientMakerCheckerService() {
    return clientMakerCheckerService;
  }

  public void setClientMakerCheckerService(
      MakerCheckerService clientMakerCheckerService) {
    this.clientMakerCheckerService = clientMakerCheckerService;
  }
}
