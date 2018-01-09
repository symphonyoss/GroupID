package org.symphonyoss.symphony.apps.authentication.certificate;

import static javax.ws.rs.core.MediaType.WILDCARD;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.internal.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.apps.authentication.ServicesInfoProvider;
import org.symphonyoss.symphony.apps.authentication.certificate.exception.PodCertificateException;
import org.symphonyoss.symphony.apps.authentication.certificate.model.PodCertificate;
import org.symphonyoss.symphony.apps.authentication.factories.ServicesInfoProviderFactory;
import org.symphonyoss.symphony.apps.authentication.json.JsonParser;
import sun.security.provider.X509Factory;

import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * HTTP Client to retrieve POD public key.
 *
 * Created by rsanchez on 09/01/18.
 */
public class PodCertificateClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(PodCertificateClient.class);

  private static final String POD_CERT_PATH = "v1/podcert";

  private final Integer connectTimeout;

  private final Integer readTimeout;

  private final ServicesInfoProviderFactory factory = ServicesInfoProviderFactory.getInstance();

  public PodCertificateClient(Integer connectTimeout, Integer readTimeout) {
    this.connectTimeout = connectTimeout;
    this.readTimeout = readTimeout;
  }

  /**
   * Initializes HTTP Client
   */
  private Client initHttpClient() {
    final ClientConfig clientConfig = new ClientConfig();
    clientConfig.property(ClientProperties.CONNECT_TIMEOUT, connectTimeout);
    clientConfig.property(ClientProperties.READ_TIMEOUT, readTimeout);

    return ClientBuilder.newBuilder().withConfig(clientConfig).build();
  }

  /**
   * Gets a public key from a POD public certificate.
   *
   * @return POD Public Key.
   */
  public PublicKey getPodPublicKey() throws PodCertificateException {
    PodCertificate pem = getPodPublicCertificate();
    return readPublicKey(pem);
  }

  /**
   * Retrieve and return the POD public certificate in PEM format.
   *
   * @return POD certificate.
   */
  private PodCertificate getPodPublicCertificate() throws PodCertificateException {
    Client client = initHttpClient();

    try {
      ServicesInfoProvider provider = factory.getComponent();

      WebTarget target = client.target(provider.getPodBaseUrl()).path(POD_CERT_PATH);
      Response response = target.request().accept(WILDCARD).get();

      if (Response.Status.OK.getStatusCode() == response.getStatus()) {
        String json = response.readEntity(String.class);
        return JsonParser.writeToObject(json, PodCertificate.class);
      } else {
        LOGGER.error("Fail to retrieve POD certificate. HTTP Status: " + response.toString());
      }
    } catch (Exception e) {
      throw new PodCertificateException("Unexpected error to retrieve POD certificate", e);
    } finally {
      client.close();
    }

    throw new PodCertificateException("Fail to retrieve POD certificate");
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
