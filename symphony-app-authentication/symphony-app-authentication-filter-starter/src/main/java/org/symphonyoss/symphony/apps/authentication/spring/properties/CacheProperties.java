package org.symphonyoss.symphony.apps.authentication.spring.properties;

/**
 * Cache properties.
 *
 * Created by rsanchez on 10/01/18.
 */
public class CacheProperties {

  private Integer timeout;

  private Integer size;

  public Integer getTimeout() {
    return timeout;
  }

  public void setTimeout(Integer timeout) {
    this.timeout = timeout;
  }

  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }
}
