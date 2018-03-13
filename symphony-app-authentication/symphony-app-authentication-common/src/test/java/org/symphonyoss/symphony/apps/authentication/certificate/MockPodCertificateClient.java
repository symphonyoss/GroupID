package org.symphonyoss.symphony.apps.authentication.certificate;

import org.symphonyoss.symphony.apps.authentication.certificate.exception.PodCertificateException;
import org.symphonyoss.symphony.apps.authentication.certificate.model.PodCertificate;

/**
 * Mock class for {@link PodCertificateClient}
 *
 * Created by rsanchez on 10/01/18.
 */
public class MockPodCertificateClient implements PodCertificateClient {

  private final PodCertificate certificate;

  public MockPodCertificateClient(PodCertificate certificate) {
    this.certificate = certificate;
  }

  @Override
  public PodCertificate getPodPublicCertificate() throws PodCertificateException {
    return certificate;
  }

}
