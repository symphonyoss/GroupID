package org.symphonyoss.symphony.bots.helpdesk.bot.it.steps;

import org.jbehave.core.annotations.When;
import org.springframework.stereotype.Component;
import org.symphonyoss.client.exceptions.AuthenticationException;
import org.symphonyoss.client.exceptions.InitException;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.TestContext;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers.UserHelper;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.utils.AuthenticationUtils;
import org.symphonyoss.symphony.clients.model.SymUser;

/**
 * Class responsible for managing authentication steps.
 * <p>
 * Created by rsanchez on 23/02/18.
 */
@Component
public class AuthenticationSteps {

  private static final String DEFAULT_KEYSTORE_PASSWORD = "changeit";

  private static final String EMAIL_PREFIX = "@test.com";

  private static final String KEYSTORE_TYPE = "pkcs12";

  private final HelpDeskBotConfig config;

  private final UserHelper userHelper;

  private final TestContext context = TestContext.getInstance();

  public AuthenticationSteps(HelpDeskBotConfig config, UserHelper userHelper) {
    this.config = config;
    this.userHelper = userHelper;
  }

  /**
   * Authenticates an user given the username.
   *
   * @param username Username
   * @throws AuthenticationException Failure to authenticate user
   * @throws InitException Failure to initialize Symphony client
   */
  @When("$user user authenticates using a certificate")
  public void authenticateUser(String username) throws AuthenticationException, InitException {
    AuthenticationUtils utils = new AuthenticationUtils(config);
    utils.authenticateUser(username);
  }

  /**
   * Authenticates an agent given the agent reference.
   *
   * @param agentRef Agent reference
   * @throws AuthenticationException Failure to authenticate user
   * @throws InitException Failure to initialize Symphony client
   */
  @When("$user agent authenticates using a certificate")
  public void authenticateAgent(String agentRef) throws AuthenticationException, InitException {
    SymUser agentUser = userHelper.getUser(agentRef.toUpperCase());

    AuthenticationUtils utils = new AuthenticationUtils(config);
    utils.authenticateUser(agentUser.getUsername());
  }
}
