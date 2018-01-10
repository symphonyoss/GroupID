package org.symphonyoss.symphony.apps.authentication.certificate;

import org.glassfish.jersey.internal.util.Base64;
import org.symphonyoss.symphony.apps.authentication.certificate.exception.PodCertificateException;
import org.symphonyoss.symphony.apps.authentication.certificate.model.PodCertificate;
import sun.security.provider.X509Factory;

import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Component to retrieve POD public key.
 *
 * Created by rsanchez on 10/01/18.
 */
public class PodCertificateService {

  private final PodCertificateClientFactory factory = PodCertificateClientFactory.getInstance();

  /**
   * Gets a public key from a POD public certificate.
   *
   * @return POD Public Key.
   */
  public PublicKey getPodPublicKey() throws PodCertificateException {
    PodCertificate pem = factory.getComponent().getPodPublicCertificate();
    return readPublicKey(pem);
  }

  /**
   * Gets a public key from a public certificate.
   *
   * @param certificate X509 certificate to extract the public key from.
   * @return Extracted Public Key.
   */
  private PublicKey readPublicKey(PodCertificate certificate) throws PodCertificateException {
    try {
      String encoded = certificate.getCertificate()
          .replace(X509Factory.BEGIN_CERT, "")
          .replace(X509Factory.END_CERT, "");

      byte[] decoded = Base64.decode(encoded.getBytes());

      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      X509Certificate x509Certificate =
          (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(decoded));

      return x509Certificate.getPublicKey();
    } catch (CertificateException e) {
      throw new PodCertificateException("Cannot retrieve public key from X.509 certificate", e);
    }
  }

}
