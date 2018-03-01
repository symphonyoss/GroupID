package org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers;

import org.springframework.stereotype.Component;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.TestContext;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.UsersEnum;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.exception.AgentNotFoundException;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.exception.UserNotAuthenticatedException;
import org.symphonyoss.symphony.clients.model.SymUser;

/**
 * Helper class to deal with user stuff.
 *
 * Created by rsanchez on 01/03/18.
 */
@Component
public class UserHelper {

  private final TestContext context = TestContext.getInstance();

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
   * Retrieves the agent user info.
   *
   * @param username Agent username
   * @return Agent user info
   */
  public SymUser getAgentUser(String username) {
    UsersEnum user = UsersEnum.valueOf(username.toUpperCase());

    SymUser agentUser = context.getUser(user);

    if (agentUser == null) {
      throw new AgentNotFoundException("Agent " + username + " not found");
    }

    return agentUser;
  }

}
