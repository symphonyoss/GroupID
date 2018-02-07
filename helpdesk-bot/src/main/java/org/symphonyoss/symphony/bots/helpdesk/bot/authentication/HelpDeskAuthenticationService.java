package org.symphonyoss.symphony.bots.helpdesk.bot.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.symphonyoss.client.exceptions.AuthenticationException;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.bots.helpdesk.bot.client.HelpDeskHttpClient;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.clients.AuthenticationClient;

import javax.ws.rs.client.Client;

/**
 * Service component responsible for bot authentication.
 *
 * Created by rsanchez on 20/11/17.
 */
@Service
public class HelpDeskAuthenticationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(HelpDeskAuthenticationService.class);

  private final HelpDeskBotConfig configuration;

  private final HelpDeskHttpClient httpClient;

  public HelpDeskAuthenticationService(HelpDeskBotConfig configuration, HelpDeskHttpClient client) {
    this.configuration = configuration;
    this.httpClient = client;
  }

  /**
   * Perform authentication using keystore and truststore files defined in the configuration.
   *
   * @return Symphony Auth object
   */
  public SymAuth authenticate() {
    Client client = httpClient.getClient();

    AuthenticationClient authClient = new AuthenticationClient(configuration.getSessionAuthUrl(),
        configuration.getKeyAuthUrl(), client, client);

    LOGGER.info("Attempting bot auth for help desk bot with group id: " + configuration.getGroupId());

    try {
      return authClient.authenticate();
    } catch (AuthenticationException e) {
      throw new HelpDeskAuthenticationException(e.getCause());
    }
  }

}
