package org.symphonyoss.symphony.apps.authentication.certificate;

import static javax.ws.rs.core.MediaType.WILDCARD;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.apps.authentication.endpoints.ServicesInfoProvider;
import org.symphonyoss.symphony.apps.authentication.certificate.exception.PodCertificateException;
import org.symphonyoss.symphony.apps.authentication.certificate.model.PodCertificate;
import org.symphonyoss.symphony.apps.authentication.endpoints.ServicesInfoProviderFactory;
import org.symphonyoss.symphony.apps.authentication.json.JsonParser;
import org.symphonyoss.symphony.apps.authentication.json.JsonParserFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * HTTP Client to retrieve POD certificate.
 *
 * Created by rsanchez on 09/01/18.
 */
public class PodCertificateJerseyClient implements PodCertificateClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(PodCertificateJerseyClient.class);

  private static final String POD_CERT_PATH = "v1/podcert";

  private final Integer connectTimeout;

  private final Integer readTimeout;

  private ServicesInfoProviderFactory providerFactory = ServicesInfoProviderFactory.getInstance();

  private JsonParserFactory jsonParserFactory = JsonParserFactory.getInstance();

  private ClientBuilder clientBuilder = ClientBuilder.newBuilder();

  public PodCertificateJerseyClient(Integer connectTimeout, Integer readTimeout) {
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

    return clientBuilder.withConfig(clientConfig).build();
  }

  @Override
  public PodCertificate getPodPublicCertificate() throws PodCertificateException {
    Client client = initHttpClient();

    try {
      ServicesInfoProvider provider = providerFactory.getComponent();

      WebTarget target = client.target(provider.getPodBaseUrl()).path(POD_CERT_PATH);
      Response response = target.request().accept(WILDCARD).get();

      if (Response.Status.OK.getStatusCode() == response.getStatus()) {
        String json = response.readEntity(String.class);

        JsonParser parser = jsonParserFactory.getComponent();
        return parser.writeToObject(json, PodCertificate.class);
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

}
