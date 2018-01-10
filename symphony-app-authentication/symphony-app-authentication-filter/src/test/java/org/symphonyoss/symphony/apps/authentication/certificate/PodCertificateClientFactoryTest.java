package org.symphonyoss.symphony.apps.authentication.certificate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.symphonyoss.symphony.apps.authentication.certificate.model.PodCertificate;
import org.symphonyoss.symphony.apps.authentication.endpoints.MockServicesInfoProvider;
import org.symphonyoss.symphony.apps.authentication.endpoints.ServicesInfoProvider;
import org.symphonyoss.symphony.apps.authentication.endpoints.ServicesInfoProviderFactory;

/**
 * Unit tests for {@link PodCertificateClientFactory}
 *
 * Created by robson on 10/01/18.
 */
public class PodCertificateClientFactoryTest {

  private PodCertificateClient client = new MockPodCertificateClient(new PodCertificate());

  private PodCertificateClientFactory factory = PodCertificateClientFactory.getInstance();

  @Test
  public void testNullComponent() {
    try {
      factory.setComponent(null);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid client implementation. It mustn't be null", e.getMessage());
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

    factory.setComponent(client);

    assertEquals(client, factory.getComponent());
  }

}
