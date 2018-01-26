package org.symphonyoss.symphony.bots.helpdesk.messageproxy.message;

import org.apache.commons.lang3.StringUtils;
import org.symphonyoss.symphony.bots.utility.message.EntityBuilder;

/**
 * Created by campidelli on 19-jan-18.
 */
public class AcceptMessageBuilder extends TicketMessageBuilder {

  private static final String BASE_TICKET_EVENT = "com.symphony.bots.helpdesk.event.ticket";

  private static final String ACCEPT_TICKET_EVENT = BASE_TICKET_EVENT + ".accept";

  private static final String VERSION = "1.0";

  private static final String ACCEPT_MESSAGE_TEMPLATE = "acceptMessage.xml";

  private static String template;

  private String agent;

  public AcceptMessageBuilder agent(String agent) {
    this.agent = agent;
    return this;
  }

  @Override
  protected String getMessageTemplate() {
    if (StringUtils.isEmpty(template)) {
      template = parseTemplate(ACCEPT_MESSAGE_TEMPLATE);
    }

    return template;
  }

  @Override
  protected EntityBuilder getBodyBuilder() {
    EntityBuilder bodyBuilder = EntityBuilder.createEntity(ACCEPT_TICKET_EVENT, VERSION);

    bodyBuilder.addField("ticketId", ticketId);
    bodyBuilder.addField("agent", agent);
    bodyBuilder.addField("streamId", streamId);

    return bodyBuilder;
  }

}
