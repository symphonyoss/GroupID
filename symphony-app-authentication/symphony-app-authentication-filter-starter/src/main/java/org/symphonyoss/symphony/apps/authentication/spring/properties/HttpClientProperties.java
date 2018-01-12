package org.symphonyoss.symphony.apps.authentication.spring.properties;

/**
 * HTTP client properties.
 *
 * Created by rsanchez on 10/01/18.
 */
public class HttpClientProperties {

  private static final Integer DEFAULT_TIMEOUT = 5000;

  private Integer connectTimeout = DEFAULT_TIMEOUT;

  private Integer readTimeout = DEFAULT_TIMEOUT;

  public Integer getConnectTimeout() {
    return connectTimeout;
  }

  public void setConnectTimeout(Integer connectTimeout) {
    this.connectTimeout = connectTimeout;
  }

  public Integer getReadTimeout() {
    return readTimeout;
  }

  public void setReadTimeout(Integer readTimeout) {
    this.readTimeout = readTimeout;
  }
}
