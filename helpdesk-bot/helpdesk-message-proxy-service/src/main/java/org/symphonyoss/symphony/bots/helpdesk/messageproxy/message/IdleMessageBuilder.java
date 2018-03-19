package org.symphonyoss.symphony.bots.helpdesk.messageproxy.message;

import org.apache.commons.lang3.StringUtils;
import org.symphonyoss.symphony.bots.utility.message.EntityBuilder;
import org.symphonyoss.symphony.bots.utility.message.SymMessageBuilder;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Created by rsanchez on 11/12/17.
 */
public class IdleMessageBuilder extends TicketMessageBuilder {

  private static final String TICKET_EVENT = "com.symphony.bots.helpdesk.event.ticket";

  private static final String VERSION = "1.0";

  private static final String IDLE_MESSAGE_TEMPLATE = "idleMessage.xml";

  private static String template;

  private String ticketState;

  private String message;

  public IdleMessageBuilder message(String message) {
    this.message = message;
    return this;
  }

  public IdleMessageBuilder ticketState(String ticketState) {
    this.ticketState = ticketState;
    return this;
  }

  @Override
  protected String getMessageTemplate() {
    if (StringUtils.isEmpty(template)) {
      template = parseTemplate(IDLE_MESSAGE_TEMPLATE);
    }

    return template;
  }

  @Override
  protected EntityBuilder getBodyBuilder() {
    EntityBuilder bodyBuilder = EntityBuilder.createEntity(TICKET_EVENT, VERSION);

    String claimUrl = String.format("%s/v1/ticket/%s/accept", botHost, ticketId);
    bodyBuilder.addField("claimUrl", claimUrl);

    String joinUrl = String.format("%s/v1/ticket/%s/join", botHost, ticketId);
    bodyBuilder.addField("joinUrl", joinUrl);

    String ticketUrl = String.format("%s/v1/ticket/%s", serviceHost, ticketId);
    bodyBuilder.addField("ticketUrl", ticketUrl);

    bodyBuilder.addField("ticketId", ticketId);
    bodyBuilder.addField("state", ticketState);
    bodyBuilder.addField("streamId", streamId);
    bodyBuilder.addField("message", message);

    return bodyBuilder;
  }

}
