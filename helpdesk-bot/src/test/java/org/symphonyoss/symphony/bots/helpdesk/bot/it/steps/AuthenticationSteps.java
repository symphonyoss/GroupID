package org.symphonyoss.symphony.bots.helpdesk.bot.it.steps;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.jbehave.core.annotations.When;
import org.springframework.stereotype.Component;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.exceptions.AuthenticationException;
import org.symphonyoss.client.exceptions.InitException;
import org.symphonyoss.client.impl.SymphonyBasicClient;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.bots.helpdesk.bot.authentication.HelpDeskAuthenticationException;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HttpClientConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.TestContext;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.utils.AuthenticationUtils;
import org.symphonyoss.symphony.clients.AuthenticationClient;
import org.symphonyoss.symphony.pod.invoker.JSON;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

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

  private final TestContext context = TestContext.getInstance();

  public AuthenticationSteps(HelpDeskBotConfig config) {
    this.config = config;
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

}
