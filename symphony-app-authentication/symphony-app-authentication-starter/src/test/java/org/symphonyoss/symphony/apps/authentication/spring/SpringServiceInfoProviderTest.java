package org.symphonyoss.symphony.apps.authentication.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.symphonyoss.symphony.apps.authentication.spring.properties.AuthenticationProperties;
import org.symphonyoss.symphony.apps.authentication.spring.properties.ServiceAddress;

/**
 * Unit tests for {@link SpringServiceInfoProvider}
 *
 * Created by rsanchez on 12/01/18.
 */
public class SpringServiceInfoProviderTest {

  private static final String MOCK_POD_HOST = "test.symphony.com";

  private static final String MOCK_SESSION_AUTH_HOST = "test-api.symphony.com";

  private static final Integer MOCK_POD_PORT = 8443;

  private static final Integer MOCK_SESSION_AUTH_PORT = 8444;

  @Test
  public void testNullProperties() {
    try {
      new SpringServiceInfoProvider(null);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Missing service addresses", e.getMessage());
    }
  }

  @Test
  public void testNullPodInfo() {
    try {
      new SpringServiceInfoProvider(new AuthenticationProperties());
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("POD address not provided", e.getMessage());
    }
  }

  @Test
  public void testNullPodHost() {
    AuthenticationProperties properties = new AuthenticationProperties();
    properties.setPod(new ServiceAddress());

    try {
      new SpringServiceInfoProvider(properties);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("pod: Host not provided in the properties", e.getMessage());
    }
  }

  @Test
  public void testNullSessionAuth() {
    ServiceAddress pod = new ServiceAddress();
    pod.setHost(MOCK_POD_HOST);

    AuthenticationProperties properties = new AuthenticationProperties();
    properties.setPod(pod);

    try {
      new SpringServiceInfoProvider(properties);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Session auth address not provided", e.getMessage());
    }
  }

  @Test
  public void testNullSessionAuthHost() {
    ServiceAddress pod = new ServiceAddress();
    pod.setHost(MOCK_POD_HOST);

    AuthenticationProperties properties = new AuthenticationProperties();
    properties.setPod(pod);
    properties.setSessionAuth(new ServiceAddress());

    try {
      new SpringServiceInfoProvider(properties);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("sessionauth: Host not provided in the properties", e.getMessage());
    }
  }

  @Test
  public void testSuccessDefaultPort() {
    ServiceAddress pod = new ServiceAddress();
    pod.setHost(MOCK_POD_HOST);

    ServiceAddress sessionAuth = new ServiceAddress();
    sessionAuth.setHost(MOCK_SESSION_AUTH_HOST);

    AuthenticationProperties properties = new AuthenticationProperties();
    properties.setPod(pod);
    properties.setSessionAuth(sessionAuth);

    SpringServiceInfoProvider provider = new SpringServiceInfoProvider(properties);

    assertEquals("https://test.symphony.com/pod", provider.getPodBaseUrl());
    assertEquals("https://test-api.symphony.com/sessionauth", provider.getSessionAuthBaseUrl());
  }

  @Test
  public void testSuccess() {
    ServiceAddress pod = new ServiceAddress();
    pod.setHost(MOCK_POD_HOST);
    pod.setPort(MOCK_POD_PORT);

    ServiceAddress sessionAuth = new ServiceAddress();
    sessionAuth.setHost(MOCK_SESSION_AUTH_HOST);
    sessionAuth.setPort(MOCK_SESSION_AUTH_PORT);

    AuthenticationProperties properties = new AuthenticationProperties();
    properties.setPod(pod);
    properties.setSessionAuth(sessionAuth);

    SpringServiceInfoProvider provider = new SpringServiceInfoProvider(properties);

    assertEquals("https://test.symphony.com:8443/pod", provider.getPodBaseUrl());
    assertEquals("https://test-api.symphony.com:8444/sessionauth", provider.getSessionAuthBaseUrl());
  }
}
