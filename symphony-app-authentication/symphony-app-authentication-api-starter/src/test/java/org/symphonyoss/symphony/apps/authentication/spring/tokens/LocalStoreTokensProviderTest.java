package org.symphonyoss.symphony.apps.authentication.spring.tokens;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.symphonyoss.symphony.apps.authentication.tokens.model.AppToken;

/**
 * Unit tests for {@link LocalStoreTokensProvider}
 *
 * Created by rsanchez on 09/03/18.
 */
public class LocalStoreTokensProviderTest {

  private static final String APP_ID = "APP_ID";

  private static final String APP_TOKEN = "ABCD";

  @Test
  public void testGetApplicationToken() {
    LocalStoreTokensProvider provider = new LocalStoreTokensProvider();

    assertNull(provider.getAppAuthenticationToken(APP_TOKEN));

    AppToken appToken = new AppToken();
    appToken.setAppId(APP_ID);
    appToken.setAppToken(APP_TOKEN);

    provider.saveAppAuthenticationToken(appToken);

    assertEquals(appToken, provider.getAppAuthenticationToken(APP_TOKEN));
  }

}
