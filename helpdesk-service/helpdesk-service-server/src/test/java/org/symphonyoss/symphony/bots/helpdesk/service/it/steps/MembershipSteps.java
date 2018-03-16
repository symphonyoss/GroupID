package org.symphonyoss.symphony.bots.helpdesk.service.it.steps;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Error;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;

/**
 * Steps to validate membership API.
 *
 * Created by alexandre-silva-daitan on 28/02/18.
 */
@Component
public class MembershipSteps {

  private static final String CLIENT = MembershipClient.MembershipType.CLIENT.getType();
  private static final String AGENT = MembershipClient.MembershipType.AGENT.getType();
  private static final String GROUP_ID = "GROUP_ID";
  private static final long AGENT_ID = 321L;
  private static final long CLIENT_ID = 123L;

  private ResponseEntity<Membership> responseEntity;

  private ResponseEntity<Error> errorResponseEntity;

  private final TestRestTemplate restTemplate;

  public MembershipSteps(TestRestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @When("I call the create membership API for client")
  public void callCreateMembershipClient() {
    Membership client = createMembershipClient();
    responseEntity = restTemplate.postForEntity("/v1/membership", client, Membership.class);
  }

  @When("I call the create membership API for agent")
  public void callCreateMembershipAgent() {
    Membership agent = createMembershipAgent();
    responseEntity = restTemplate.postForEntity("/v1/membership", agent, Membership.class);
  }

  @When("I call the create membership API for agent without id")
  public void callCreateMembershipAgentIthoutId() {
    Membership agent = createMembershipAgent();
    agent.setId(null);
    errorResponseEntity = restTemplate.postForEntity("/v1/membership", agent, Error.class);
  }

  @When("I call the create membership API for agent without group id")
  public void callCreateMembershipAgentIthoutGroupId() {
    Membership agent = createMembershipAgent();
    agent.setGroupId(null);
    errorResponseEntity = restTemplate.postForEntity("/v1/membership", agent, Error.class);
  }

  @When("I call the create membership API for agent without type")
  public void callCreateMembershipAgentIthoutType() {
    Membership agent = createMembershipAgent();
    agent.setType(null);
    errorResponseEntity = restTemplate.postForEntity("/v1/membership", agent, Error.class);
  }

  @When("I call the search membership API for agent")
  public void callSearchMembershipAgent() {
    responseEntity = restTemplate.getForEntity("/v1/membership/{groupid}/{id}", Membership.class,
        GROUP_ID, AGENT_ID);
  }

  @When("I call the search membership API for client")
  public void callSearchMembershipClient() {
    responseEntity = restTemplate.getForEntity("/v1/membership/{groupid}/{id}", Membership.class,
        GROUP_ID, CLIENT_ID);
  }

  @When("I call the search membership API for unexistent client")
  public void callSearchMembershipForUnexistentClient() {
    errorResponseEntity = restTemplate.getForEntity("/v1/membership/{groupid}/{id}", Error.class,
        GROUP_ID, 212L);
  }

  @When("I call the search membership API for unexistent path")
  public void callSearchMembershipForUnexistentPath() {
    errorResponseEntity = restTemplate.getForEntity("/v1/membership/{groupid}{id}", Error.class,
        GROUP_ID, 212L);
  }

  @When("call the update membership API for agent")
  public void callUpdateMembershipAgent() {
    Membership agent = createMembershipAgent();
    agent.setType(CLIENT);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);

    HttpEntity<Membership> requestEntity = new HttpEntity<>(agent, headers);
    responseEntity =
        restTemplate.exchange("/v1/membership/{groupid}/{id}", HttpMethod.PUT, requestEntity,
            Membership.class, GROUP_ID, AGENT_ID);
  }

  @When("call the update membership API for client")
  public void callUpdateMembershipClientWithError() {
    Membership client = createMembershipClient();
    client.setType(AGENT);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);

