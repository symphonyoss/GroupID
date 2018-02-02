package org.symphonyoss.symphony.bots.helpdesk.bot.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.symphonyoss.symphony.bots.helpdesk.bot.authentication.HelpDeskAuthenticationException;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.AuthenticationConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;

/**
 * Unit tests for {@link HelpDeskHttpClient}
 * Created by rsanchez on 02/02/18.
 */
public class HelpDeskHttpClientTest {

  private static final String DEFAULT_PASS = "changeit";

  private static final String DEFAULT_KEYSTORE_FILE = "test.p12";

  private static final String KEYSTORE_TYPE = "pkcs12";

  private static final String MOCK_KEYSTORE_DATA = "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";

  @Before
  public void init() {
    System.setProperty("javax.net.ssl.keyStoreType", "");
    System.setProperty("javax.net.ssl.keyStore", "");
    System.setProperty("javax.net.ssl.keyStorePassword", "");
  }

  @Test(expected = IllegalStateException.class)
  public void testIllegalState() {
    new HelpDeskHttpClient().getClient();
  }

  @Test
  public void testEmptyKeystoreData() {
    AuthenticationConfig authConfig = new AuthenticationConfig();
    authConfig.setKeystorePassword(DEFAULT_PASS);
    authConfig.setKeystoreFile(DEFAULT_KEYSTORE_FILE);

    HelpDeskBotConfig configuration = new HelpDeskBotConfig();
    configuration.setAuthentication(authConfig);

    HelpDeskHttpClient httpClient = new HelpDeskHttpClient();
    httpClient.setupClient(configuration);

    assertEquals(KEYSTORE_TYPE, System.getProperty("javax.net.ssl.keyStoreType"));
    assertEquals(DEFAULT_KEYSTORE_FILE, System.getProperty("javax.net.ssl.keyStore"));
    assertEquals(DEFAULT_PASS, System.getProperty("javax.net.ssl.keyStorePassword"));

    assertNotNull(httpClient.getClient());
  }

  @Test(expected = HelpDeskAuthenticationException.class)
  public void testInvalidKeystoreData() {
    AuthenticationConfig authConfig = new AuthenticationConfig();
    authConfig.setKeystorePassword(DEFAULT_PASS);
    authConfig.setKeystoreData("");

    HelpDeskBotConfig configuration = new HelpDeskBotConfig();
    configuration.setAuthentication(authConfig);

    HelpDeskHttpClient httpClient = new HelpDeskHttpClient();
    httpClient.setupClient(configuration);
  }

  @Test
  public void testKeystoreData() {
    AuthenticationConfig authConfig = new AuthenticationConfig();
    authConfig.setKeystorePassword(DEFAULT_PASS);
    authConfig.setKeystoreData(MOCK_KEYSTORE_DATA);

    HelpDeskBotConfig configuration = new HelpDeskBotConfig();
    configuration.setAuthentication(authConfig);

    HelpDeskHttpClient httpClient = new HelpDeskHttpClient();
    httpClient.setupClient(configuration);

    assertEquals("", System.getProperty("javax.net.ssl.keyStoreType"));
    assertEquals("", System.getProperty("javax.net.ssl.keyStore"));
    assertEquals("", System.getProperty("javax.net.ssl.keyStorePassword"));

    assertNotNull(httpClient.getClient());
  }
}
