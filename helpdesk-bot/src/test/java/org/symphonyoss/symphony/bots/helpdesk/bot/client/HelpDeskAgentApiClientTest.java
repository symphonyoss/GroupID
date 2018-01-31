package org.symphonyoss.symphony.bots.helpdesk.bot.client;

import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.agent.invoker.ApiException;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * Unit tests for {@link HelpDeskAgentApiClient}
 *
 * Created by rsanchez on 31/01/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class HelpDeskAgentApiClientTest {

  @Mock
  private Response response;

  @Mock
  private GenericType genericType;

  private HelpDeskAgentApiClient client = new HelpDeskAgentApiClient();

  @Test
  public void testNullResponse() throws ApiException {
    assertNull(client.deserialize(null, genericType));
  }

  @Test
  public void testNullType() throws ApiException {
    assertNull(client.deserialize(response, null));
  }

  @Test
  public void testNullContentLength() throws ApiException {
    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

    headers.add(CONTENT_TYPE, MediaType.APPLICATION_JSON);

    doReturn(headers).when(response).getHeaders();

    client.deserialize(response, genericType);

    verify(response, times(1)).readEntity(genericType);
  }

  @Test
  public void testContentLength() throws ApiException {
    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

    headers.add(CONTENT_LENGTH, 1000);
    headers.add(CONTENT_TYPE, MediaType.APPLICATION_JSON);

    doReturn(headers).when(response).getHeaders();

    client.deserialize(response, genericType);

    verify(response, times(1)).readEntity(genericType);
  }

  @Test
  public void testZeroContentLength() throws ApiException {
    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

    headers.add(CONTENT_LENGTH, 0);

    doReturn(headers).when(response).getHeaders();

    assertNull(client.deserialize(response, genericType));
    verify(response, never()).readEntity(genericType);
  }
}
