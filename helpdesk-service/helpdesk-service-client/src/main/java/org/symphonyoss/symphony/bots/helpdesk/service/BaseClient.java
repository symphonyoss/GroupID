package org.symphonyoss.symphony.bots.helpdesk.service;

/**
 * Base HTTP client for HelpDesk API.
 *
 * Created by rsanchez on 03/04/18.
 */
public abstract class BaseClient {

  private static final String AUTHORIZATION_HEADER = "Bearer %s";

  public String getAuthorizationHeader(String jwt) {
    return String.format(AUTHORIZATION_HEADER, jwt);
  }

}
