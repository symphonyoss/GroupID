package org.symphonyoss.symphony.apps.authentication;

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
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.apps.authentication.endpoints.ServicesInfoProvider;
import org.symphonyoss.symphony.apps.authentication.endpoints.ServicesInfoProviderFactory;
import org.symphonyoss.symphony.apps.authentication.json.JacksonParser;
import org.symphonyoss.symphony.apps.authentication.json.JsonParserFactory;
import org.symphonyoss.symphony.apps.authentication.keystore.KeystoreProvider;
import org.symphonyoss.symphony.apps.authentication.keystore.KeystoreProviderFactory;
import org.symphonyoss.symphony.apps.authentication.keystore.model.KeystoreSettings;
import org.symphonyoss.symphony.apps.authentication.tokens.model.AppToken;

import java.security.KeyStore;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Response;

/**
 * Unit tests for {@link AppAuthenticationApiClient}
 *
 * Created by rsanchez on 07/03/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class AppAuthenticationApiClientTest {

  private static final String APP_ID = "APP_ID";

  private static final String APP_TOKEN = "ABCD";

  private static final String PASSWORD = "Test123";

  private static final String BASE_URL = "https://test.symphony.com";

  private static final String AUTHENTICATE_PATH = "/v1/authenticate/extensionApp";

  private static final String MOCK_RESPONSE = "{ \"appId\": \"APP_ID\", \"appToken\": \"ABCD\" }";

  @Mock
  private ClientBuilder clientBuilder;

  @Mock
  private KeystoreProvider keystoreProvider;

  @Mock
  private KeyStore keyStore;

  @Mock
  private Client client;

  @Mock
  private ServicesInfoProvider provider;

  @Mock
  private WebTarget target;

  @Mock
  private Invocation.Builder request;

  @Mock
  private Response response;

  @InjectMocks
  private AppAuthenticationApiClient appAuthenticationApiClient;

  @Before
  public void init() {
    AppToken token = new AppToken();
    token.setAppToken(APP_TOKEN);

    KeystoreProviderFactory.getInstance().setComponent(keystoreProvider);
    ServicesInfoProviderFactory.getInstance().setComponent(provider);
    JsonParserFactory.getInstance().setComponent(new JacksonParser());

    KeystoreSettings appKeystore = new KeystoreSettings();
    appKeystore.setData(keyStore);
    appKeystore.setPassword(PASSWORD);

    doReturn(appKeystore).when(keystoreProvider).getApplicationKeystore(APP_ID);

    doReturn(BASE_URL).when(provider).getSessionAuthBaseUrl();

    doReturn(clientBuilder).when(clientBuilder).withConfig(any(Configuration.class));
    doReturn(clientBuilder).when(clientBuilder).keyStore(keyStore, PASSWORD);
    doReturn(client).when(clientBuilder).build();
    doReturn(target).when(client).target(BASE_URL);
    doReturn(target).when(target).path(AUTHENTICATE_PATH);
    doReturn(request).when(target).request();
    doReturn(request).when(request).accept(WILDCARD);
    doReturn(response).when(request).post(Entity.json(token));
  }

  @Test
  public void testFailureRequest() {
    doThrow(ClientErrorException.class).when(request).post(any(Entity.class));

    try {
      appAuthenticationApiClient.authenticate(APP_ID, APP_TOKEN);
      fail();
    } catch (AppAuthenticationException e) {
      assertEquals("Unexpected error to authenticate app: " + APP_ID, e.getMessage());
    }

    verify(client, times(1)).close();
  }

  @Test
  public void testRequestNOK() {
    doReturn(Response.Status.UNAUTHORIZED.getStatusCode()).when(response).getStatus();

    String message = "error message";
    doReturn(message).when(response).toString();

    try {
      appAuthenticationApiClient.authenticate(APP_ID, APP_TOKEN);
      fail();
    } catch (AppAuthenticationException e) {
      assertEquals("Failure to authenticate app: " + APP_ID + " due to http error: " + message,
          e.getMessage());
    }

    verify(client, times(1)).close();
  }

  @Test
  public void testRequestOK() throws AppAuthenticationException {
    doReturn(Response.Status.OK.getStatusCode()).when(response).getStatus();
    doReturn(MOCK_RESPONSE).when(response).readEntity(String.class);

    AppToken expected = new AppToken();
    expected.setAppId(APP_ID);
    expected.setAppToken(APP_TOKEN);

    AppToken result = appAuthenticationApiClient.authenticate(APP_ID, APP_TOKEN);
    assertEquals(expected, result);

    verify(client, times(1)).close();
  }
}
