package org.symphonyoss.symphony.bots.utility.config;

import org.apache.commons.lang3.StringUtils;

/**
 * Model class to describe service host and port.
 * <p>
 * Created by robson on 11/20/17.
 */
public class ServiceInfo {

  private String scheme;

  private String host;

  private Integer port;

  public String getScheme() {
    return scheme;
  }

  public void setScheme(String scheme) {
    this.scheme = scheme;
  }

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

  /**
   * Returns the service URL.
   *
   * @param context Service context
   * @return Service URL
   */
  public String getUrl(String context) {
    if (StringUtils.isEmpty(host)) {
      return StringUtils.EMPTY;
    }
    String usedScheme = StringUtils.isEmpty(scheme) ? "https" : scheme;

    if (port != null) {
      return String.format("%s://%s:%d/%s", usedScheme, host, port, context);
    } else {
      return String.format("%s://%s/%s", usedScheme, host, context);
    }
  }
}
