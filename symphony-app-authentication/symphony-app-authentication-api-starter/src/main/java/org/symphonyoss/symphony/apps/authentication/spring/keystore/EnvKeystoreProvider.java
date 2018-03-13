package org.symphonyoss.symphony.apps.authentication.spring.keystore;

import org.symphonyoss.symphony.apps.authentication.keystore.KeystoreProvider;
import org.symphonyoss.symphony.apps.authentication.keystore.model.KeystoreSettings;
import org.symphonyoss.symphony.apps.authentication.utils.PropertiesReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

/**
 * Implementation class to retrieve the application keystore from system properties or
 * environment variables
 *
 * Created by rsanchez on 08/03/18.
 */
public class EnvKeystoreProvider implements KeystoreProvider {

  private static final String KEYSTORE = "app_keystore";

  private static final String KEYSTORE_PASS = KEYSTORE + "_password";

  private static final String KEYSTORE_TYPE = KEYSTORE + "_type";

  private static final String DEFAULT_KEYSTORE_TYPE = "pkcs12";

  @Override
  public KeystoreSettings getApplicationKeystore(String appId) {
    String keystorePath = PropertiesReader.readRequiredProperty(KEYSTORE,
        "App keystore not provided in the system properties or environment variables.");
    String keystorePass = PropertiesReader.readRequiredProperty(KEYSTORE_PASS,
        "App keystore password not provided in the system properties or environment variables.");
    String keystoreType = PropertiesReader.readProperty(KEYSTORE_TYPE, DEFAULT_KEYSTORE_TYPE);

    KeyStore keyStore = loadKeyStore(keystorePath, keystorePass, keystoreType);

    return new KeystoreSettings(keyStore, keystorePass);
  }

  /**
   * Load the keystore file
   *
   * @param storeLocation Keystore path
   * @param password Keystore password
   * @param type Keystore type
   * @return Keystore object
   */
  private KeyStore loadKeyStore(String storeLocation, String password, String type) {
    try(FileInputStream inputStream = new FileInputStream(storeLocation)) {
      final KeyStore ks = KeyStore.getInstance(type);
      ks.load(inputStream, password.toCharArray());

      return ks;
    } catch (GeneralSecurityException | IOException e) {
      throw new LoadKeyStoreException(
          String.format("Fail to load keystore file at %s", storeLocation), e);
    }
  }
}
