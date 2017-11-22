package org.symphonyoss.symphony.bots.helpdesk.bot.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.clients.AuthenticationClient;

/**
 * Service component responsible for bot authentication.
 *
 * Created by rsanchez on 20/11/17.
 */
@Service
public class HelpDeskAuthenticationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(HelpDeskAuthenticationService.class);

  private final HelpDeskBotConfig configuration;

  public HelpDeskAuthenticationService(HelpDeskBotConfig configuration) {
    this.configuration = configuration;
  }

  /**
   * Perform authentication using keystore and truststore files defined in the configuration.
   *
   * @return Symphony Auth object
   */
  public SymAuth authenticate() {
    AuthenticationClient authClient = new AuthenticationClient(configuration.getSessionAuthUrl(),
        configuration.getKeyAuthUrl());

    LOGGER.info("Setting up auth http client for help desk bot with group id: " + configuration.getGroupId());

    System.setProperty("javax.net.ssl.keyStore", configuration.getKeyStoreFile());
    System.setProperty("javax.net.ssl.keyStorePassword", configuration.getKeyStorePassword());
    System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");

    String trustStoreFile = configuration.getTrustStoreFile();
    if (trustStoreFile != null) {
      System.setProperty("javax.net.ssl.trustStore", trustStoreFile);
    }

    String trustStorePassword = configuration.getTrustStorePassword();
    if (trustStorePassword != null) {
      System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
    }

    LOGGER.info("Attempting bot auth for help desk bot with group id: " + configuration.getGroupId());

    try {
      return authClient.authenticate();
    } catch (Exception e) {
      throw new HelpDeskAuthenticationException(e);
    }
  }

}
