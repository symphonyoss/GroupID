package org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;

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

  private final String groupId = "GROUP_ID";

  private final String TICKET_SERVICE_URL = "https://localhost/helpdesk-service";

  @Mock
  private ApiClient apiClient;

  private MakercheckerClient makercheckerClient;

  @Before
  public void setUp() throws Exception {
    Configuration.setDefaultApiClient(apiClient);
    makercheckerClient = new MakercheckerClient(groupId, TICKET_SERVICE_URL);
  }

  @Test
  public void getMakerchecker() throws ApiException {
    doReturn(makercheckerMock()).when(apiClient)
        .invokeAPI(any(), eq("GET"), any(), any(), any(), any(), any(), any(), any(), any());

    doReturn(MOCK_MAKERCHECKER_ID).when(apiClient).escapeString(any());

    Makerchecker makerchecker = makercheckerClient.getMakerchecker(groupId);

    assertNotNull(makerchecker);
    assertEquals(makercheckerMock(), makerchecker);

  }

  @Test(expected = HelpDeskApiException.class)
  public void getMakercheckerWithNullId() throws ApiException {

    Makerchecker makerchecker = makercheckerClient.getMakerchecker(null);

  }

  @Test
  public void createMakerchecker() throws ApiException {

    doReturn(makercheckerMock()).when(apiClient)
        .invokeAPI(any(), eq("POST"), any(), any(), any(), any(), any(), any(), any(), any());

    Makerchecker makerchecker =
        makercheckerClient.createMakerchecker(MOCK_MAKERCHECKER_ID, MOCK_MAKER_ID,
            MOCK_SERVICE_STREAM_ID, ATTACHMENT_ID, ATTACHMENT_NAME, MESSAGE_ID, TIMESTAMP, null);

    assertNotNull(makerchecker);
    assertEquals(makercheckerMock(), makerchecker);
  }

  @Test
  public void updateMakerchecker() throws ApiException {

    doReturn(MOCK_MAKERCHECKER_ID).when(apiClient).escapeString(any());

    Makerchecker makerchecker = makercheckerMock();
    makerchecker.setChecker(null);

    doReturn(makerchecker).when(apiClient).invokeAPI(any(),eq("PUT"),any(),any(),any(),any(),any(),any(),any(),any());

    Makerchecker updatedMakerchecker = makercheckerClient.updateMakerchecker(makerchecker);

    assertNotNull(updatedMakerchecker);
    assertEquals(makerchecker,updatedMakerchecker);

  }

  @Test(expected = HelpDeskApiException.class)
  public void updateMakercheckerWithError() throws ApiException {

    Makerchecker updatedMakerchecker = makercheckerClient.updateMakerchecker(new Makerchecker());

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