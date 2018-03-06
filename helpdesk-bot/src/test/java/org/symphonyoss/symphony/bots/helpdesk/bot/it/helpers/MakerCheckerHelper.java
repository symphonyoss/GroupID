package org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.TicketResponse;

import java.net.MalformedURLException;
import java.net.URL;
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
   * Approve or Deny attachment
   *
   * @param url Url to Approve or Deny attachment
   * @param agentId Agent id
   * @return ResponseEntity
   */
  public ResponseEntity actionAttachment(String url, Long agentId) throws MalformedURLException {
    URL context = new URL(url);
    url = context.getPath().replace("/helpdesk-bot", "");

    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
        .queryParam("userId", agentId);

    ResponseEntity<TicketResponse> responseEntity =
        restTemplate.exchange(builder.buildAndExpand().toUri(), HttpMethod.POST, null,
            TicketResponse.class);

    if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
      throw new WebApplicationException(responseEntity.getStatusCodeValue());
    }

    return responseEntity;
  }
}
