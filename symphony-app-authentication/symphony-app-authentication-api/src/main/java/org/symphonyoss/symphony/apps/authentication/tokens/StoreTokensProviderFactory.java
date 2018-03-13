package org.symphonyoss.symphony.apps.authentication.tokens;

import org.symphonyoss.symphony.apps.authentication.AbstractFactory;

/**
 * Factory to build {@link StoreTokensProvider} component
 *
 * Created by rsanchez on 06/03/18.
 */
public class StoreTokensProviderFactory extends AbstractFactory<StoreTokensProvider> {

  private static final StoreTokensProviderFactory INSTANCE = new StoreTokensProviderFactory();

  private StoreTokensProviderFactory() {}

  public static StoreTokensProviderFactory getInstance() {
    return INSTANCE;
  }

}
