package org.symphonyoss.symphony.apps.authentication.certificate;

/**
 * Factory to build {@link PodCertificateClient} component
 *
 * Created by rsanchez on 09/01/18.
 */
public class PodCertificateClientFactory {

  private static final PodCertificateClientFactory INSTANCE = new PodCertificateClientFactory();

  private PodCertificateClient client;

  private PodCertificateClientFactory() {}

  public static PodCertificateClientFactory getInstance() {
    return INSTANCE;
  }

  public void setComponent(PodCertificateClient client) {
    if (client == null) {
      throw new IllegalArgumentException("Invalid client implementation. It mustn't be null");
    }

    this.client = client;
  }

  public PodCertificateClient getComponent() {
    if (client == null) {
      throw new IllegalStateException("There is no implementation defined for this component");
    }

    return client;
  }

}
