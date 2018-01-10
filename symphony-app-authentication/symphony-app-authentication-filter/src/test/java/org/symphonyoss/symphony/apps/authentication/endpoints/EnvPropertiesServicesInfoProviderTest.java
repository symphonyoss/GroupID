package org.symphonyoss.symphony.apps.authentication.endpoints;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for {@link EnvPropertiesServicesInfoProvider}
 *
 * Created by rsanchez on 10/01/18.
 */
public class EnvPropertiesServicesInfoProviderTest {

  private static final String POD_HOST = "pod_host";

  private static final String MOCK_POD_HOST = "test.symphony.com";

  private static final String SESSION_AUTH_HOST = "session_auth_host";

  private static final String SESSION_AUTH_PORT = "session_auth_port";

  private static final String MOCK_SESSION_AUTH_HOST = "test-api.symphony.com";

  private static final String SESSION_AUTH_HTTPS_PORT = "8444";

  private static final String EXPECTED_POD_URL = "https://test.symphony.com:443/pod";

  private static final String EXPECTED_SESSION_AUTH_URL = "https://test-api.symphony.com:8444/sessionauth";

  private ServicesInfoProvider provider = new EnvPropertiesServicesInfoProvider();

  @Test
  public void testPodUrl() {
    System.setProperty(POD_HOST, MOCK_POD_HOST);

    String baseUrl = provider.getPodBaseUrl();
    assertEquals(EXPECTED_POD_URL, baseUrl);
  }

  @Test
  public void testSessionAuthUrl() {
    System.setProperty(SESSION_AUTH_HOST, MOCK_SESSION_AUTH_HOST);
    System.setProperty(SESSION_AUTH_PORT, SESSION_AUTH_HTTPS_PORT);

    String baseUrl = provider.getSessionAuthBaseUrl();
    assertEquals(EXPECTED_SESSION_AUTH_URL, baseUrl);
  }
}
