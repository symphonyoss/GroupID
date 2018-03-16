package org.symphonyoss.symphony.apps.authentication.keystore;

import org.symphonyoss.symphony.apps.authentication.keystore.model.KeystoreSettings;

/**
 * Mock class for {@link KeystoreProvider}
 *
 * Created by rsanchez on 06/03/18.
 */
public class MockKeystoreProvider implements KeystoreProvider {

  private final KeystoreSettings settings;

  public MockKeystoreProvider(KeystoreSettings settings) {
    this.settings = settings;
  }

  @Override
  public KeystoreSettings getApplicationKeystore(String appId) {
    return settings;
  }
}
