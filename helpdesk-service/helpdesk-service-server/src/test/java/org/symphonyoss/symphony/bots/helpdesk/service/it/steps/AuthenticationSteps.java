package org.symphonyoss.symphony.bots.helpdesk.service.it.steps;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.apps.authentication.servlets.model.AppInfo;
import org.symphonyoss.symphony.apps.authentication.servlets.model.ErrorResponse;
import org.symphonyoss.symphony.apps.authentication.servlets.model.JwtInfo;
import org.symphonyoss.symphony.apps.authentication.tokens.model.AppToken;
import org.symphonyoss.symphony.bots.helpdesk.service.it.MockPodCertificateService;

/**
 * Steps to validate app authentication API.
 *
 * Created by rsanchez on 15/03/18.
 */
@Component
public class AuthenticationSteps {

  private static final String APP_ID = "app1";

  private static final String APP_TOKEN = "TOKENA";

  private static final String SYMPHONY_TOKEN = "TOKENS";

  private static final Long USER_ID = 1234L;

  private ResponseEntity<AppToken> tokenResponseEntity;

  private ResponseEntity<ErrorResponse> errorResponseEntity;

  private ResponseEntity<String> userResponseEntity;

  private String appToken;

  private String symphonyToken;

  private String jwt;

  private final TestRestTemplate restTemplate;

  private final MockPodCertificateService podCertificateService;

  public AuthenticationSteps(TestRestTemplate restTemplate, MockPodCertificateService podCertificateService) {
    this.restTemplate = restTemplate;
    this.podCertificateService = podCertificateService;
  }

  @Given("a generated JWT from user")
  public void createUserJWT() {
    this.jwt = podCertificateService.generateUserJWT(APP_ID, USER_ID);
  }

  @When("I call the app authentication API without appId")
  public void callAuthWithoutAppId() {
    AppInfo appInfo = new AppInfo();
    errorResponseEntity = restTemplate.postForEntity("/v1/application/authenticate", appInfo,
        ErrorResponse.class);
  }

  @When("I call the app authentication API with valid appId")
  public void callAuthWithValidKeystore() {
    AppInfo appInfo = new AppInfo();
    appInfo.setAppId(APP_ID);

    tokenResponseEntity = restTemplate.postForEntity("/v1/application/authenticate", appInfo,
        AppToken.class);
  }

  @When("I call the tokens validation API without appId")
  public void callTokenValidationWithoutAppId() {
    AppToken tokens = new AppToken();

    errorResponseEntity = restTemplate.postForEntity("/v1/application/tokens/validate", tokens,
        ErrorResponse.class);
  }

  @When("I call the tokens validation API without appToken")
  public void callTokenValidationWithoutAppToken() {
    AppToken tokens = createAppToken();
    tokens.setAppToken(null);

    errorResponseEntity = restTemplate.postForEntity("/v1/application/tokens/validate", tokens,
        ErrorResponse.class);
  }

  @When("I call the tokens validation API without symphonyToken")
  public void callTokenValidationWithoutSymphonyToken() {
    AppToken tokens = createAppToken();
    tokens.setSymphonyToken(null);

    errorResponseEntity = restTemplate.postForEntity("/v1/application/tokens/validate", tokens,
        ErrorResponse.class);
  }

  @When("I call the tokens validation API with invalid appToken")
  public void callTokenValidationWithoInvalidAppToken() {
    AppToken tokens = createAppToken();
    tokens.setAppToken(APP_TOKEN);

    errorResponseEntity = restTemplate.postForEntity("/v1/application/tokens/validate", tokens,
        ErrorResponse.class);
  }

  @When("I call the tokens validation API with invalid symphonyToken")
  public void callTokenValidationWithInvalidSymphonyToken() {
    AppToken tokens = createAppToken();
    tokens.setSymphonyToken(SYMPHONY_TOKEN);

    errorResponseEntity = restTemplate.postForEntity("/v1/application/tokens/validate", tokens,
        ErrorResponse.class);
  }

  @When("I call the tokens validation API with valid tokens")
  public void callTokenValidationWithValidTokens() {
    AppToken tokens = createAppToken();

    tokenResponseEntity = restTemplate.postForEntity("/v1/application/tokens/validate", tokens,
        AppToken.class);
  }

  @When("I call the JWT validation API without data")
  public void callJwtValidationWithoutData() {
    JwtInfo jwt = new JwtInfo();

    errorResponseEntity = restTemplate.postForEntity("/v1/application/jwt/validate", jwt,
        ErrorResponse.class);
  }

  @When("I call the JWT validation API with invalid token")
  public void callJwtValidationWithInvalidData() {
    JwtInfo jwt = new JwtInfo();
    jwt.setJwt("-");

    errorResponseEntity = restTemplate.postForEntity("/v1/application/jwt/validate", jwt,
        ErrorResponse.class);
  }

  @When("I call the JWT validation API with valid token")
  public void callJwtValidation() {
    JwtInfo jwtInfo = new JwtInfo();
    jwtInfo.setJwt(jwt);

    userResponseEntity = restTemplate.postForEntity("/v1/application/jwt/validate", jwtInfo,
        String.class);
  }

  @Then("check the app token was created")
  public void checkTokenExists() {
    checkTokens();
  }

  @Then("check the tokens were validated")
  public void checkTokensValidated() {
    checkTokens();
  }

  private void checkTokens() {
    assertEquals(HttpStatus.OK, tokenResponseEntity.getStatusCode());

    AppToken expected = tokenResponseEntity.getBody();

    this.appToken = expected.getAppToken();
    this.symphonyToken = expected.getSymphonyToken();

    assertNotNull(appToken);
    assertNotNull(symphonyToken);
  }

  @Then("check the user id")
  public void checkUserId() {
    assertEquals(HttpStatus.OK, userResponseEntity.getStatusCode());
    assertEquals(USER_ID.toString(), userResponseEntity.getBody());
  }

  @Then("receive a bad request error from app authentication API caused by $id missing in the body")
  public void errorBadRequestAppAuthentication(String paramName) {
    errorBadRequest(paramName);
  }

  @Then("receive a bad request error from tokens validation API caused by $id missing in the body")
  public void errorBadRequestTokensValidation(String paramName) {
    errorBadRequest(paramName);
  }

  @Then("receive a bad request error from JWT validation API caused by $id missing in the body")
  public void errorBadRequestJwtValidation(String paramName) {
    errorBadRequest(paramName);
  }

  private void errorBadRequest(String paramName) {
    assertEquals(HttpStatus.BAD_REQUEST, errorResponseEntity.getStatusCode());
    assertEquals("Missing the required parameter " + paramName, errorResponseEntity.getBody().getMessage());
  }

  @Then("receive an unauthorized error")
  public void errorUnauthorized() {
    assertEquals(HttpStatus.UNAUTHORIZED, errorResponseEntity.getStatusCode());
  }

  @Then("receive an internal server error")
  public void errorInternalServerError() {
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponseEntity.getStatusCode());
  }

  private AppToken createAppToken() {
    AppToken tokens = new AppToken();
    tokens.setAppId(APP_ID);
    tokens.setAppToken(appToken);
    tokens.setSymphonyToken(symphonyToken);
    return tokens;
  }

}
