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
    String email = username + EMAIL_PREFIX;
    String certsDir = context.getCertsDir();

    String keystorePath = certsDir + File.separator + username + ".p12";

    Client client = buildHttpClient(keystorePath);

    AuthenticationClient authenticationClient = new AuthenticationClient(config.getSessionAuthUrl(),
        config.getKeyAuthUrl(), client);
    SymAuth symAuth = authenticationClient.authenticate();

    SymphonyClient symphonyClient = initSymphonyClient(email, symAuth);

    context.setAuthenticatedUser(username, symphonyClient);
  }

  /**
   * Builds HTTP client given the keystore path.
   *
   * @param keystorePath Keystore path.
   * @return HTTP client
   */
  private Client buildHttpClient(String keystorePath) {
    HttpClientConfig httpClientConfig = config.getHttpClient();
    if (httpClientConfig == null) {
      httpClientConfig = new HttpClientConfig();
    }

    final ClientConfig clientConfig = new ClientConfig();
    clientConfig.property(ClientProperties.CONNECT_TIMEOUT, httpClientConfig.getConnectTimeout());
    clientConfig.property(ClientProperties.READ_TIMEOUT, httpClientConfig.getReadTimeout());

    KeyStore keyStore = readKeystore(keystorePath);

    return ClientBuilder.newBuilder()
        .withConfig(clientConfig)
        .keyStore(keyStore, DEFAULT_KEYSTORE_PASSWORD)
        .build();
  }

  /**
   * Read the keystore from filesystem.
   *
   * @param keystorePath Keystore path
   * @return Keystore object
   * @throws HelpDeskAuthenticationException Failure to read keystore file
   */
  private KeyStore readKeystore(String keystorePath) {
    try (FileInputStream fis = new FileInputStream(keystorePath)) {
      final KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
      ks.load(fis, DEFAULT_KEYSTORE_PASSWORD.toCharArray());

      return ks;
    } catch (GeneralSecurityException | IOException e) {
      throw new HelpDeskAuthenticationException("Fail to load keystore file", e);
    }
  }

  /**
   * Initializes Symphony Client for the given user.
   *
   * @param email User email address
   * @param symAuth User credentials
   * @return Symphony Client
   * @throws InitException Unexpected error to initialize Symphony Client
   */
  private SymphonyClient initSymphonyClient(String email, SymAuth symAuth) throws InitException {
    SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig(false);
    symphonyClientConfig.set(SymphonyClientConfigID.AGENT_URL, config.getAgentUrl());
    symphonyClientConfig.set(SymphonyClientConfigID.POD_URL, config.getPodUrl());
    symphonyClientConfig.set(SymphonyClientConfigID.USER_EMAIL, email);

    SymphonyClient symphonyClient = new SymphonyBasicClient();
    symphonyClient.init(symAuth, symphonyClientConfig);

    symphonyClient.getAgentHttpClient().register(new JSON());
    symphonyClient.getPodHttpClient().register(new JSON());

    return symphonyClient;
  }
}
