package org.symphonyoss.symphony.apps.authentication.spring.configuration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.symphonyoss.symphony.apps.authentication.spring.tokens.LocalStoreTokensProvider;
import org.symphonyoss.symphony.apps.authentication.tokens.StoreTokensProvider;

/**
 * Unit tests for {@link StoreTokensProviderConfiguration}
 *
 * Created by rsanchez on 09/03/18.
 */
public class StoreTokensProviderConfigurationTest {

  @Test
  public void testConfiguration() {
    StoreTokensProviderConfiguration configuration = new StoreTokensProviderConfiguration();
    StoreTokensProvider provider = configuration.storeTokensProvider();

    assertEquals(LocalStoreTokensProvider.class, provider.getClass());
  }

}
