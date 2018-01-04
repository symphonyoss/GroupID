package org.symphonyoss.symphony.bots.helpdesk.service.it.steps;

import static org.junit.Assert.assertEquals;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;

/**
 * Created by rsanchez on 02/01/18.
 */
@Component
public class TestSteps {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestSteps.class);

  @Autowired
  private TestRestTemplate restTemplate;

  @Given("a system state")
  public void givenSystemState() {
    LOGGER.info("Given system state");
  }

  @When("I do something")
  public void doSomething() {
    ResponseEntity<Ticket> result =
        restTemplate.getForEntity("/v1/ticket/{id}", Ticket.class, "ABCDEFG");

    assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

    Ticket ticket = new Ticket();
    ticket.setId("ABCDEFG");
    ticket.setGroupId("group");
    ticket.setClientStreamId("clientstream");
    ticket.setServiceStreamId("servicestream");
    ticket.setState("OPENED");

    ResponseEntity<Ticket> createdTicket =
        restTemplate.postForEntity("/v1/ticket", ticket, Ticket.class);

    assertEquals(HttpStatus.OK, createdTicket.getStatusCode());
    assertEquals(ticket, createdTicket.getBody());

    LOGGER.info("Do something");
  }

  @Then("system is in a different state")
  public void thenSystemDifferentState() {
    LOGGER.info("Different state");
  }

}
