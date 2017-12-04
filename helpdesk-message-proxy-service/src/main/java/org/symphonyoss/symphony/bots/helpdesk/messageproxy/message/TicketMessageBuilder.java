package org.symphonyoss.symphony.bots.helpdesk.messageproxy.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by rsanchez on 01/12/17.
 */
public class TicketMessageBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(TicketMessageBuilder.class);

  private static final String BASE_TICKET_EVENT = "com.symphony.bots.helpdesk.event.ticket";

  private static final String USER_TICKET_EVENT = BASE_TICKET_EVENT + ".user";

  private static final String MESSAGE_TICKET_EVENT = BASE_TICKET_EVENT + ".message";

  private static final String VERSION = "1.0";

  private static final String CLAIM_MESSAGE_TEMPLATE = "claimMessage.xml";

  private static String message;

  private final SymMessageBuilder messageBuilder;

  private String host;

  private String ticketId;

  private String ticketState;

  private String username;

  private String header;

  private String company;

  private String question;

  public TicketMessageBuilder() {
    if (StringUtils.isEmpty(message)) {
      message = parseTemplate();
    }

    this.messageBuilder = SymMessageBuilder.message(message);
  }

  private String parseTemplate() {
    StringBuilder message = new StringBuilder();
    InputStream resource = getClass().getClassLoader().getResourceAsStream(CLAIM_MESSAGE_TEMPLATE);

    try (BufferedReader buffer = new BufferedReader(new InputStreamReader(resource))) {
      buffer.lines().forEach(message::append);
    } catch (IOException e) {
      LOGGER.error("Fail to parse claim message template");
    }

    return message.toString();
  }

  public TicketMessageBuilder host(String host) {
    this.host = host;
    return this;
  }

  public TicketMessageBuilder ticketId(String ticketId) {
    this.ticketId = ticketId;
    return this;
  }

  public TicketMessageBuilder ticketState(String ticketState) {
    this.ticketState = ticketState;
    return this;
  }

  public TicketMessageBuilder username(String username) {
    this.username = username;
    return this;
  }

  public TicketMessageBuilder header(String header) {
    this.header = header;
    return this;
  }

  public TicketMessageBuilder company(String company) {
    this.company = company;
    return this;
  }

  public TicketMessageBuilder question(String question) {
    this.question = question;
    return this;
  }

  public SymMessage build() {
    if (messageBuilder == null) {
      return null;
    }

    try {
      EntityBuilder userBuilder = EntityBuilder.createEntity(USER_TICKET_EVENT, VERSION);
      userBuilder.addField("displayName", username);

      EntityBuilder ticketMessageBuilder = EntityBuilder.createEntity(MESSAGE_TICKET_EVENT, VERSION);
      ticketMessageBuilder.addField("header", header);
      ticketMessageBuilder.addField("company", company);
      ticketMessageBuilder.addField("customer", username);
      ticketMessageBuilder.addField("question", question);

      EntityBuilder bodyBuilder = EntityBuilder.createEntity(BASE_TICKET_EVENT, VERSION);

      String url = String.format("%s/v1/ticket/%s/accept", host, ticketId);
      bodyBuilder.addField("url", url);

      bodyBuilder.addField("ticketId", ticketId);
      bodyBuilder.addField("state", ticketState);
      bodyBuilder.addField("user", userBuilder.toObject());
      bodyBuilder.addField("message", ticketMessageBuilder.toObject());

      EntityBuilder builder = EntityBuilder.createEntity();
      builder.addField("helpdesk", bodyBuilder.toObject());

      String entityData = builder.build();

      return messageBuilder.entityData(entityData).build();
    } catch (JsonProcessingException e) {
      LOGGER.error("Fail to create entity data");
      return null;
    }
  }

}
