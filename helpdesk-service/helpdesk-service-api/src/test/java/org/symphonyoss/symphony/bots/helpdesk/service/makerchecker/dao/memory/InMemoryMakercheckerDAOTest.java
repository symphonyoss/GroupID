package org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.dao.memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client.MakercheckerClient;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.exception
    .MakercheckerNotFoundException;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Makerchecker;
import org.symphonyoss.symphony.bots.helpdesk.service.model.UserInfo;

/**
 * Created by alexandre-silva-daitan on 29/12/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class InMemoryMakercheckerDAOTest {

  private static final String MOCK_MAKERCHECKER_ID = "XJW9H3XPCU";

  private static final String MOCK_NEW_MAKERCHECKER_ID = "XJW9H3XP00";

  private static final Long MOCK_MAKER_ID = 10651518946916l;

  private static final String MOCK_SERVICE_STREAM_ID = "RU0dsa0XfcE5SNoJKsXSKX___p-qTQ3XdA";

  private static final String DISPLAY_NAME = "DISPLAY_NAME";

  private static final Long MOCK_USER_ID = 1234567l;

  private static final Long MEMBERSHIP_ID_MOCK = 999999l;

  private static final String GROUP_ID_MOCK = "GROUP_ID";

  @InjectMocks
  private InMemoryMakercheckerDAO inMemoryMakercheckerDAO;

  @Test
  public void createMakerchecker() throws Exception {
    Makerchecker makercheckerMock = makercheckerMock();
    Makerchecker makerchecker = inMemoryMakercheckerDAO.createMakerchecker(makercheckerMock);
    assertEquals(makercheckerMock, makerchecker);
  }

  @Test
  public void updateMakerchecker() throws Exception {
    Makerchecker makercheckerMock = makercheckerMock();
    inMemoryMakercheckerDAO.createMakerchecker(makercheckerMock);
    Makerchecker makerchecker = inMemoryMakercheckerDAO.updateMakerchecker(MOCK_MAKERCHECKER_ID, makercheckerMock);
    assertEquals(makercheckerMock, makerchecker);
  }

  @Test(expected = MakercheckerNotFoundException.class)
  public void updateInvalidMakerchecker() throws Exception {
    Makerchecker makercheckerMock = makercheckerMock();
    Makerchecker makerchecker = inMemoryMakercheckerDAO.updateMakerchecker(MOCK_MAKERCHECKER_ID, makercheckerMock);
  }


  @Test
  public void getMakerchecker() throws Exception {
    Makerchecker makercheckerMock = makercheckerMock();
    inMemoryMakercheckerDAO.createMakerchecker(makercheckerMock);
    Makerchecker makerchecker = inMemoryMakercheckerDAO.getMakerchecker(MOCK_MAKERCHECKER_ID);
    assertNotNull(makerchecker);
  }

  @Test
  public void getNoneMakerchecker() throws Exception {
    Makerchecker makercheckerMock = makercheckerMock();
    Makerchecker makerchecker = inMemoryMakercheckerDAO.getMakerchecker(MOCK_MAKERCHECKER_ID);
    assertNull(makerchecker);
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