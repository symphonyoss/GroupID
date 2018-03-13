package org.symphonyoss.symphony.apps.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.apps.authentication.tokens.StoreTokensProvider;
import org.symphonyoss.symphony.apps.authentication.tokens.StoreTokensProviderFactory;
import org.symphonyoss.symphony.apps.authentication.tokens.model.AppToken;

/**
 * Unit tests for {@link AppAuthenticationService}
 *
 * Created by rsanchez on 07/03/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class AppAuthenticationServiceTest {

  private static final String APP_ID = "APP_ID";

  private static final String APP_TOKEN = "ABCD";

  private static final String SYMPHONY_TOKEN = "EFGH";

  @Mock
  private StoreTokensProvider provider;

  @Mock
  private AppAuthenticationApiClient apiClient;

  @InjectMocks
  private AppAuthenticationService service;

  @Before
  public void init() {
    StoreTokensProviderFactory.getInstance().setComponent(provider);
  }

  @Test
  public void testAuthenticate() throws AppAuthenticationException {
    AppToken expected = new AppToken();
    expected.setAppId(APP_ID);
    expected.setAppToken(APP_TOKEN);

    doReturn(expected).when(apiClient).authenticate(eq(APP_ID), anyString());

    AppToken result = service.authenticate(APP_ID);

    verify(provider, times(1)).saveAppAuthenticationToken(expected);
    assertEquals(expected, result);
  }

  @Test
  public void testNullToken() {
    assertFalse(service.isValidTokenPair(APP_TOKEN, SYMPHONY_TOKEN));
  }

  @Test
  public void testInvalidToken() {
    doReturn(new AppToken()).when(provider).getAppAuthenticationToken(APP_TOKEN);
    assertFalse(service.isValidTokenPair(APP_TOKEN, SYMPHONY_TOKEN));
  }

  @Test
  public void testValidToken() {
    AppToken token = new AppToken();
    token.setAppId(APP_ID);
    token.setAppToken(APP_TOKEN);
    token.setSymphonyToken(SYMPHONY_TOKEN);

    doReturn(token).when(provider).getAppAuthenticationToken(APP_TOKEN);
    assertTrue(service.isValidTokenPair(APP_TOKEN, SYMPHONY_TOKEN));
  }

}
