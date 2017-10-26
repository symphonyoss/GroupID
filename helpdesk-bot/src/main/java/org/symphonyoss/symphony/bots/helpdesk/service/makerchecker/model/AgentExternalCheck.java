package org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.model;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;
import org.symphonyoss.symphony.bots.helpdesk.model.HelpDeskBotSession;

import org.symphonyoss.client.model.Chat;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;

/**
 * Created by nick.tarsillo on 10/20/17.
 */
public class AgentExternalCheck extends Checker {
  private HelpDeskBotSession helpDeskBotSession;

  public AgentExternalCheck(HelpDeskBotSession helpDeskBotSession) {
    this.helpDeskBotSession = helpDeskBotSession;
  }

  @Override
  public boolean check(SymphonyAiMessage message) {
    Chat chat = null;
    Ticket ticket = helpDeskBotSession.getTicketService().getTicketByServiceStreamId(message.getStreamId());
    if(ticket == null) {
      ticket = helpDeskBotSession.getTicketService().getTicketByClientStreamId(message.getStreamId());
      chat = helpDeskBotSession.getSymphonyClient().getChatService().getChatByStream(ticket.getServiceStreamId());
    } else {
      chat = helpDeskBotSession.getSymphonyClient().getChatService().getChatByStream(ticket.getClientStreamId());
    }

    if(message.getAttachments() != null &&
        !message.getAttachments().isEmpty()) {
      return true;
    }
    return false;
  }
}
