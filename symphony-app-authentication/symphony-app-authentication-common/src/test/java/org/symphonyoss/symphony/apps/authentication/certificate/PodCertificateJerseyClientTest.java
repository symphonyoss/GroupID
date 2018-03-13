package org.symphonyoss.symphony.apps.authentication.certificate;

import static javax.ws.rs.core.MediaType.WILDCARD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.apps.authentication.certificate.exception.PodCertificateException;
import org.symphonyoss.symphony.apps.authentication.certificate.model.PodCertificate;
import org.symphonyoss.symphony.apps.authentication.endpoints.ServicesInfoProvider;
import org.symphonyoss.symphony.apps.authentication.endpoints.ServicesInfoProviderFactory;
import org.symphonyoss.symphony.apps.authentication.json.JacksonParser;
import org.symphonyoss.symphony.apps.authentication.json.JsonParserFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Response;

/**
 * Unit tests for {@link PodCertificateJerseyClient}
 *
 * Created by rsanchez on 10/01/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class PodCertificateJerseyClientTest {

  private static final String MOCK_POD_URL = "https://mock.symphony.com/pod";

  private static final String POD_CERT_PATH = "v1/podcert";

  private static final String MOCK_CERT = "-----BEGIN CERTIFICATE-----MIIEQDCCAyigAwIBAgIVAKmVB"
      + "Tro/GCF6u842F9q7JdYVYguMA0GCSqGSIb3DQEBCwUAMGQxJjAkBgNVBAMMHUlzc3VpbmcgQ2VydGlmaWNhdGU"
      + "gQXV0aG9yaXR5MS0wKwYDVQQKDCRTeW1waG9ueSBDb21tdW5pY2F0aW9uIFNlcnZpY2VzIExMQy4xCzAJBgN"
      + "VBAYTAlVTMB4XDTE3MDYyODE2MDMxMVoXDTM3MDYyODE2MDMxMVowZDEmMCQG-----END CERTIFICATE-----";

  private static final String MOCK_JSON_RESPONSE = "{\"certificate\":\"" + MOCK_CERT + "\"}";

  @Mock
  private ClientBuilder clientBuilder;

  @Mock
  private Client httpClient;

  @Mock
  private WebTarget target;

  @Mock
  private Invocation.Builder builder;

  @Mock
  private Response response;

  @Mock
  private ServicesInfoProvider provider;

  @Mock
  private ServicesInfoProviderFactory factory;

  @Mock
  private JsonParserFactory jsonParserFactory;

  @InjectMocks
  private PodCertificateJerseyClient client = new PodCertificateJerseyClient(1000, 1000);

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);

    doReturn(provider).when(factory).getComponent();
    doReturn(MOCK_POD_URL).when(provider).getPodBaseUrl();

    doReturn(clientBuilder).when(clientBuilder).withConfig(any(Configuration.class));
    doReturn(httpClient).when(clientBuilder).build();
    doReturn(target).when(httpClient).target(MOCK_POD_URL);
    doReturn(target).when(target).path(POD_CERT_PATH);
    doReturn(builder).when(target).request();
    doReturn(builder).when(builder).accept(WILDCARD);
    doReturn(response).when(builder).get();

    doReturn(new JacksonParser()).when(jsonParserFactory).getComponent();
  }

  @Test
  public void testUnexpectedError() {
    doThrow(IllegalStateException.class).when(provider).getPodBaseUrl();

    try {
      client.getPodPublicCertificate();
      fail();
    } catch (PodCertificateException e) {
      assertEquals("Unexpected error to retrieve POD certificate", e.getMessage());
      verify(httpClient, times(1)).close();
    }
  }

  @Test
  public void testResponseNOK() {
    doReturn(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).when(response).getStatus();

    try {
      client.getPodPublicCertificate();
      fail();
    } catch (PodCertificateException e) {
      assertEquals("Fail to retrieve POD certificate", e.getMessage());
      verify(httpClient, times(1)).close();
    }
  }

  @Test
  public void testResponseOK() throws PodCertificateException {
    doReturn(Response.Status.OK.getStatusCode()).when(response).getStatus();
    doReturn(MOCK_JSON_RESPONSE).when(response).readEntity(String.class);

    PodCertificate result = client.getPodPublicCertificate();

    assertEquals(MOCK_CERT, result.getCertificate());
  }
}
