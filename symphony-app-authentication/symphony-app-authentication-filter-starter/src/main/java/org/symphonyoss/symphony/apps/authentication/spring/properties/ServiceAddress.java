package org.symphonyoss.symphony.apps.authentication.spring.properties;

/**
 * Service url (host and port)
 *
 * Created by rsanchez on 20/11/17.
 */
public class ServiceAddress {

  private String host;

  private Integer port;

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public String getUrl(String context) {
    if ((host == null) || (host.isEmpty())) {
      throw new IllegalArgumentException(context + ": Host not provided in the properties");
    }

    if (port != null) {
      return String.format("https://%s:%d/%s", host, port, context);
    } else {
      return String.format("https://%s/%s", host, context);
    }
  }

}
