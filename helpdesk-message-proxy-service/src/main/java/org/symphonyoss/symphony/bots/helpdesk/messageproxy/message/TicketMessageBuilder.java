package org.symphonyoss.symphony.bots.helpdesk.messageproxy.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.bots.utility.message.EntityBuilder;
import org.symphonyoss.symphony.bots.utility.message.SymMessageBuilder;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by rsanchez on 01/12/17.
 */
public abstract class TicketMessageBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(TicketMessageBuilder.class);

  private final SymMessageBuilder messageBuilder;

  protected String botHost;

  protected String serviceHost;

  protected String streamId;

  protected String ticketId;

  public TicketMessageBuilder() {
    this.messageBuilder = SymMessageBuilder.message(getMessageTemplate());
  }

  public TicketMessageBuilder botHost(String host) {
    this.botHost = host;
    return this;
  }

  public TicketMessageBuilder serviceHost(String host) {
    this.serviceHost = host;
    return this;
  }

  public TicketMessageBuilder streamId(String streamId) {
    this.streamId = streamId;
    return this;
  }

  public TicketMessageBuilder ticketId(String ticketId) {
    this.ticketId = ticketId;
    return this;
  }

  protected String parseTemplate(String templateFilename) {
    StringBuilder message = new StringBuilder();
    InputStream resource = getClass().getClassLoader().getResourceAsStream(templateFilename);

    try (BufferedReader buffer = new BufferedReader(new InputStreamReader(resource))) {
      buffer.lines().forEach(message::append);
    } catch (IOException e) {
      LOGGER.error("Fail to parse claim message template");
    }

    return message.toString();
  }

  public SymMessage build() {
    try {
      EntityBuilder builder = EntityBuilder.createEntity();
      builder.addField("helpdesk", getBodyBuilder().toObject());

      String entityData = builder.build();

      return messageBuilder.entityData(entityData).build();
    } catch (JsonProcessingException e) {
      LOGGER.error("Fail to create entity data");
      return null;
    }
  }

  protected abstract String getMessageTemplate();

  protected abstract EntityBuilder getBodyBuilder();

}
