package org.symphonyoss.symphony.bots.helpdesk.messageproxy;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.bots.ai.conversation.ProxyIdleTimer;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.HelpDeskBotInfo;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.HelpDeskServiceInfo;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.config.IdleTicketConfig;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.message.IdleMessageBuilder;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.service.TicketService;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.clients.model.SymMessage;

@Component
public class IdleMessageService {

  private final TicketService ticketService;

  private final HelpDeskBotInfo helpDeskBotInfo;

  private final HelpDeskServiceInfo helpDeskServiceInfo;

  private final String agentStreamId;

  private final IdleTicketConfig idleTicketConfig;

  public IdleMessageService (
      TicketService ticketService,
      HelpDeskBotInfo helpDeskBotInfo,
      HelpDeskServiceInfo helpDeskServiceInfo,
      @Value("${agentStreamId}") String agentStreamId,
      IdleTicketConfig idleTicketConfig) {
    this.ticketService = ticketService;
    this.helpDeskBotInfo = helpDeskBotInfo;
    this.helpDeskServiceInfo = helpDeskServiceInfo;
    this.agentStreamId = agentStreamId;
    this.idleTicketConfig = idleTicketConfig;
  }

  public void sendIdleMessage(Ticket ticket) {
    String safeAgentStreamId = Base64.encodeBase64String(Base64.decodeBase64(agentStreamId));

    SymMessage message = new IdleMessageBuilder()
        .message(idleTicketConfig.getMessage())
        .ticketState(ticket.getState())
        .botHost(helpDeskBotInfo.getUrl())
        .serviceHost(helpDeskServiceInfo.getUrl())
        .ticketId(ticket.getId())
        .streamId(safeAgentStreamId)
        .build();

    ticketService.sendIdleMessageToAgentStreamId(message);
  }

}
