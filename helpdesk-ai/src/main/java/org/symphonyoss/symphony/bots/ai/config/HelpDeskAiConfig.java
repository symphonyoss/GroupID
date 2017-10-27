package org.symphonyoss.symphony.bots.ai.config;

/**
 * Created by nick.tarsillo on 10/27/17.
 */
public class HelpDeskAiConfig {
  private boolean suggestCommands;
  private String sessionContextDir;

  private String defaultPrefix;
  private String agentServiceRoomPrefix;

  private String acceptTicketCommand;
  private String addMemberCommand;
  private String closeTicketCommand;

  private String addMemberClientSuccessResponse;
  private String addMemberAgentSuccessResponse;
  private String acceptTicketAgentSuccessResponse;
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

  public String getSessionContextDir() {
    return sessionContextDir;
  }

  public void setSessionContextDir(String sessionContextDir) {
    this.sessionContextDir = sessionContextDir;
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

  public String getAcceptTicketAgentSuccessResponse() {
    return acceptTicketAgentSuccessResponse;
  }

  public void setAcceptTicketAgentSuccessResponse(String acceptTicketAgentSuccessResponse) {
    this.acceptTicketAgentSuccessResponse = acceptTicketAgentSuccessResponse;
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
}
