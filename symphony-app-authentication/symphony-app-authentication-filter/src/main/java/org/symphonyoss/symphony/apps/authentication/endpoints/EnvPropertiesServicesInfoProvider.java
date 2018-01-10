package org.symphonyoss.symphony.apps.authentication.endpoints;

import org.symphonyoss.symphony.apps.authentication.utils.PropertiesReader;

/**
 * Implementation class to retrieve the POD and session auth base URL's from system properties or
 * environment variables
 *
 * Created by rsanchez on 10/01/18.
 */
public class EnvPropertiesServicesInfoProvider implements ServicesInfoProvider {

  private static final String POD_HOST = "pod_host";

  private static final String POD_PORT = "pod_port";

  private static final String SESSION_AUTH_HOST = "session_auth_host";

  private static final String SESSION_AUTH_PORT = "session_auth_port";

  private static final String DEFAULT_HTTPS_PORT = "443";

  @Override
  public String getPodBaseUrl() {
    String host = PropertiesReader.readRequiredProperty(POD_HOST,
        "POD host not provided in the system properties or environment variables.");
    String port = PropertiesReader.readProperty(POD_PORT, DEFAULT_HTTPS_PORT);
    return String.format("https://%s:%s/pod", host, port);
  }

  @Override
  public String getSessionAuthBaseUrl() {
    String host = PropertiesReader.readRequiredProperty(SESSION_AUTH_HOST,
        "Session auth host not provided in the system properties or environment variables.");
    String port = PropertiesReader.readProperty(SESSION_AUTH_PORT, DEFAULT_HTTPS_PORT);
    return String.format("https://%s:%s/sessionauth", host, port);
  }

}
