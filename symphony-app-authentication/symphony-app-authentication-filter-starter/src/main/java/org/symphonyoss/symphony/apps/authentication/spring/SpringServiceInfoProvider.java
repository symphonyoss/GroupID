package org.symphonyoss.symphony.apps.authentication.spring;

import org.symphonyoss.symphony.apps.authentication.endpoints.ServicesInfoProvider;
import org.symphonyoss.symphony.apps.authentication.spring.properties.AuthenticationProperties;
import org.symphonyoss.symphony.apps.authentication.spring.properties.ServiceAddress;

/**
 * Provider to read information from YAML config file.
 *
 * Created by rsanchez on 10/01/18.
 */
public class SpringServiceInfoProvider implements ServicesInfoProvider {

  private static final String POD_CONTEXT = "pod";

  private static final String SESSION_AUTH_CONTEXT = "sessionauth";

  private final AuthenticationProperties properties;

  private String podUrl;

  private String sessionAuthUrl;

  public SpringServiceInfoProvider(AuthenticationProperties properties) {
    this.properties = properties;

    if (properties == null) {
      throw new IllegalArgumentException("Missing service addresses");
    }

    this.podUrl = getPodUrl();
    this.sessionAuthUrl = getSessionAuthUrl();
  }

  private String getPodUrl() {
    ServiceAddress address = properties.getPod();

    if (address == null) {
      throw new IllegalArgumentException("POD address not provided");
    }

    return address.getUrl(POD_CONTEXT);
  }

  private String getSessionAuthUrl() {
    ServiceAddress address = properties.getSessionAuth();

    if (address == null) {
      throw new IllegalArgumentException("Session auth address not provided");
    }

    return address.getUrl(SESSION_AUTH_CONTEXT);
  }

  @Override
  public String getPodBaseUrl() {
    return podUrl;
  }

  @Override
  public String getSessionAuthBaseUrl() {
    return sessionAuthUrl;
  }
}
