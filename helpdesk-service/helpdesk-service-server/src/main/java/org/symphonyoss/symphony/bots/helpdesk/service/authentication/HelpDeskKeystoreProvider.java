package org.symphonyoss.symphony.bots.helpdesk.service.authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.apps.authentication.keystore.KeystoreProvider;
import org.symphonyoss.symphony.apps.authentication.keystore.model.KeystoreSettings;
import org.symphonyoss.symphony.apps.authentication.spring.keystore.LoadKeyStoreException;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Optional;

/**
 * Implementation class to retrieve the keystore used to perform authentication on the POD. This
 * class get the keystore path and keystore password from YAML config file.
 * <p>
 * Created by rsanchez on 12/03/18.
 */
@Component
public class HelpDeskKeystoreProvider implements KeystoreProvider {

  private static final String DEFAULT_KEYSTORE_TYPE = "pkcs12";

  @Value("${app-authentication.api.keystore-file:#{null}}")
  private Optional<String> keystoreFile;

  @Value("${app-authentication.api.keystore-password:#{null}}")
  private Optional<String> keystorePassword;

  @Override
  public KeystoreSettings getApplicationKeystore(String appId) {
    if (!keystoreFile.isPresent()) {
      throw new IllegalStateException("App keystore not provided in the YAML config");
    }

    if (!keystorePassword.isPresent()) {
      throw new IllegalStateException("App keystore password not provided in the YAML config");
    }

    KeyStore keyStore = loadKeyStore(keystoreFile.get(), keystorePassword.get(), DEFAULT_KEYSTORE_TYPE);

    return new KeystoreSettings(keyStore, keystorePassword.get());
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
