package org.symphonyoss.symphony.bots.helpdesk.messageproxy.message;

import org.apache.commons.lang3.StringUtils;
import org.symphonyoss.symphony.bots.utility.message.EntityBuilder;

/**
 * Created by rsanchez on 01/12/17.
 */
public class ClaimMessageBuilder extends TicketMessageBuilder {

  private static final String BASE_TICKET_EVENT = "com.symphony.bots.helpdesk.event.ticket";

  private static final String USER_TICKET_EVENT = BASE_TICKET_EVENT + ".user";

  private static final String MESSAGE_TICKET_EVENT = BASE_TICKET_EVENT + ".message";

  private static final String VERSION = "1.0";

  private static final String CLAIM_MESSAGE_TEMPLATE = "claimMessage.xml";

  private static String template;

  private String ticketState;

  private String username;

  private String header;

  private String company;

  private String question;

  public ClaimMessageBuilder ticketState(String ticketState) {
    this.ticketState = ticketState;
    return this;
  }

  public ClaimMessageBuilder username(String username) {
    this.username = username;
    return this;
  }

  public ClaimMessageBuilder header(String header) {
    this.header = header;
    return this;
  }

  public ClaimMessageBuilder company(String company) {
    this.company = company;
    return this;
  }

  public ClaimMessageBuilder question(String question) {
    this.question = question;
    return this;
  }

  @Override
  protected String getMessageTemplate() {
    if (StringUtils.isEmpty(template)) {
      template = parseTemplate(CLAIM_MESSAGE_TEMPLATE);
    }

    return template;
  }

  @Override
  protected EntityBuilder getBodyBuilder() {
    EntityBuilder userBuilder = EntityBuilder.createEntity(USER_TICKET_EVENT, VERSION);
    userBuilder.addField("displayName", username);

    EntityBuilder ticketMessageBuilder = EntityBuilder.createEntity(MESSAGE_TICKET_EVENT, VERSION);
    ticketMessageBuilder.addField("header", header);
    ticketMessageBuilder.addField("company", company);
    ticketMessageBuilder.addField("customer", username);
    ticketMessageBuilder.addField("question", question);

    EntityBuilder bodyBuilder = EntityBuilder.createEntity(BASE_TICKET_EVENT, VERSION);

    String claimUrl = String.format("%s/v1/ticket/%s/accept", botHost, ticketId);
    bodyBuilder.addField("claimUrl", claimUrl);

    String joinUrl = String.format("%s/v1/ticket/%s/join", botHost, ticketId);
    bodyBuilder.addField("joinUrl", joinUrl);

    String ticketUrl = String.format("%s/v1/ticket/%s", serviceHost, ticketId);
    bodyBuilder.addField("ticketUrl", ticketUrl);

    bodyBuilder.addField("ticketId", ticketId);
    bodyBuilder.addField("state", ticketState);
    bodyBuilder.addField("streamId", streamId);
    bodyBuilder.addField("user", userBuilder.toObject());
    bodyBuilder.addField("message", ticketMessageBuilder.toObject());

    return bodyBuilder;
  }

}
