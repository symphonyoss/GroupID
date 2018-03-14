package org.symphonyoss.symphony.apps.authentication.tokens;

import org.symphonyoss.symphony.apps.authentication.tokens.model.AppToken;

/**
 * Mock class for {@link StoreTokensProvider}
 *
 * Created by rsanchez on 06/03/18.
 */
public class MockStoreTokensProvider implements StoreTokensProvider {

  private AppToken token;

  @Override
  public void saveAppAuthenticationToken(AppToken appToken) {
    this.token = appToken;
  }

  @Override
  public AppToken getAppAuthenticationToken(String applicationToken) {
    return token.getAppToken().equals(applicationToken) ? token : null;
  }

}
