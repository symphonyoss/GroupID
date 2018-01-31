package org.symphonyoss.symphony.bots.helpdesk.bot.client;

import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;

import org.symphonyoss.symphony.agent.invoker.ApiClient;
import org.symphonyoss.symphony.agent.invoker.ApiException;

import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

/**
 * HelpDesk API client for agent service.
 *
 * Created by rsanchez on 31/01/18.
 */
public class HelpDeskAgentApiClient extends ApiClient {

  @Override
  public <T> T deserialize(Response response, GenericType<T> returnType) throws ApiException {
    if (response == null || returnType == null) {
      return null;
    }

    List<Object> contentLength = response.getHeaders().get(CONTENT_LENGTH);

    if (contentLength != null && !contentLength.isEmpty()) {
      long length = Long.valueOf(contentLength.get(0).toString());

      if (length == 0) {
        return null;
      }
    }

    return super.deserialize(response, returnType);
  }

}
