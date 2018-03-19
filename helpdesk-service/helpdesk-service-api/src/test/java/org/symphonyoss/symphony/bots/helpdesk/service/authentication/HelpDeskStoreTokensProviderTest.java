package org.symphonyoss.symphony.bots.helpdesk.service.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.apps.authentication.tokens.model.AppToken;
import org.symphonyoss.symphony.bots.helpdesk.service.authentication.dao.AppTokenDao;
import org.symphonyoss.symphony.bots.helpdesk.service.authentication.model.HelpDeskAppToken;

/**
 * Unit tests for {@link HelpDeskStoreTokensProvider}
 *
 * Created by rsanchez on 13/03/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class HelpDeskStoreTokensProviderTest {

  private static final String APP_ID = "ID";

  private static final String APP_TOKEN = "ABCD";

  @Mock
  private AppTokenDao dao;

  private HelpDeskStoreTokensProvider provider;

  @Before
  public void init() {
    this.provider = new HelpDeskStoreTokensProvider(dao);
  }

  @Test
  public void testSaveAppAuthenticationToken() {
    AppToken appToken = new AppToken();
    appToken.setAppId(APP_ID);
    appToken.setAppToken(APP_TOKEN);

    provider.saveAppAuthenticationToken(appToken);

    ArgumentCaptor<HelpDeskAppToken> argument = ArgumentCaptor.forClass(HelpDeskAppToken.class);
    verify(dao).saveAppToken(argument.capture());

    assertEquals(APP_ID, argument.getValue().getId());
    assertEquals(APP_TOKEN, argument.getValue().getAppToken());
  }

  @Test
  public void testGetAppAuthenticationNullToken() {
    assertNull(provider.getAppAuthenticationToken(APP_TOKEN));
  }

  @Test
  public void testGetAppAuthenticationExpiredToken() {
    doReturn(new HelpDeskAppToken()).when(dao).getAppToken(APP_TOKEN);
    assertNull(provider.getAppAuthenticationToken(APP_TOKEN));
  }

  @Test
  public void testGetAppAuthenticationToken() {
    AppToken appToken = new AppToken();
    appToken.setAppId(APP_ID);
    appToken.setAppToken(APP_TOKEN);

    doReturn(new HelpDeskAppToken(appToken)).when(dao).getAppToken(APP_TOKEN);

    AppToken result = provider.getAppAuthenticationToken(APP_TOKEN);

    assertEquals(APP_ID, result.getAppId());
    assertEquals(APP_TOKEN, result.getAppToken());
  }
}
