package org.symphonyoss.symphony.apps.authentication.endpoints;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.symphonyoss.symphony.apps.authentication.endpoints.MockServicesInfoProvider;
import org.symphonyoss.symphony.apps.authentication.endpoints.ServicesInfoProvider;
import org.symphonyoss.symphony.apps.authentication.endpoints.ServicesInfoProviderFactory;

/**
 * Unit tests for {@link ServicesInfoProviderFactory}
 *
 * Created by robson on 09/01/18.
 */
public class ServicesInfoProviderFactoryTest {

  private static final String MOCK_POD_URL = "https://mock.test.com/pod";

  private static final String MOCK_SESSION_AUTH_URL = "https://mock-api.test.com/sessionauth";

  private static final ServicesInfoProvider MOCK_PROVIDER =
      new MockServicesInfoProvider(MOCK_POD_URL, MOCK_SESSION_AUTH_URL);

  private ServicesInfoProviderFactory factory = ServicesInfoProviderFactory.getInstance();

  @Test
  public void testNullComponent() {
    try {
      factory.setComponent(null);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid component implementation. It mustn't be null", e.getMessage());
    }
  }

  @Test
  public void testFactory() {
    try {
      factory.getComponent();
      fail();
    } catch (IllegalStateException e) {
      assertEquals("There is no implementation defined for this component", e.getMessage());
    }

    factory.setComponent(MOCK_PROVIDER);

    assertEquals(MOCK_PROVIDER, factory.getComponent());
  }

}
