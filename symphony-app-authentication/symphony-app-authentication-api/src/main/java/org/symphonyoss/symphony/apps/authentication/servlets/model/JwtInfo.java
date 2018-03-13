package org.symphonyoss.symphony.apps.authentication.servlets.model;

/**
 * Holds the JSON Web Token.
 *
 * Created by rsanchez on 07/03/18.
 */
public class JwtInfo {

  private String jwt;

  public String getJwt() {
    return jwt;
  }

  public void setJwt(String jwt) {
    this.jwt = jwt;
  }
}
