package org.symphonyoss.symphony.bots.helpdesk.bot.client;

import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.bots.helpdesk.bot.authentication.HelpDeskAuthenticationException;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HttpClientConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * Created by rsanchez on 02/02/18.
 */
@Component
public class HelpDeskHttpClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(HelpDeskHttpClient.class);

  private static final String KEYSTORE_TYPE = "pkcs12";

  private static final String TRUSTSTORE_TYPE = "jks";

  private Client client;

  private boolean initialized;

  /**
   * Setup HTTP client
   */
  public void setupClient(HelpDeskBotConfig configuration) {
    LOGGER.info("Setting up auth http client for help desk bot with group id: " + configuration.getGroupId());

    HttpClientConfig httpClientConfig = configuration.getHttpClient();
    if (httpClientConfig == null) {
      httpClientConfig = new HttpClientConfig();
    }

    final ClientConfig clientConfig = new ClientConfig();
    clientConfig.property(ClientProperties.CONNECT_TIMEOUT, httpClientConfig.getConnectTimeout());
    clientConfig.property(ClientProperties.READ_TIMEOUT, httpClientConfig.getReadTimeout());

    ClientBuilder clientBuilder = ClientBuilder.newBuilder().withConfig(clientConfig);

    String keyStoreData = configuration.getKeyStoreData();
    String keyStorePassword = configuration.getKeyStorePassword();

    if (keyStoreData != null) {
      KeyStore keyStore = loadDataKeystore(keyStoreData, KEYSTORE_TYPE, keyStorePassword);
      clientBuilder = clientBuilder.keyStore(keyStore, keyStorePassword.toCharArray());
    } else {
      System.setProperty("javax.net.ssl.keyStoreType", KEYSTORE_TYPE);
      System.setProperty("javax.net.ssl.keyStore", configuration.getKeyStoreFile());
      System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
    }

    String truststoreData = configuration.getTruststoreData();
    String trustStoreFile = configuration.getTrustStoreFile();
    String trustStorePassword = configuration.getTrustStorePassword();

    if (truststoreData != null) {
      KeyStore keyStore = loadDataKeystore(truststoreData, TRUSTSTORE_TYPE, trustStorePassword);
      clientBuilder = clientBuilder.trustStore(keyStore);
    } else if (trustStoreFile != null) {
      System.setProperty("javax.net.ssl.trustStore", trustStoreFile);
      System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
    }

    this.client = clientBuilder.build();
    this.initialized = true;
  }

  /**
   * Load the keystore from Base64 encoded string
   *
   * @param data Base64 encoded string
   * @param type Keystore type
   * @param password Keystore password
   * @return Keystore object
   */
  private KeyStore loadDataKeystore(String data, String type, String password) {
    byte[] keystoreBytes = Base64.decodeBase64(data.getBytes());

    try (ByteArrayInputStream bis = new ByteArrayInputStream(keystoreBytes)) {
      final KeyStore ks = KeyStore.getInstance(type);
      ks.load(bis, password.toCharArray());

      return ks;
    } catch (GeneralSecurityException | IOException e) {
      throw new HelpDeskAuthenticationException("Fail to load keystore data", e);
    }
  }

  public Client getClient() {
    if (!initialized) {
      throw new IllegalStateException("HTTP client was not initialized");
    }

    return client;
  }
}
