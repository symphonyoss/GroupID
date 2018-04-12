package org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.authenticator.model.Token;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.TicketResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.HelpDeskApiException;
import org.symphonyoss.symphony.bots.helpdesk.service.api.TicketApi;
import org.symphonyoss.symphony.bots.helpdesk.service.client.ApiClient;
import org.symphonyoss.symphony.bots.helpdesk.service.client.ApiException;
import org.symphonyoss.symphony.bots.helpdesk.service.client.Configuration;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.WebApplicationException;

/**
 * Helper class to deal with stream stuff.
 *
 * Created by rsanchez on 01/03/18.
 */
@Component
public class TicketHelper {

  private static final String AUTHORIZATION_HEADER = "Bearer %s";

  private final TicketApi ticketApi;

  private final String groupId;

  private final TestRestTemplate restTemplate;

  private final SymphonyClient symphonyClient;

  public TicketHelper(HelpDeskBotConfig config, TestRestTemplate restTemplate,
      SymphonyClient symphonyClient) {
    ApiClient apiClient = Configuration.getDefaultApiClient();
    apiClient.setBasePath(config.getHelpDeskServiceUrl());

    this.ticketApi = new TicketApi(apiClient);
    this.groupId = config.getGroupId();
    this.restTemplate = restTemplate;
    this.symphonyClient = symphonyClient;
  }

  /**
   * Retrieves the latest unserviced ticket.
   * @return Unserviced ticket
   */
  public Optional<Ticket> getUnservicedTicket() {
    try {
      List<Ticket> ticketList = ticketApi.searchTicket(getAuthorizationHeader(), groupId, null, null);

      return ticketList.stream()
          .filter(ticket -> ticket.getState().equals(TicketClient.TicketStateType.UNSERVICED.getState()))
          .sorted(Comparator.comparing(Ticket::getQuestionTimestamp).reversed())
          .findFirst();
    } catch (ApiException e) {
      throw new HelpDeskApiException("Failed to search tickets", e);
    }
  }

  /**
   * Retrieves the latest claimed ticket by an agent.
   * @param agentId Agent id
   * @return Unresolved ticket
   */
  public Optional<Ticket> getClaimedTicket(Long agentId) {
    try {
      List<Ticket> ticketList = ticketApi.searchTicket(getAuthorizationHeader(), groupId, null, null);

      return ticketList.stream()
          .filter(ticket -> TicketClient.TicketStateType.UNRESOLVED.getState().equals(ticket.getState()))
          .filter(ticket -> ticket.getAgent() != null && ticket.getAgent().getUserId().equals(agentId))
          .sorted(Comparator.comparing(Ticket::getQuestionTimestamp).reversed())
          .findFirst();
    } catch (ApiException e) {
      throw new HelpDeskApiException("Failed to search tickets", e);
    }
  }

  /**
   * Retrieves the first claimed ticket by an agent.
   * @param agentId Agent id
   * @return Unresolved ticket
   */
  public Optional<Ticket> getFirstClaimedTicket(Long agentId) {
    try {
      List<Ticket> ticketList = ticketApi.searchTicket(getAuthorizationHeader(), groupId, null, null);

      return ticketList.stream()
          .filter(ticket -> TicketClient.TicketStateType.UNRESOLVED.getState().equals(ticket.getState()))
          .filter(ticket -> ticket.getAgent() != null && ticket.getAgent().getUserId().equals(agentId))
          .sorted(Comparator.comparing(Ticket::getQuestionTimestamp))
          .findFirst();
    } catch (ApiException e) {
      throw new HelpDeskApiException("Failed to search tickets", e);
    }
  }

  /**
   * Retrieves the latest claimed ticket.
   * @return Unresolved ticket
   */
  public Optional<Ticket> getClaimedTicket() {
    try {
      List<Ticket> ticketList = ticketApi.searchTicket(getAuthorizationHeader(), groupId, null, null);

      return ticketList.stream()
          .filter(ticket -> ticket.getState().equals(TicketClient.TicketStateType.UNRESOLVED.getState()))
          .sorted(Comparator.comparing(Ticket::getQuestionTimestamp).reversed())
          .findFirst();
    } catch (ApiException e) {
      throw new HelpDeskApiException("Failed to search tickets", e);
    }
  }

  /**
   * Accepts the ticket.
   *
   * @param ticketId Ticket id
   * @param agentId Agent id
   * @return Ticket API response
   */
  public TicketResponse acceptTicket(String ticketId, Long agentId) {
    String url = "/v1/ticket/{ticketId}/accept";

    Map<String, String> uriParams = new HashMap<>();
    uriParams.put("ticketId", ticketId);

    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
        .queryParam("agentId", agentId);

    HttpHeaders authorizationHeader = getHttpAuthHeader();
    HttpEntity<String> entity = new HttpEntity<>(authorizationHeader);

    ResponseEntity<TicketResponse> responseEntity =
        restTemplate.exchange(builder.buildAndExpand(uriParams).toUri(), HttpMethod.POST, entity,
            TicketResponse.class);

    if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
      throw new WebApplicationException(responseEntity.getStatusCodeValue());
    }

    return responseEntity.getBody();
  }

  public TicketResponse joinTicketRoom(String ticketId, Long agentId) {
    String url = "/v1/ticket/{ticketId}/join";

    Map<String, String> uriParams = new HashMap<>();
    uriParams.put("ticketId", ticketId);

    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
        .queryParam("agentId", agentId);

    HttpHeaders authorizationHeader = getHttpAuthHeader();
    HttpEntity<String> entity = new HttpEntity<>(authorizationHeader);

    ResponseEntity<TicketResponse> responseEntity =
        restTemplate.exchange(builder.buildAndExpand(uriParams).toUri(), HttpMethod.POST, entity,
            TicketResponse.class);

    if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
      throw new WebApplicationException(responseEntity.getStatusCodeValue());
    }

    return responseEntity.getBody();
  }

  private String getAuthorizationHeader() {
    Token sessionToken = symphonyClient.getSymAuth().getSessionToken();
    return String.format(AUTHORIZATION_HEADER, sessionToken.getToken());
  }

  private HttpHeaders getHttpAuthHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.set(AUTHORIZATION, getAuthorizationHeader());

    return headers;
  }

}
