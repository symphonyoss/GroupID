package org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers;

import org.springframework.stereotype.Component;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.helpdesk.bot.util.TempVariablesContext;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.exception.UserNotFoundException;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.exception.UserNotAuthenticatedException;
import org.symphonyoss.symphony.clients.model.SymUser;

/**
 * Helper class to deal with user stuff.
 *
 * Created by rsanchez on 01/03/18.
 */
@Component
public class UserHelper {

  private final TempVariablesContext context = TempVariablesContext.getInstance();

  private final SymphonyClient symphonyClient;

  public UserHelper(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
  }

  /**
   * Retrieves bot user.
   *
   * @return Bot user
   */
  public SymUser getBotUser() {
    return symphonyClient.getLocalUser();
  }

  /**
   * Returns the user context.
   *
   * @param username username
   * @return Symphony client for the given user
   */
  public SymphonyClient getUserContext(String username) {
    SymphonyClient userClient = context.getAuthenticatedUser(username);

    if (userClient == null) {
      throw new UserNotAuthenticatedException("User " + username + " is not authenticated");
    }

    return userClient;
  }

  /**
   * Retrieves the user info.
   *
   * @param user user ref
   * @return User info
   */
  public SymUser getUser(String user) {
    SymUser symUser = context.getUser(user);

    if (symUser == null) {
      throw new UserNotFoundException("User " + user + " not found");
    }

    return symUser;
  }

}
