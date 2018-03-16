package org.symphonyoss.symphony.apps.authentication.spring.tokens;

import org.symphonyoss.symphony.apps.authentication.tokens.StoreTokensProvider;
import org.symphonyoss.symphony.apps.authentication.tokens.model.AppToken;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation class to retrieve and store the application and symphony tokens from/to a
 * local storage (in-memory).
 *
 * Created by rsanchez on 08/03/18.
 */
public class LocalStoreTokensProvider implements StoreTokensProvider {

  private final Map<String, AppToken> tokens = new ConcurrentHashMap<>();

  @Override
  public void saveAppAuthenticationToken(AppToken appToken) {
    this.tokens.put(appToken.getAppToken(), appToken);
  }

  @Override
  public AppToken getAppAuthenticationToken(String applicationToken) {
    return this.tokens.get(applicationToken);
  }

}
