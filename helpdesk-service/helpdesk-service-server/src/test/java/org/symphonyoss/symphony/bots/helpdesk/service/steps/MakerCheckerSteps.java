package org.symphonyoss.symphony.bots.helpdesk.service.steps;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.jbehave.core.annotations.Given;
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
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client.MakercheckerClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Makerchecker;
import org.symphonyoss.symphony.bots.helpdesk.service.model.UserInfo;

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
  }

  @When("call the create makerchecker API")
  public void callMakercheckerAPI() {

    Makerchecker makerchecker = createMakerchecker();

    ResponseEntity<Makerchecker> responseEntity = restTemplate.postForEntity("/v1/makerchecker" , makerchecker, Makerchecker.class);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(makerchecker, responseEntity.getBody());

    LOGGER.info("Calling the API");

  }

  @When("call the create makerchecker API with invalid id")
  public void callMakercheckerAPIWithInvalidId() {

    Makerchecker makerchecker = createMakerchecker();
    makerchecker.setId(null);

    ResponseEntity<Makerchecker> responseEntity = restTemplate.postForEntity("/v1/makerchecker" , makerchecker, Makerchecker.class);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    LOGGER.info("Calling the API");

  }

  @When("call the create makerchecker API with invalid stream id")
  public void callMakercheckerAPIWithInvalidStreamId() {

    Makerchecker makerchecker = createMakerchecker();
    makerchecker.setStreamId(null);

    ResponseEntity<Makerchecker> responseEntity = restTemplate.postForEntity("/v1/makerchecker" , makerchecker, Makerchecker.class);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    LOGGER.info("Calling the API");

  }

  @When("call the create makerchecker API with invalid maker id")
  public void callMakercheckerAPIWithInvalidMakerId() {

    Makerchecker makerchecker = createMakerchecker();
    makerchecker.makerId(null);

    ResponseEntity<Makerchecker> responseEntity = restTemplate.postForEntity("/v1/makerchecker" , makerchecker, Makerchecker.class);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    LOGGER.info("Calling the API");

  }

  @When("call the create makerchecker API with invalid state")
  public void callMakercheckerAPIWithInvalidState() {

    Makerchecker makerchecker = createMakerchecker();
    makerchecker.setId("NEW_MOCK_MAKERCHECKER_ID");
    makerchecker.setState(MakercheckerClient.AttachmentStateType.DENIED.getState());

    ResponseEntity<Makerchecker> responseEntity = restTemplate.postForEntity("/v1/makerchecker" , makerchecker, Makerchecker.class);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

    LOGGER.info("Calling the API");

  }

  @When("call the create makerchecker API with same id")
  public void callMakercheckerAPIWithSameId() {

    Makerchecker makerchecker = createMakerchecker();

    ResponseEntity<Makerchecker> responseEntity = restTemplate.postForEntity("/v1/makerchecker" , makerchecker, Makerchecker.class);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

    LOGGER.info("Calling the API");

  }

  @When("call the read makerchecker API")
  public void callGetMakercheckerAPI() {

    ResponseEntity<Makerchecker> responseEntity = restTemplate.getForEntity("/v1/makerchecker/{id}" , Makerchecker.class, "MOCK_MAKERCHECKER_ID");

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());

    LOGGER.info("Calling the API");

  }

  @When("call the read makerchecker API with invalid id")
  public void callGetMakercheckerAPIWithInvalidId() {

    ResponseEntity<Makerchecker> responseEntity = restTemplate.getForEntity("/v1/makerchecker/{id}" , Makerchecker.class, "INVALID_ID");

    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());

    LOGGER.info("Calling the API");

  }

  @When("call the read makerchecker API with invalid parameter")
  public void callGetMakercheckerAPIWithInvalidParameter() {

    ResponseEntity<Makerchecker> responseEntity = restTemplate.getForEntity("/v1/makerchecker/{id}" , Makerchecker.class, "");

    assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());

    LOGGER.info("Calling the API");

  }

  @When("call the update makerchecker API and returns bad request")
  public void callUpdateMakerCheckerWithErros() {

    Makerchecker makerchecker = createMakerchecker();
    makerchecker.setMakerId(666666L);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);

    HttpEntity<Makerchecker> requestEntity = new HttpEntity<>(makerchecker, headers);


    ResponseEntity<Makerchecker> responseEntity = restTemplate.exchange("/v1/makerchecker/{id}", HttpMethod.PUT, requestEntity, Makerchecker.class, "MOCK_MAKERCHECKER_ID");
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    LOGGER.info("Calling the API");

  }

  @When("call the update makerchecker API")
  public void callUpdateMakerChecker() {

    Makerchecker makerchecker = createMakerchecker();
    makerchecker.setChecker(createUserInfo());

    restTemplate.put("/v1/makerchecker/{id}", makerchecker, "MOCK_MAKERCHECKER_ID");

    ResponseEntity<Makerchecker> responseEntity = restTemplate.getForEntity("/v1/makerchecker/{id}" , Makerchecker.class, "MOCK_MAKERCHECKER_ID");

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(createUserInfo(), responseEntity.getBody().getChecker());

    LOGGER.info("Calling the API");

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

  public UserInfo createUserInfo() {
    UserInfo userInfo = new UserInfo();
    userInfo.setUserId(1234L);
    userInfo.setDisplayName("DISPLAY_NAME");
    return userInfo;
  }

  @Then("receive a successful created message")
  public void logSuccessfulCreatedMessage() {
    LOGGER.info("Makerchecker created successfully");
  }

  @Then("receive a successful founded message")
  public void logSuccessfulFoundedMessage() {
    LOGGER.info("Makerchecker founded successfully");
  }

  @Then("receive a successful updated message")
  public void logSuccessfulUpdatedMessage() {
    LOGGER.info("Makerchecker updated successfully");
  }

  @Then("receive a created error message")
  public void logErrorMessage() {
    LOGGER.info("Makerchecker not created");
  }

  @Then("receive an updated error message")
  public void logUpdatedErrorMessage() {
    LOGGER.info("Makerchecker not updated");
  }

  @Then("receive a founded error message")
  public void logFoundedErrorMessage() {
    LOGGER.info("Makerchecker not founded");
  }

}
