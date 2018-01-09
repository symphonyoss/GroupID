package org.symphonyoss.symphony.apps.authentication.factories;

import org.symphonyoss.symphony.apps.authentication.ServicesInfoProvider;

/**
 * Factory to build {@link ServicesInfoProvider} component
 *
 * Created by rsanchez on 09/01/18.
 */
public class ServicesInfoProviderFactory {

  private static final ServicesInfoProviderFactory INSTANCE = new ServicesInfoProviderFactory();

  private ServicesInfoProvider provider;

  private ServicesInfoProviderFactory() {}

  public static ServicesInfoProviderFactory getInstance() {
    return INSTANCE;
  }

  public void setComponent(ServicesInfoProvider provider) {
    if (provider == null) {
      throw new IllegalArgumentException("Invalid provider implementation. It mustn't be null");
    }

    this.provider = provider;
  }

  public ServicesInfoProvider getComponent() {
    if (provider == null) {
      throw new IllegalStateException("There is no implementation defined for this component");
    }

    return provider;
  }

}
