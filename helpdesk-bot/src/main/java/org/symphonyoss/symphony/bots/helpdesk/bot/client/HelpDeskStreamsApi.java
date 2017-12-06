package org.symphonyoss.symphony.bots.helpdesk.bot.client;

import org.symphonyoss.symphony.pod.api.StreamsApi;
import org.symphonyoss.symphony.pod.invoker.ApiClient;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.invoker.Pair;
import org.symphonyoss.symphony.pod.model.V3RoomAttributes;
import org.symphonyoss.symphony.pod.model.V3RoomDetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.GenericType;

/**
 * Created by rsanchez on 05/12/17.
 */
public class HelpDeskStreamsApi extends StreamsApi {

  private static final String MEDIA_TYPE = "application/json";

  private ApiClient apiClient;

  public HelpDeskStreamsApi(ApiClient apiClient) {
    super(apiClient);
    this.apiClient = apiClient;
  }

  public V3RoomDetail v3RoomCreatePost(V3RoomAttributes payload, String sessionToken) throws ApiException {
    if(payload == null) {
      throw new ApiException(400, "Missing the required parameter \'payload\' when calling v4RoomCreatePost");
    } else if(sessionToken == null) {
      throw new ApiException(400, "Missing the required parameter \'sessionToken\' when calling v2RoomCreatePost");
    } else {
      String path = "/v3/room/create".replaceAll("\\{format\\}", "json");

      List<Pair> queryParams = new ArrayList<>();
      Map<String, String> headerParams = new HashMap<>();
      Map<String, Object> formParams = new HashMap<>();

      if(sessionToken != null) {
        headerParams.put("sessionToken", this.apiClient.parameterToString(sessionToken));
      }

      GenericType<V3RoomDetail> returnType = new GenericType<V3RoomDetail>() {};

      return this.apiClient.invokeAPI(path, "POST", queryParams, payload, headerParams, formParams,
          MEDIA_TYPE, MEDIA_TYPE, new String[] {}, returnType);
    }
  }

}
