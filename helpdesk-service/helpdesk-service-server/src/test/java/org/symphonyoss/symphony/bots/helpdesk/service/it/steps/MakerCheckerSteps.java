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
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client.MakercheckerClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Error;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Makerchecker;
import org.symphonyoss.symphony.bots.helpdesk.service.model.UserInfo;

/**
 * Steps to validate makerchecker API.
 *
 * Created by alexandre-silva-daitan on 23/02/18.
 */
@Component
public class MakerCheckerSteps {

  private static final String OPENED = MakercheckerClient.AttachmentStateType.OPENED.getState();
  private static final String MOCK_SERVICE_STREAM_ID = "MOCK_SERVICE_STREAM_ID";
  private static final String MOCK_MAKERCHECKER_ID = "MOCK_MAKERCHECKER_ID";
  private static final String DISPLAY_NAME = "DISPLAY_NAME";

  private ResponseEntity<Makerchecker> responseEntity;

  private ResponseEntity<Error> errorResponseEntity;

  private final TestRestTemplate restTemplate;

  public MakerCheckerSteps(TestRestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @When("call the create makerchecker API")
  public void callMakercheckerAPI() {

    Makerchecker makerchecker = createMakerchecker();

    responseEntity =
        restTemplate.postForEntity("/v1/makerchecker", makerchecker, Makerchecker.class);
  }

  @When("call the create makerchecker API with invalid id")
  public void callMakercheckerAPIWithInvalidId() {

    Makerchecker makerchecker = createMakerchecker();
    makerchecker.setId(null);

    errorResponseEntity =
        restTemplate.postForEntity("/v1/makerchecker", makerchecker, Error.class);
  }

  @When("call the create makerchecker API with invalid stream id")
  public void callMakercheckerAPIWithInvalidStreamId() {

    Makerchecker makerchecker = createMakerchecker();
    makerchecker.setStreamId(null);

    errorResponseEntity =
        restTemplate.postForEntity("/v1/makerchecker", makerchecker, Error.class);
  }

  @When("call the create makerchecker API with invalid maker id")
  public void callMakercheckerAPIWithInvalidMakerId() {

    Makerchecker makerchecker = createMakerchecker();
    makerchecker.makerId(null);

    errorResponseEntity =
        restTemplate.postForEntity("/v1/makerchecker", makerchecker, Error.class);
  }

  @When("call the create makerchecker API with invalid state")
  public void callMakercheckerAPIWithInvalidState() {

    Makerchecker makerchecker = createMakerchecker();
    makerchecker.setId("NEW_MOCK_MAKERCHECKER_ID");
    makerchecker.setState(MakercheckerClient.AttachmentStateType.DENIED.getState());

    errorResponseEntity =
        restTemplate.postForEntity("/v1/makerchecker", makerchecker, Error.class);

  }

  @When("call the create makerchecker API with same id")
  public void callMakercheckerAPIWithSameId() {
    this.errorResponseEntity = null;

    Makerchecker makerchecker = createMakerchecker();

    errorResponseEntity =
        restTemplate.postForEntity("/v1/makerchecker", makerchecker, Error.class);
  }

  @When("call the read makerchecker API")
  public void callGetMakercheckerAPI() {
    responseEntity =
        restTemplate.getForEntity("/v1/makerchecker/{id}", Makerchecker.class,
            MOCK_MAKERCHECKER_ID);
  }

  @When("call the read makerchecker API with invalid id")
  public void callGetMakercheckerAPIWithInvalidId() {
    errorResponseEntity =
        restTemplate.getForEntity("/v1/makerchecker/{id}", Error.class, "INVALID_ID");
  }

  @When("call the read makerchecker API with invalid parameter")
  public void callGetMakercheckerAPIWithInvalidParameter() {
    errorResponseEntity =
        restTemplate.getForEntity("/v1/makerchecker/{id}", Error.class, "");
  }

  @When("call the update makerchecker API with invalid makerId")
  public void callUpdateMakerCheckerWithErros() {
    Makerchecker makerchecker = createMakerchecker();
    makerchecker.setMakerId(null);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);

    HttpEntity<Makerchecker> requestEntity = new HttpEntity<>(makerchecker, headers);

    errorResponseEntity =
        restTemplate.exchange("/v1/makerchecker/{id}", HttpMethod.PUT, requestEntity,
            Error.class, MOCK_MAKERCHECKER_ID);
  }

  @When("call the update makerchecker API")
  public void callUpdateMakerChecker() {
    Makerchecker makerchecker = createMakerchecker();
    makerchecker.setChecker(createUserInfo());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);

    HttpEntity<Makerchecker> requestEntity = new HttpEntity<>(makerchecker, headers);

    responseEntity =
        restTemplate.exchange("/v1/makerchecker/{id}", HttpMethod.PUT, requestEntity,
            Makerchecker.class, MOCK_MAKERCHECKER_ID);
  }

  private Makerchecker createMakerchecker() {
    Makerchecker makerchecker = new Makerchecker();
    makerchecker.setState(OPENED);
    makerchecker.setStreamId(MOCK_SERVICE_STREAM_ID);
    makerchecker.setId(MOCK_MAKERCHECKER_ID);
    makerchecker.setMakerId(0123456L);
    return makerchecker;
  }

  private UserInfo createUserInfo() {
    UserInfo userInfo = new UserInfo();
    userInfo.setUserId(1234L);
    userInfo.setDisplayName(DISPLAY_NAME);
    return userInfo;
  }

  @Then("check that makerchecker exists")
  public void checkMakercheckerExists() {
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(createMakerchecker(), responseEntity.getBody());
  }

  @Then("check that makerchecker was updated")
  public void checkMakercheckerWasUpdated() {
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(createUserInfo(), responseEntity.getBody().getChecker());
  }

  @Then("receive a bad request error caused by $id missing in the $body")
  public void errorBadRequest(String paramName, String requiredIn) {
    assertEquals(HttpStatus.BAD_REQUEST, errorResponseEntity.getStatusCode());
    assertEquals("This request requires a " + paramName +
        " to be provided with the " + requiredIn + ".", errorResponseEntity.getBody().getMessage());
  }

  @Then("receive an internal server error")
  public void errorInternalServerError() {
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponseEntity.getStatusCode());
    assertEquals("Failed to create new makerchecker. Id: MOCK_MAKERCHECKER_ID",
        errorResponseEntity.getBody().getMessage());
  }

  @Then("receive a no content message")
  public void noContentInfo() {
    assertEquals(HttpStatus.NO_CONTENT, errorResponseEntity.getStatusCode());
    assertNull(errorResponseEntity.getBody());
  }

  @Then("receive a method not allowed error")
  public void errorMethodNotAllowed() {
    assertEquals(HttpStatus.METHOD_NOT_ALLOWED, errorResponseEntity.getStatusCode());
    assertEquals("Request method 'GET' not supported", errorResponseEntity.getBody().getMessage());
  }
}
