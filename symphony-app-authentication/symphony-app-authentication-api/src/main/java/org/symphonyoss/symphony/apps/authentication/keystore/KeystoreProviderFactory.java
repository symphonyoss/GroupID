package org.symphonyoss.symphony.apps.authentication.keystore;

import org.symphonyoss.symphony.apps.authentication.AbstractFactory;

/**
 * Factory to build {@link KeystoreProvider} component
 *
 * Created by rsanchez on 06/03/18.
 */
public class KeystoreProviderFactory extends AbstractFactory<KeystoreProvider> {

  private static final KeystoreProviderFactory INSTANCE = new KeystoreProviderFactory();

  private KeystoreProviderFactory() {}

  public static KeystoreProviderFactory getInstance() {
    return INSTANCE;
  }

}
