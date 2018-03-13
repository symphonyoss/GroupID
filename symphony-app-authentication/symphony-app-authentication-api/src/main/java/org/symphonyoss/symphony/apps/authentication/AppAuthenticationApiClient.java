/**
 * Copyright 2016-2017 Symphony Integrations - Symphony LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.symphonyoss.symphony.apps.authentication;

import static javax.ws.rs.core.MediaType.WILDCARD;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.symphonyoss.symphony.apps.authentication.endpoints.ServicesInfoProvider;
import org.symphonyoss.symphony.apps.authentication.endpoints.ServicesInfoProviderFactory;
import org.symphonyoss.symphony.apps.authentication.json.JsonParser;
import org.symphonyoss.symphony.apps.authentication.json.JsonParserFactory;
import org.symphonyoss.symphony.apps.authentication.keystore.KeystoreProvider;
import org.symphonyoss.symphony.apps.authentication.keystore.KeystoreProviderFactory;
import org.symphonyoss.symphony.apps.authentication.keystore.model.KeystoreSettings;
import org.symphonyoss.symphony.apps.authentication.tokens.model.AppToken;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * API client to authenticate applications on the POD using certificates.
 *
 * Created by rsanchez on 22/02/17.
 */
public class AppAuthenticationApiClient {

  private static final String AUTHENTICATE_PATH = "/v1/authenticate/extensionApp";

  private static final Integer DEFAULT_CONNECT_TIMEOUT = 10000;

  private static final Integer DEFAULT_READ_TIMEOUT = 10000;

  private ServicesInfoProviderFactory servicesInfoProviderFactory = ServicesInfoProviderFactory.getInstance();

  private KeystoreProviderFactory keystoreProviderFactory = KeystoreProviderFactory.getInstance();

  private JsonParserFactory jsonParserFactory = JsonParserFactory.getInstance();

  private ClientBuilder clientBuilder = ClientBuilder.newBuilder();

  /**
   * Initializes HTTP Client given the application ID.
   *
   * @param appId Application identifier
   * @return HTTP client
   */
  private Client initHttpClient(String appId) {
    KeystoreProvider keystoreProvider = keystoreProviderFactory.getComponent();
    KeystoreSettings appKeystore = keystoreProvider.getApplicationKeystore(appId);

    final ClientConfig clientConfig = new ClientConfig();
    clientConfig.property(ClientProperties.CONNECT_TIMEOUT, DEFAULT_CONNECT_TIMEOUT);
    clientConfig.property(ClientProperties.READ_TIMEOUT, DEFAULT_READ_TIMEOUT);

    return clientBuilder.withConfig(clientConfig)
        .keyStore(appKeystore.getData(), appKeystore.getPassword())
        .build();
  }

  /**
   * Authenticates application on the POD.
   *
   * @param appId Application identifier
   * @param appToken Application token
   * @return Token pair that contains application token and symphony token
   * @throws AppAuthenticationException Failure to authenticate application
   */
  public AppToken authenticate(String appId, String appToken) throws AppAuthenticationException {
    Client client = initHttpClient(appId);

    try {
      ServicesInfoProvider provider = servicesInfoProviderFactory.getComponent();

      WebTarget target = client.target(provider.getSessionAuthBaseUrl()).path(AUTHENTICATE_PATH);

      AppToken token = new AppToken();
      token.setAppToken(appToken);

      Response response = target.request().accept(WILDCARD).post(Entity.json(token));

      if (Response.Status.OK.getStatusCode() == response.getStatus()) {
        String json = response.readEntity(String.class);

        JsonParser parser = jsonParserFactory.getComponent();
        return parser.writeToObject(json, AppToken.class);
      } else {
        throw new AppAuthenticationException("Failure to authenticate app: " + appId + " due to "
            + "http error: " + response.toString());
      }
    } catch (AppAuthenticationException e) {
      throw e;
    } catch (Exception e) {
      throw new AppAuthenticationException("Unexpected error to authenticate app: " + appId, e);
    } finally {
      client.close();
    }
  }

}
