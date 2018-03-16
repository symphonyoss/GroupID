package org.symphonyoss.symphony.bots.helpdesk.service.it.steps;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import junit.framework.Assert;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Error;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.model.TicketSearchResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Steps to validate ticket API.
 *
 * Created by rsanchez on 05/03/18.
 */
@Component
public class TicketSteps {

  private static final String TICKET_ID = UUID.randomUUID().toString();
  private static final String GROUP_ID = "GROUP_ID";
  private static final String CLIENT_STREAM_ID = "CLIENT_STREAM_ID";
  private static final String SERVICE_STREAM_ID = "SERVICE_STREAM_ID";
  private static final String CONVERSATION_ID = "CONVERSATION_ID";
  private static final Long TIMESTAMP = System.currentTimeMillis();

  private ResponseEntity<Ticket> responseEntity;

  private ResponseEntity<TicketSearchResponse> searchResponseEntity;

  private ResponseEntity<Error> errorResponseEntity;

  private final TestRestTemplate restTemplate;

  public TicketSteps(TestRestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @When("I call the create ticket API")
  public void callCreateTicket() {
    Ticket ticket = createTicket();
    responseEntity = restTemplate.postForEntity("/v1/ticket", ticket, Ticket.class);
  }

  @When("I call the create ticket API without id")
  public void callCreateTicketWithoutId() {
    Ticket ticket = createTicket();
    ticket.setId(null);
    errorResponseEntity = restTemplate.postForEntity("/v1/ticket", ticket, Error.class);
  }

  @When("I call the create ticket API without group id")
  public void callCreateTicketWIthoutGroupId() {
    Ticket ticket = createTicket();
    ticket.setGroupId(null);
    errorResponseEntity = restTemplate.postForEntity("/v1/ticket", ticket, Error.class);
  }

  @When("I call the create ticket API without state")
  public void callCreateTicketWIthoutState() {
    Ticket ticket = createTicket();
    ticket.setState(null);
    errorResponseEntity = restTemplate.postForEntity("/v1/ticket", ticket, Error.class);
  }

  @When("I call the get ticket API")
  public void callGetTicket() {
    responseEntity = restTemplate.getForEntity("/v1/ticket/{id}", Ticket.class, TICKET_ID);
  }

  @When("I call the get ticket API for unexistent ticket")
  public void callGetTicketForUnexistentTicket() {
    errorResponseEntity = restTemplate.getForEntity("/v1/ticket/{id}", Error.class, GROUP_ID);
  }

  @When("I call the get ticket API for unexistent path")
  public void callGetTicketForUnexistentPath() {
    errorResponseEntity = restTemplate.getForEntity("/v1/ticket/{groupid}/{id}", Error.class,
        GROUP_ID, TICKET_ID);
  }

  @When("I call the search ticket API without groupId")
  public void callSearchTicketWithoutGroupId() {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("/v1/ticket/search")
        .queryParam("clientStreamId", CLIENT_STREAM_ID);

    errorResponseEntity = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, null, Error.class);
  }

