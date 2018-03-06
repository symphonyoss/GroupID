package org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.TicketResponse;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

/**
 * Helper class to deal with stream stuff.
 *
 * Created by crepache on 05/03/18.
 */
@Component
public class MakerCheckerHelper {

  private final TestRestTemplate restTemplate;

  public MakerCheckerHelper(TestRestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Approve attachment
   *
   * @param attachmentId Ticket id
   * @param agentId Agent id
   * @return Ticket API response
   */
  public ResponseEntity approveAttachment(String attachmentId, Long agentId) {
    String url = "/v1/makerchecker/{makerCheckerId}/approve";

    Map<String, String> uriParams = new HashMap<>();
    uriParams.put("makerCheckerId", attachmentId);

    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
        .queryParam("userId", agentId);

    ResponseEntity<TicketResponse> responseEntity =
        restTemplate.exchange(builder.buildAndExpand(uriParams).toUri(), HttpMethod.POST, null,
            TicketResponse.class);

    if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
      throw new WebApplicationException(responseEntity.getStatusCodeValue());
    }

    return responseEntity;
  }

  /**
   * Deny attachment
   *
   * @param attachmentId Ticket id
   * @param agentId Agent id
   * @return Ticket API response
   */
  public ResponseEntity denyAttachment(String attachmentId, Long agentId) {
    String url = "/v1/makerchecker/{makerCheckerId}/deny";

    Map<String, String> uriParams = new HashMap<>();
    uriParams.put("makerCheckerId", attachmentId);

    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
        .queryParam("userId", agentId);

    ResponseEntity<TicketResponse> responseEntity =
        restTemplate.exchange(builder.buildAndExpand(uriParams).toUri(), HttpMethod.POST, null,
            TicketResponse.class);

    if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
      throw new WebApplicationException(responseEntity.getStatusCodeValue());
    }

    return responseEntity;
  }
}
