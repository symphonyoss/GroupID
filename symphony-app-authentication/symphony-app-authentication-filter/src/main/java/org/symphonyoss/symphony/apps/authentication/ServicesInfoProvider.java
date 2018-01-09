package org.symphonyoss.symphony.apps.authentication;

/**
 * Interface to retrieve the POD and session auth base URL's
 *
 * Created by rsanchez on 09/01/18.
 */
public interface ServicesInfoProvider {

  String getPodBaseUrl();

  String getSessionAuthBaseUrl();

}
