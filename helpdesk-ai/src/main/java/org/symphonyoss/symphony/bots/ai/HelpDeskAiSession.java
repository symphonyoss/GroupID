package org.symphonyoss.symphony.bots.ai;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.ai.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.helpdesk.service.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.client.TicketClient;

/**
 * Created by nick.tarsillo on 10/27/17.
 */
public class HelpDeskAiSession {
  private HelpDeskAiConfig helpDeskAiConfig;

  private MembershipClient membershipClient;
  private TicketClient ticketClient;
  private SymphonyClient symphonyClient;

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

  public SymphonyClient getSymphonyClient() {
    return symphonyClient;
  }

  public void setSymphonyClient(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
  }

  public HelpDeskAiConfig getHelpDeskAiConfig() {
    return helpDeskAiConfig;
  }

  public void setHelpDeskAiConfig(
      HelpDeskAiConfig helpDeskAiConfig) {
    this.helpDeskAiConfig = helpDeskAiConfig;
  }
}