    HttpEntity<Membership> requestEntity = new HttpEntity<>(client, headers);
    responseEntity =
        restTemplate.exchange("/v1/membership/{groupid}{id}", HttpMethod.PUT, requestEntity,
            Membership.class, GROUP_ID, CLIENT_ID);
  }

  @When("call the delete membership API for an unexistent agent")
  public void callDeleteMembershipUnexistentMembership() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);

    HttpEntity<Membership> requestEntity = new HttpEntity<>(createMembershipAgent(), headers);
    responseEntity =
        restTemplate.exchange("/v1/membership/{groupid}/{id}", HttpMethod.DELETE, requestEntity,
            Membership.class, GROUP_ID, 304L);
  }

  @When("call the delete membership API with wrong parameters")
  public void callDeleteMembershipWithWrongParameters() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);

    HttpEntity<Membership> requestEntity = new HttpEntity<>(createMembershipAgent(), headers);
    errorResponseEntity =
        restTemplate.exchange("/v1/membership/{groupid}/{id}", HttpMethod.DELETE, requestEntity,
            Error.class, GROUP_ID, null);
  }

  @When("call the delete membership API for agent")
  public void callDeleteMembershipAgent() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);

    HttpEntity<Membership> requestEntity = new HttpEntity<>(createMembershipAgent(), headers);
    responseEntity =
        restTemplate.exchange("/v1/membership/{groupid}/{id}", HttpMethod.DELETE, requestEntity,
            Membership.class, GROUP_ID, AGENT_ID);
  }

  @Then("check that client membership exists")
  public void checkMembershipClientExists() {
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(createMembershipClient(), responseEntity.getBody());
  }

  @Then("check that agent membership exists")
  public void checkMembershipAgentExists() {
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(createMembershipAgent(), responseEntity.getBody());
  }

  @Then("receive a bad request error from membership API caused by $id missing in the $body")
  public void errorBadRequest(String paramName, String requiredIn) {
    assertEquals(HttpStatus.BAD_REQUEST, errorResponseEntity.getStatusCode());
    assertEquals("This request requires a " + paramName +
        " to be provided with the " + requiredIn + ".", errorResponseEntity.getBody().getMessage());
  }

  @Then("receive a not found error from membership API")
  public void NotFoundError() {
    assertEquals(HttpStatus.NOT_FOUND, errorResponseEntity.getStatusCode());
    assertEquals("No message available", errorResponseEntity.getBody().getMessage());
  }

  @Then("receive a no content response from membership API")
  public void noContentResponse() {
    assertEquals(HttpStatus.NO_CONTENT, errorResponseEntity.getStatusCode());
    assertNull(errorResponseEntity.getBody());
  }

  @Then("check that agent was updated")
  public void checkAgentUpdated() {
    assertEquals(CLIENT, responseEntity.getBody().getType());
  }

  @Then("check that agent no longer exists")
  public void checkAgentDeleted() {
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
  }

  @Then("receive successfull message even there is no agent")
  public void checkAgentUnexistentDeleted() {
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @When("I call the create membership API for client duplicated")
  public void callCreateMembershipClientDuplicated() {
    Membership client = createMembershipClient();
    errorResponseEntity = restTemplate.postForEntity("/v1/membership", client, Error.class);
  }

  @Then("receive a bad request error caused by membership already exists")
  public void errorMembershipAlreadyExists() {
    assertEquals(HttpStatus.BAD_REQUEST, errorResponseEntity.getStatusCode());
    assertEquals("Membership already exists. GroupId: GROUP_ID, userId: 123",
        errorResponseEntity.getBody().getMessage());
  }

  private Membership createMembershipClient() {
    Membership membership = new Membership();
    membership.setId(CLIENT_ID);
    membership.setGroupId(GROUP_ID);
    membership.setType(CLIENT);
    return membership;
  }

  private Membership createMembershipAgent() {
    Membership agent = new Membership();
    agent.setId(AGENT_ID);
    agent.setGroupId(GROUP_ID);
    agent.setType(AGENT);
    return agent;
  }
}
