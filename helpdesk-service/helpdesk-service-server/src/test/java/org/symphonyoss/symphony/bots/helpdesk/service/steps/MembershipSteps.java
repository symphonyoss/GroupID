package org.symphonyoss.symphony.bots.helpdesk.service.steps;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;

/**
 * Created by alexandre-silva-daitan on 28/02/18.
 */
@Component
public class MembershipSteps {

  private static final Logger LOGGER = LoggerFactory.getLogger(MembershipSteps.class);
  public static final String CLIENT = MembershipClient.MembershipType.CLIENT.getType();
  public static final String AGENT = MembershipClient.MembershipType.AGENT.getType();
  public static final String GROUP_ID = "GROUP_ID";

  @Autowired
  private TestRestTemplate restTemplate;

  private ResponseEntity<Membership> responseEntity;

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

    responseEntity = restTemplate.postForEntity("/v1/membership", agent, Membership.class);


  }

  @When("I call the create membership API for agent without group id")
  public void callCreateMembershipAgentIthoutGroupId() {
    Membership agent = createMembershipAgent();
    agent.setGroupId(null);

    responseEntity = restTemplate.postForEntity("/v1/membership", agent, Membership.class);
  }

  @When("I call the create membership API for agent without type")
  public void callCreateMembershipAgentIthoutType() {
    Membership agent = createMembershipAgent();
    agent.setType(null);

    responseEntity = restTemplate.postForEntity("/v1/membership", agent, Membership.class);
  }

  @When("I call the search membership API for agent")
  public void callSearchMembershipAgent() {

    responseEntity = restTemplate.getForEntity("/v1/membership/{groupid}/{id}", Membership.class,
        GROUP_ID, 654321);
  }

  @When("I call the search membership API for client")
  public void callSearchMembershipClient() {

    responseEntity = restTemplate.getForEntity("/v1/membership/{groupid}/{id}", Membership.class,
        GROUP_ID, 123456);
  }

  @When("I call the search membership API for unexistent client")
  public void callSearchMembershipForUnexistentClient() {

    responseEntity = restTemplate.getForEntity("/v1/membership/{groupid}/{id}", Membership.class,
        GROUP_ID, 121212);

  }

  @When("I call the search membership API for unexistent path")
  public void callSearchMembershipForUnexistentPath() {

    responseEntity = restTemplate.getForEntity("/v1/membership/{groupid}{id}", Membership.class,
        GROUP_ID, 121212);

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
            Membership.class, GROUP_ID, 654321);

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
            Membership.class, GROUP_ID, 123456);

  }

  @Then("check that membership client was created/founded")
  public void checkMembershipClientCreated() {
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(createMembershipClient(), responseEntity.getBody());
  }

  @Then("check that membership agents was created/founded")
  public void checkMembershipAgentCreated() {
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(createMembershipAgent(), responseEntity.getBody());
  }

  @Then("receive a bad request error")
  public void badRequestError() {
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(new Membership(), responseEntity.getBody());
  }

  @Then("receive a not found error")
  public void NotFoundError() {
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertEquals(new Membership(), responseEntity.getBody());
  }

  @Then("receive a no content response")
  public void noContentResponse() {
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
  }

  @Then("check that agent was updated")
  public void checkAgentUpdated() {
    assertEquals(CLIENT, responseEntity.getBody().getType());
  }

  public Membership createMembershipClient() {
    Membership membership = new Membership();
    membership.setId(123456L);
    membership.setGroupId(GROUP_ID);
    membership.setType(CLIENT);
    return membership;
  }

  public Membership createMembershipAgent() {
    Membership membership = new Membership();
    membership.setId(654321L);
    membership.setGroupId(GROUP_ID);
    membership.setType(AGENT);
    return membership;
  }

}
