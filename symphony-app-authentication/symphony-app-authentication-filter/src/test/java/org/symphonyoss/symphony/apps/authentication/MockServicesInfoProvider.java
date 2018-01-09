package org.symphonyoss.symphony.apps.authentication;

/**
 * Mock class for {@link ServicesInfoProvider}
 *
 * Created by rsanchez on 09/01/18.
 */
public class MockServicesInfoProvider implements ServicesInfoProvider {

  private final String podUrl;

  private final String sessionAuthUrl;

  public MockServicesInfoProvider(String podUrl, String sessionAuthUrl) {
    this.podUrl = podUrl;
    this.sessionAuthUrl = sessionAuthUrl;
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
