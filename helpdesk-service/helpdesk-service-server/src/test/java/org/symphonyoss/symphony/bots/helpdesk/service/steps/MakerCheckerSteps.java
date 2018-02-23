package org.symphonyoss.symphony.bots.helpdesk.service.steps;

import static junit.framework.TestCase.assertEquals;

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
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client.MakercheckerClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Makerchecker;

/**
 * Created by alexandre-silva-daitan on 23/02/18.
 */

@Component
public class MakerCheckerSteps {

  private static final Logger LOGGER = LoggerFactory.getLogger(MakerCheckerSteps.class);

  @Autowired
  private TestRestTemplate restTemplate;

  @Given("a system state")
  public void doSomething() {
    LOGGER.info("Given system state");
  }

  @When("call the create makerchecker API")
  public void callMakercheckerAPI() {

    Makerchecker makerchecker = createMakerchecker();

    ResponseEntity<Makerchecker> responseEntity = restTemplate.postForEntity("/v1/makerchecker" , makerchecker, Makerchecker.class);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(makerchecker, responseEntity.getBody());

    LOGGER.info("do something");

  }

  public Makerchecker createMakerchecker() {
    Makerchecker makerchecker = new Makerchecker();
    makerchecker.setState(MakercheckerClient.AttachmentStateType.OPENED.getState());
    makerchecker.setStreamId("MOCK_SERVICE_STREAM_ID");
    makerchecker.setId("MOCK_MAKERCHECKER_ID");
    makerchecker.setMakerId(0123456L);
    makerchecker.checker(null);
    return makerchecker;
  }

  @Then("system is in a different state")
  public void thenSomething() {
    LOGGER.info("Different state");
  }

}
