package org.symphonyoss.symphony.apps.authentication.spring.configuration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.symphonyoss.symphony.apps.authentication.keystore.KeystoreProvider;
import org.symphonyoss.symphony.apps.authentication.spring.keystore.EnvKeystoreProvider;

/**
 * Unit tests for {@link KeystoreProviderConfiguration}
 *
 * Created by rsanchez on 09/03/18.
 */
public class KeystoreProviderConfigurationTest {

  @Test
  public void testConfiguration() {
    KeystoreProviderConfiguration configuration = new KeystoreProviderConfiguration();
    KeystoreProvider provider = configuration.keystoreProvider();

    assertEquals(EnvKeystoreProvider.class, provider.getClass());
  }

}
