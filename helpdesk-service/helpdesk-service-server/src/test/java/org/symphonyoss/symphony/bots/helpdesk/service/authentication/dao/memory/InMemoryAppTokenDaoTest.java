package org.symphonyoss.symphony.bots.helpdesk.service.authentication.dao.memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.symphonyoss.symphony.apps.authentication.tokens.model.AppToken;
import org.symphonyoss.symphony.bots.helpdesk.service.authentication.model.HelpDeskAppToken;

/**
 * Unit tests for {@link InMemoryAppTokenDao}
 *
 * Created by rsanchez on 13/03/18.
 */
public class InMemoryAppTokenDaoTest {

  private static final String APP_ID = "ID";

  private static final String APP_TOKEN = "ABCD";

  private InMemoryAppTokenDao dao = new InMemoryAppTokenDao();

  @Test
  public void testDAO() {
    assertNull(dao.getAppToken(APP_TOKEN));

    AppToken token = new AppToken();
    token.setAppId(APP_ID);
    token.setAppToken(APP_TOKEN);

    HelpDeskAppToken appToken = new HelpDeskAppToken(token);

    assertEquals(appToken, dao.saveAppToken(appToken));
    assertEquals(appToken, dao.getAppToken(APP_TOKEN));
  }
}
