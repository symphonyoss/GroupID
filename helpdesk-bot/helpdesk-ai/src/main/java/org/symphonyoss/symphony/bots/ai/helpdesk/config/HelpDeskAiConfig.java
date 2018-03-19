package org.symphonyoss.symphony.bots.ai.helpdesk.config;

/**
 * Helpdesk AI configuration
 * Created by nick.tarsillo on 10/27/17.
 */
public class HelpDeskAiConfig {
  private boolean suggestCommands;

  private String groupId;
  private String agentStreamId;

  private String defaultPrefix;
  private String agentServiceRoomPrefix;

  private String acceptTicketCommand;
  private String addMemberCommand;
  private String closeTicketCommand;

  private String addMemberClientSuccessResponse;
  private String addMemberAgentSuccessResponse;
  private String acceptTicketClientSuccessResponse;
  private String closeTicketSuccessResponse;

  public String getDefaultPrefix() {
    return defaultPrefix;
  }

  public void setDefaultPrefix(String defaultPrefix) {
    this.defaultPrefix = defaultPrefix;
  }

  public String getAgentServiceRoomPrefix() {
    return agentServiceRoomPrefix;
  }

  public void setAgentServiceRoomPrefix(String agentServiceRoomPrefix) {
    this.agentServiceRoomPrefix = agentServiceRoomPrefix;
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

  public String getCloseTicketCommand() {
    return closeTicketCommand;
  }

  public void setCloseTicketCommand(String closeTicketCommand) {
    this.closeTicketCommand = closeTicketCommand;
  }

  public boolean isSuggestCommands() {
    return suggestCommands;
  }

  public void setSuggestCommands(boolean suggestCommands) {
    this.suggestCommands = suggestCommands;
  }

  public String getAddMemberClientSuccessResponse() {
    return addMemberClientSuccessResponse;
  }

  public void setAddMemberClientSuccessResponse(String addMemberClientSuccessResponse) {
    this.addMemberClientSuccessResponse = addMemberClientSuccessResponse;
  }

  public String getAddMemberAgentSuccessResponse() {
    return addMemberAgentSuccessResponse;
  }

  public void setAddMemberAgentSuccessResponse(String addMemberAgentSuccessResponse) {
    this.addMemberAgentSuccessResponse = addMemberAgentSuccessResponse;
  }

  public String getCloseTicketSuccessResponse() {
    return closeTicketSuccessResponse;
  }

  public void setCloseTicketSuccessResponse(String closeTicketSuccessResponse) {
    this.closeTicketSuccessResponse = closeTicketSuccessResponse;
  }

  public String getAcceptTicketClientSuccessResponse() {
    return acceptTicketClientSuccessResponse;
  }

  public void setAcceptTicketClientSuccessResponse(String acceptTicketClientSuccessResponse) {
    this.acceptTicketClientSuccessResponse = acceptTicketClientSuccessResponse;
  }

  public String getAgentStreamId() {
    return agentStreamId;
  }

  public void setAgentStreamId(String agentStreamId) {
    this.agentStreamId = agentStreamId;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

}
