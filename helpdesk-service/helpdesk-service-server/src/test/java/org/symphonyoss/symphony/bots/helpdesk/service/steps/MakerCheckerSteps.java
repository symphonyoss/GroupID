package org.symphonyoss.symphony.bots.helpdesk.service.steps;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
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

  public static final String OPENED = MakercheckerClient.AttachmentStateType.OPENED.getState();
  public static final String MOCK_SERVICE_STREAM_ID = "MOCK_SERVICE_STREAM_ID";
  public static final String MOCK_MAKERCHECKER_ID = "MOCK_MAKERCHECKER_ID";
  public static final String DISPLAY_NAME = "DISPLAY_NAME";
  private ResponseEntity<Makerchecker> responseEntity;

  @Autowired
  private TestRestTemplate restTemplate;

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

    responseEntity =
        restTemplate.postForEntity("/v1/makerchecker", makerchecker, Makerchecker.class);

  }

  @When("call the create makerchecker API with invalid stream id")
  public void callMakercheckerAPIWithInvalidStreamId() {

    Makerchecker makerchecker = createMakerchecker();
    makerchecker.setStreamId(null);

    responseEntity =
        restTemplate.postForEntity("/v1/makerchecker", makerchecker, Makerchecker.class);

  }

  @When("call the create makerchecker API with invalid maker id")
  public void callMakercheckerAPIWithInvalidMakerId() {

    Makerchecker makerchecker = createMakerchecker();
    makerchecker.makerId(null);

    responseEntity =
        restTemplate.postForEntity("/v1/makerchecker", makerchecker, Makerchecker.class);

  }

  @When("call the create makerchecker API with invalid state")
  public void callMakercheckerAPIWithInvalidState() {

    Makerchecker makerchecker = createMakerchecker();
    makerchecker.setId("NEW_MOCK_MAKERCHECKER_ID");
    makerchecker.setState(MakercheckerClient.AttachmentStateType.DENIED.getState());

    responseEntity =
        restTemplate.postForEntity("/v1/makerchecker", makerchecker, Makerchecker.class);

  }

  @When("call the create makerchecker API with same id")
  public void callMakercheckerAPIWithSameId() {

    Makerchecker makerchecker = createMakerchecker();

    responseEntity =
        restTemplate.postForEntity("/v1/makerchecker", makerchecker, Makerchecker.class);

  }

  @When("call the retrieve makerchecker API")
  public void callGetMakercheckerAPI() {

    responseEntity =
        restTemplate.getForEntity("/v1/makerchecker/{id}", Makerchecker.class,
            MOCK_MAKERCHECKER_ID);
  }

  @When("call the read makerchecker API with invalid id")
  public void callGetMakercheckerAPIWithInvalidId() {

    responseEntity =
        restTemplate.getForEntity("/v1/makerchecker/{id}", Makerchecker.class, "INVALID_ID");
  }

  @When("call the read makerchecker API with invalid parameter")
  public void callGetMakercheckerAPIWithInvalidParameter() {

    responseEntity =
        restTemplate.getForEntity("/v1/makerchecker/{id}", Makerchecker.class, "");

  }

  @When("call the update makerchecker API and returns bad request")
  public void callUpdateMakerCheckerWithErros() {

    Makerchecker makerchecker = createMakerchecker();
    makerchecker.setMakerId(null);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);

    HttpEntity<Makerchecker> requestEntity = new HttpEntity<>(makerchecker, headers);

    responseEntity =
        restTemplate.exchange("/v1/makerchecker/{id}", HttpMethod.PUT, requestEntity,
            Makerchecker.class, MOCK_MAKERCHECKER_ID);
  }

  @When("call the update makerchecker API")
  public void callUpdateMakerChecker() {

    Makerchecker makerchecker = createMakerchecker();
    makerchecker.setChecker(createUserInfo());

    restTemplate.put("/v1/makerchecker/{id}", makerchecker, MOCK_MAKERCHECKER_ID);

    responseEntity =
        restTemplate.getForEntity("/v1/makerchecker/{id}", Makerchecker.class,
            MOCK_MAKERCHECKER_ID);
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

  @Then("check that makerchecker was created/founded")
  public void checkMakercheckerWasCreatedFounded() {
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(createMakerchecker(), responseEntity.getBody());
  }

  @Then("check that makerchecker was updated")
  public void checkMakercheckerWasUpdated() {
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(createUserInfo(), responseEntity.getBody().getChecker());
  }

  @Then("receive a bad request error")
  public void errorBadRequest() {
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }

  @Then("receive an internal server error")
  public void errorInternalServerError() {
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
  }

  @Then("receive a no content message")
  public void noContentInfo() {
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
  }

  @Then("receive a method not allowed error")
  public void errorMethodNotAllowed() {
    assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());
  }
}
