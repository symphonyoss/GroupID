package org.symphonyoss.symphony.bots.helpdesk.messageproxy.message;

import org.apache.commons.lang3.StringUtils;
import org.symphonyoss.symphony.bots.helpdesk.service.model.UserInfo;
import org.symphonyoss.symphony.bots.utility.message.EntityBuilder;

/**
 * Created by campidelli on 19-jan-18.
 */
public class AcceptMessageBuilder extends TicketMessageBuilder {

    private static final String BASE_TICKET_EVENT = "com.symphony.bots.helpdesk.event.ticket.claimed";

  private static final String USER_TICKET_EVENT = BASE_TICKET_EVENT + ".user";

  private static final String ACCEPT_TICKET_EVENT = BASE_TICKET_EVENT + ".accept";

  private static final String VERSION = "1.0";

  private static final String ACCEPT_MESSAGE_TEMPLATE = "acceptMessage.xml";

  private static String template;

  private UserInfo agent;

  private String ticketState;

  public AcceptMessageBuilder agent(UserInfo agent) {
    this.agent = agent;
    return this;
  }

  public AcceptMessageBuilder ticketState(String ticketState) {
    this.ticketState = ticketState;
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
    bodyBuilder.addField("state", ticketState);

    EntityBuilder userBuilder = EntityBuilder.createEntity(USER_TICKET_EVENT, VERSION);
    userBuilder.addField("displayName", agent.getDisplayName());
    bodyBuilder.addField("agent", userBuilder.toObject());

    return bodyBuilder;
  }

}
