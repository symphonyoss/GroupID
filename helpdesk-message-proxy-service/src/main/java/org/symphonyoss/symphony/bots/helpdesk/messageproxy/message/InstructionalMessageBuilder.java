package org.symphonyoss.symphony.bots.helpdesk.messageproxy.message;

import org.symphonyoss.symphony.bots.utility.message.SymMessageBuilder;
import org.symphonyoss.symphony.clients.model.SymMessage;

public class InstructionalMessageBuilder {
  private static final String TEMPLATE = "<messageML>Use <b>%s</b> %s</messageML>";

  private String mentionUserId;

  private String message;

  private String command;

  public InstructionalMessageBuilder mentionUserId(String userId) {
    this.mentionUserId = userId;
    return this;
  }

  public InstructionalMessageBuilder command(String command) {
    this.command = command;
    return this;
  }

  public InstructionalMessageBuilder message(String message) {
    this.message = message;
    return this;
  }

  public SymMessage build() {
    String formatCommand = String.format(command, mentionUserId);
    String instructionalMessage = String.format(TEMPLATE, formatCommand, message);
    return SymMessageBuilder.message(instructionalMessage).build();
  }

}