  @When("I call the search ticket API with groupId")
  public void callSearchTicketGroupId() {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("/v1/ticket/search")
        .queryParam("groupId", GROUP_ID);

    searchResponseEntity = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, null,
        TicketSearchResponse.class);
  }

  @When("I call the search ticket API with clientStreamId")
  public void callSearchTicketClientStreamId() {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("/v1/ticket/search")
        .queryParam("groupId", GROUP_ID)
        .queryParam("clientStreamId", CLIENT_STREAM_ID);

    searchResponseEntity = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, null,
        TicketSearchResponse.class);
  }

  @When("I call the search ticket API with serviceStreamId")
  public void callSearchTicketServiceStreamId() {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("/v1/ticket/search")
        .queryParam("groupId", GROUP_ID)
        .queryParam("serviceStreamId", SERVICE_STREAM_ID);

    searchResponseEntity = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, null,
        TicketSearchResponse.class);
  }

  @When("I call the search ticket API")
  public void callSearchTicket() {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("/v1/ticket/search")
        .queryParam("groupId", GROUP_ID)
        .queryParam("clientStreamId", CLIENT_STREAM_ID)
        .queryParam("serviceStreamId", SERVICE_STREAM_ID);

    searchResponseEntity = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, null,
        TicketSearchResponse.class);
  }

  @When("I call the search ticket API with invalid groupId")
  public void callSearchTicketInvalidGroupId() {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("/v1/ticket/search")
        .queryParam("groupId", "test")
        .queryParam("clientStreamId", CLIENT_STREAM_ID)
        .queryParam("serviceStreamId", SERVICE_STREAM_ID);

    searchResponseEntity = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, null,
        TicketSearchResponse.class);
  }

  @When("I call the search ticket API with invalid clientStreamId")
  public void callSearchTicketInvalidClientStreamId() {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("/v1/ticket/search")
        .queryParam("groupId", GROUP_ID)
        .queryParam("clientStreamId", "test")
        .queryParam("serviceStreamId", SERVICE_STREAM_ID);

    searchResponseEntity = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, null,
        TicketSearchResponse.class);
  }

  @When("I call the search ticket API with invalid serviceStreamId")
  public void callSearchTicketInvalidServiceStreamId() {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("/v1/ticket/search")
        .queryParam("groupId", GROUP_ID)
        .queryParam("clientStreamId", CLIENT_STREAM_ID)
        .queryParam("serviceStreamId", "test");

    searchResponseEntity = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, null,
        TicketSearchResponse.class);
  }

  @When("call the update ticket API")
  public void callUpdateTicket() {
    Ticket ticket = createTicket();
    ticket.setGroupId("ABCD");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);

    HttpEntity<Ticket> requestEntity = new HttpEntity<>(ticket, headers);
    responseEntity =
        restTemplate.exchange("/v1/ticket/{id}", HttpMethod.PUT, requestEntity,
            Ticket.class, TICKET_ID);
  }

  @When("call the update ticket API with invalid path")
  public void callUpdateTicketWithError() {
    Ticket ticket = createTicket();
    ticket.setGroupId("ABCD");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);

    HttpEntity<Ticket> requestEntity = new HttpEntity<>(ticket, headers);
    errorResponseEntity =
        restTemplate.exchange("/v1/ticket/{groupid}/{id}", HttpMethod.PUT, requestEntity,
            Error.class, GROUP_ID, TICKET_ID);
  }

  @When("call the update ticket API for an unexistent ticket")
  public void callUpdateTicketUnexistentTicket() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);

    HttpEntity<Ticket> requestEntity = new HttpEntity<>(createTicket(), headers);
    errorResponseEntity =
        restTemplate.exchange("/v1/ticket/{id}", HttpMethod.PUT, requestEntity,
            Error.class, GROUP_ID);
  }

  @When("call the delete ticket API for an unexistent ticket")
  public void callDeleteTicketUnexistentTicket() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);

    responseEntity =
        restTemplate.exchange("/v1/ticket/{id}", HttpMethod.DELETE, null,
            Ticket.class, "123");
  }

  @When("call the delete ticket API with wrong parameters")
  public void callDeleteTicketWithWrongParameters() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);

    errorResponseEntity =
        restTemplate.exchange("/v1/ticket/{groupid}/{id}", HttpMethod.DELETE, null,
            Error.class, GROUP_ID, TICKET_ID);
  }

  @When("call the delete ticket API")
  public void callDeleteTicket() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);

    responseEntity =
        restTemplate.exchange("/v1/ticket/{id}", HttpMethod.DELETE, null,
            Ticket.class, TICKET_ID);
  }

  @Then("check that ticket exists")
  public void checkTicketExists() {
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(createTicket(), responseEntity.getBody());
  }

  @Then("check that ticket was found")
  public void checkTicketWasFound() {
    assertEquals(HttpStatus.OK, searchResponseEntity.getStatusCode());

    TicketSearchResponse body = searchResponseEntity.getBody();
    assertEquals(1, body.size());
    assertEquals(createTicket(), body.get(0));
  }

  @Then("check that ticket was not found")
  public void checkTicketWasNotFound() {
    assertEquals(HttpStatus.NO_CONTENT, searchResponseEntity.getStatusCode());
    assertNull(searchResponseEntity.getBody());
  }

  @Then("receive a bad request error from ticket API caused by $id missing in the $body")
  public void errorBadRequest(String paramName, String requiredIn) {
    assertEquals(HttpStatus.BAD_REQUEST, errorResponseEntity.getStatusCode());
    assertEquals("This request requires a " + paramName +
        " to be provided with the " + requiredIn + ".", errorResponseEntity.getBody().getMessage());
  }

  @Then("receive a bad request error caused by ticket not found")
  public void errorBadRequestTicketNotFound() {
    assertEquals(HttpStatus.BAD_REQUEST, errorResponseEntity.getStatusCode());
    assertEquals("Ticket not found. Ticket: " + GROUP_ID, errorResponseEntity.getBody().getMessage());
  }

  @Then("receive a not found error from ticket API")
  public void notFoundError() {
    assertEquals(HttpStatus.NOT_FOUND, errorResponseEntity.getStatusCode());
    assertEquals("No message available", errorResponseEntity.getBody().getMessage());
  }

  @Then("receive a no content response from ticket API")
  public void noContentResponse() {
    assertEquals(HttpStatus.NO_CONTENT, errorResponseEntity.getStatusCode());
    assertNull(errorResponseEntity.getBody());
  }

  @Then("check that ticket was updated")
  public void checkTicketUpdated() {
    assertEquals("ABCD", responseEntity.getBody().getGroupId());
  }

  @Then("check that ticket no longer exists")
  public void checkTicketDeleted() {
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
  }

  @Then("receive successfull message even there is no ticket")
  public void checkAgentUnexistentDeleted() {
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @When("I call the create ticket API for ticket duplicated")
  public void callCreateTicketDuplicated() {
    Ticket ticket = createTicket();
    errorResponseEntity = restTemplate.postForEntity("/v1/ticket", ticket, Error.class);
  }

  @Then("receive a bad request error caused by ticket already exists")
  public void errorTicketAlreadyExists() {
    assertEquals(HttpStatus.BAD_REQUEST, errorResponseEntity.getStatusCode());
    assertEquals("Ticket already exists. TicketId: " + TICKET_ID, errorResponseEntity.getBody().getMessage());
  }

  private Ticket createTicket() {
    Ticket ticket = new Ticket();
    ticket.setId(TICKET_ID);
    ticket.setGroupId(GROUP_ID);
    ticket.setClientStreamId(CLIENT_STREAM_ID);
    ticket.setServiceStreamId(SERVICE_STREAM_ID);
    ticket.setState(TicketClient.TicketStateType.UNSERVICED.getState());
    ticket.setQuestionTimestamp(TIMESTAMP);
    ticket.setShowHistory(true);
    ticket.setConversationID(CONVERSATION_ID);
    return ticket;
  }

}
