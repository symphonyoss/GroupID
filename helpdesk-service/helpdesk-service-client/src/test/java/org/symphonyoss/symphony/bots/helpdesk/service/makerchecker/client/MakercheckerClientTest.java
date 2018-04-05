package org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.bots.helpdesk.service.HelpDeskApiException;
import org.symphonyoss.symphony.bots.helpdesk.service.client.ApiClient;
import org.symphonyoss.symphony.bots.helpdesk.service.client.ApiException;
import org.symphonyoss.symphony.bots.helpdesk.service.client.Configuration;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Makerchecker;
import org.symphonyoss.symphony.bots.helpdesk.service.model.UserInfo;

@RunWith(MockitoJUnitRunner.class)
public class MakercheckerClientTest {

  private static final String MOCK_SERVICE_STREAM_ID = "MOCK_SERVICE_STREAM_ID";
  private static final String MOCK_MAKERCHECKER_ID = "MOCK_MAKERCHECKER_ID";
  private static final Long MOCK_MAKER_ID = 123L;
  private static final String DISPLAY_NAME = "DISPLAY_NAME";
  private static final Long MOCK_USER_ID = 456L;
  private static final String ATTACHMENT_ID = "ATTACHMENT_ID";
  private static final String ATTACHMENT_NAME = "ATTACHMENT_NAME";
  private static final String MESSAGE_ID = "MESSAGE_ID";
  private static final Long TIMESTAMP = 1L;
  private static final String JWT = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9."
      + "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9";

  private static final String GROUP_ID = "GROUP_ID";

  private static final String TICKET_SERVICE_URL = "https://localhost/helpdesk-service";

  @Mock
  private ApiClient apiClient;

  private MakercheckerClient makercheckerClient;

  @Before
  public void setUp() throws Exception {
    Configuration.setDefaultApiClient(apiClient);
    makercheckerClient = new MakercheckerClient(GROUP_ID, TICKET_SERVICE_URL);
  }

  @Test
  public void getMakerchecker() throws ApiException {
    doReturn(makercheckerMock()).when(apiClient)
        .invokeAPI(eq("/v1/makerchecker/MOCK_MAKERCHECKER_ID"), eq("GET"), any(), any(), any(),
            any(), any(), any(), any(), any());

    doReturn(MOCK_MAKERCHECKER_ID).when(apiClient).escapeString(MOCK_MAKERCHECKER_ID);

    Makerchecker makerchecker = makercheckerClient.getMakerchecker(JWT, MOCK_MAKERCHECKER_ID);

    assertNotNull(makerchecker);
    assertEquals(makercheckerMock(), makerchecker);
  }

  @Test
  public void getMakercheckerWithNullId() throws ApiException {
    try {
      makercheckerClient.getMakerchecker(JWT, null);
      fail();
    } catch (HelpDeskApiException e) {
      assertEquals("Missing the required parameter 'id' when calling getMakerchecker",
          e.getCause().getMessage());
    }
  }

  @Test
  public void createMakerchecker() throws ApiException {

    doReturn(makercheckerMock()).when(apiClient)
        .invokeAPI(eq("/v1/makerchecker"), eq("POST"), any(), any(), any(), any(), any(), any(),
            any(), any());

    Makerchecker makerchecker =
        makercheckerClient.createMakerchecker(JWT, MOCK_MAKERCHECKER_ID, MOCK_MAKER_ID,
            MOCK_SERVICE_STREAM_ID, ATTACHMENT_ID, ATTACHMENT_NAME, MESSAGE_ID, TIMESTAMP, null);

    assertNotNull(makerchecker);
    assertEquals(makercheckerMock(), makerchecker);
  }

  @Test
  public void createMakerCheckerFail() throws ApiException {
    doThrow(ApiException.class).when(apiClient)
        .invokeAPI(eq("/v1/makerchecker"), eq("POST"), any(), any(), any(), any(), any(), any(),
            any(), any());

    try {
      makercheckerClient.createMakerchecker(JWT, MOCK_MAKERCHECKER_ID, MOCK_MAKER_ID,
          MOCK_SERVICE_STREAM_ID, ATTACHMENT_ID, ATTACHMENT_NAME, MESSAGE_ID, TIMESTAMP, null);
      fail();
    } catch (HelpDeskApiException e) {
      assertEquals("Creating makerchecker failed: " + MOCK_MAKERCHECKER_ID, e.getMessage());
    }
  }

  @Test
  public void updateMakerchecker() throws ApiException {

    doReturn(MOCK_MAKERCHECKER_ID).when(apiClient).escapeString(MOCK_MAKERCHECKER_ID);

    Makerchecker makerchecker = makercheckerMock();
    makerchecker.setChecker(null);

    doReturn(makerchecker).when(apiClient)
        .invokeAPI(eq("/v1/makerchecker/MOCK_MAKERCHECKER_ID"), eq("PUT"), any(), any(), any(), any(), any(), any(), any(), any());

    Makerchecker updatedMakerchecker = makercheckerClient.updateMakerchecker(JWT, makerchecker);

    assertNotNull(updatedMakerchecker);
    assertEquals(makerchecker, updatedMakerchecker);

  }

  @Test
  public void updateMakercheckerWithError() throws ApiException {

    try {
      makercheckerClient.updateMakerchecker(JWT, new Makerchecker());
      fail();
    } catch(HelpDeskApiException e) {
      assertEquals("Missing the required parameter 'id' when calling updateMakerchecker", e.getCause().getMessage());
    }
  }

  private Makerchecker makercheckerMock() {
    Makerchecker makerchecker = new Makerchecker();
    makerchecker.setChecker(userInfoMock());
    makerchecker.setState(MakercheckerClient.AttachmentStateType.OPENED.getState());
    makerchecker.setStreamId(MOCK_SERVICE_STREAM_ID);
    makerchecker.setId(MOCK_MAKERCHECKER_ID);
    makerchecker.setMakerId(MOCK_MAKER_ID);

    return makerchecker;
  }

  private UserInfo userInfoMock() {
    UserInfo userInfo = new UserInfo();
    userInfo.displayName(DISPLAY_NAME);
    userInfo.setUserId(MOCK_USER_ID);

    return userInfo;
  }
}