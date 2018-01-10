package org.symphonyoss.symphony.apps.authentication.certificate;

import org.symphonyoss.symphony.apps.authentication.AbstractFactory;

/**
 * Factory to build {@link PodCertificateClient} component
 *
 * Created by rsanchez on 09/01/18.
 */
public class PodCertificateClientFactory extends AbstractFactory<PodCertificateClient> {

  private static final PodCertificateClientFactory INSTANCE = new PodCertificateClientFactory();

  private PodCertificateClientFactory() {}

  public static PodCertificateClientFactory getInstance() {
    return INSTANCE;
  }

}
