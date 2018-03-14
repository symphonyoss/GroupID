package org.symphonyoss.symphony.apps.authentication.tokens;

import org.symphonyoss.symphony.apps.authentication.tokens.model.AppToken;

/**
 * Interface to retrieve and store the application and symphony tokens from/to a persistent storage.
 *
 * Created by rsanchez on 06/03/18.
 */
public interface StoreTokensProvider {

  /**
   * Save application authentication data.
   * @param appToken Pair of tokens - application and Symphony tokens.
   */
  void saveAppAuthenticationToken(AppToken appToken);

  /**
   * Find application authentication data.r
   * @param applicationToken Application token.
   * @return Pair of tokens - application and Symphony tokens.
   */
  AppToken getAppAuthenticationToken(String applicationToken);

}
