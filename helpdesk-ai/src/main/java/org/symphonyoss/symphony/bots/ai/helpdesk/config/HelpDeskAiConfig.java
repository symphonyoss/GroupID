package org.symphonyoss.symphony.bots.ai.helpdesk.config;

/**
 * Helpdesk AI configuration
 * Created by nick.tarsillo on 10/27/17.
 */
public class HelpDeskAiConfig {

  private boolean suggestCommands;

  private String groupId;
  private String agentStreamId;

  private String agentServiceRoomPrefix;

  private String closeTicketCommand;
  private String closeTicketSuccessResponse;

  public String getAgentServiceRoomPrefix() {
    return agentServiceRoomPrefix;
  }

  public void setAgentServiceRoomPrefix(String agentServiceRoomPrefix) {
    this.agentServiceRoomPrefix = agentServiceRoomPrefix;
  }

  public String getCloseTicketCommand() {
    return closeTicketCommand;
  }

  public void setCloseTicketCommand(String closeTicketCommand) {
    this.closeTicketCommand = closeTicketCommand;
  }

  public String getCloseTicketSuccessResponse() {
    return closeTicketSuccessResponse;
  }

  public void setCloseTicketSuccessResponse(String closeTicketSuccessResponse) {
    this.closeTicketSuccessResponse = closeTicketSuccessResponse;
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
