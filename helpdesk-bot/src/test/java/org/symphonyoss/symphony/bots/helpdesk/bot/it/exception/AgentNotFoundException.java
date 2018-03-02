package org.symphonyoss.symphony.bots.helpdesk.bot.it.exception;

/**
 * Exception to report the given agent not found.
 *
 * Created by rsanchez on 23/02/18.
 */
public class AgentNotFoundException extends RuntimeException {

  public AgentNotFoundException(String message) {
    super(message);
  }

}
