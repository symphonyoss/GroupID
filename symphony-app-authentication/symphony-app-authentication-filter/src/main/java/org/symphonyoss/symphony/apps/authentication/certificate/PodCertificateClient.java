package org.symphonyoss.symphony.apps.authentication.certificate;

import org.symphonyoss.symphony.apps.authentication.certificate.exception.PodCertificateException;
import org.symphonyoss.symphony.apps.authentication.certificate.model.PodCertificate;

/**
 * Interface to implement HTTP Client to retrieve POD certificate.
 *
 * Created by rsanchez on 10/01/18.
 */
public interface PodCertificateClient {

  /**
   * Retrieve and return the POD public certificate in PEM format.
   *
   * @return POD certificate.
   */
  PodCertificate getPodPublicCertificate() throws PodCertificateException;

}
