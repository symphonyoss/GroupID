package org.symphonyoss.symphony.bots.helpdesk.messageproxy.message;

import org.symphonyoss.symphony.bots.utility.message.EntityBuilder;

/**
 * Created by rsanchez on 20/12/17.
 */
public class MockTicketMessageBuilder extends TicketMessageBuilder {

  private String message;

  public MockTicketMessageBuilder() {}

  public MockTicketMessageBuilder(String message) {
    this.message = message;
  }

  @Override
  protected String getMessageTemplate() {
    return parseTemplate("mockMessage.xml");
  }

  @Override
  protected EntityBuilder getBodyBuilder() {
    EntityBuilder builder = EntityBuilder.createEntity();

    if (message != null) {
      builder.addField("message", message);
    }

    return builder;
  }

}
