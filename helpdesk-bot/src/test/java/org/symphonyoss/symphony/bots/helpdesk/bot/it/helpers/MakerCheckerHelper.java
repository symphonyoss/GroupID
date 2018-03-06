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
 * Helper class to deal with maker checker stuff.
 *
 * Created by crepache on 05/03/18.
 */
@Component
public class MakerCheckerHelper {

  private static final String HELPDESK_BOT = "/helpdesk-bot";

  private static final String USER_ID = "userId";

  private final TestRestTemplate restTemplate;

  public MakerCheckerHelper(TestRestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Dispatch action to Approve or Deny the attachment
   *
   * @param actionUrl Url to action of the attachment
   * @param agentId Agent id
   * @return ResponseEntity
   */
  public ResponseEntity actionAttachment(String actionUrl, Long agentId) throws MalformedURLException {
    URL context = new URL(actionUrl);
    String url = context.getPath().replace(HELPDESK_BOT, "");

    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
        .queryParam(USER_ID, agentId);

    ResponseEntity<TicketResponse> responseEntity =
        restTemplate.exchange(builder.buildAndExpand().toUri(), HttpMethod.POST, null,
            TicketResponse.class);

    if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
      throw new WebApplicationException(responseEntity.getStatusCodeValue());
    }

    return responseEntity;
  }
}
