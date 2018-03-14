package org.symphonyoss.symphony.apps.authentication.endpoints;

import org.symphonyoss.symphony.apps.authentication.AbstractFactory;

/**
 * Factory to build {@link ServicesInfoProvider} component
 *
 * Created by rsanchez on 09/01/18.
 */
public class ServicesInfoProviderFactory extends AbstractFactory<ServicesInfoProvider> {

  private static final ServicesInfoProviderFactory INSTANCE = new ServicesInfoProviderFactory();

  private ServicesInfoProviderFactory() {}

  public static ServicesInfoProviderFactory getInstance() {
    return INSTANCE;
  }

}
