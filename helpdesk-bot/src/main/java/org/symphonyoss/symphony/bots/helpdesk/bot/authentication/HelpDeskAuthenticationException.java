package org.symphonyoss.symphony.bots.helpdesk.bot.authentication;

/**
 * Exception class to report failures during the bot authentication process.
 *
 * Created by robson on 20/11/17.
 */
public class HelpDeskAuthenticationException extends RuntimeException {

  public HelpDeskAuthenticationException(Throwable e) {
    super("Authentication failed for bot", e);
  }

}
