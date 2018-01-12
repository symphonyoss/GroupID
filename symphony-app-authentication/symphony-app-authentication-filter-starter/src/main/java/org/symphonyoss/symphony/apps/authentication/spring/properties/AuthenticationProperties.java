package org.symphonyoss.symphony.apps.authentication.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Properties for the web filter.
 *
 * Created by rsanchez on 10/01/18.
 */
@Configuration
@ConfigurationProperties(prefix = "app-authentication")
public class AuthenticationProperties {

  private HttpClientProperties httpClient;

  private CacheProperties cache;

  private ServiceAddress pod;

  private ServiceAddress sessionAuth;

  private AuthenticationFilterProperties filter;

  public HttpClientProperties getHttpClient() {
    return httpClient;
  }

  public void setHttpClient(
      HttpClientProperties httpClient) {
    this.httpClient = httpClient;
  }

  public CacheProperties getCache() {
    return cache;
  }

  public void setCache(CacheProperties cache) {
    this.cache = cache;
  }

  public ServiceAddress getPod() {
    return pod;
  }

  public void setPod(ServiceAddress pod) {
    this.pod = pod;
  }

  public ServiceAddress getSessionAuth() {
    return sessionAuth;
  }

  public void setSessionAuth(
      ServiceAddress sessionAuth) {
    this.sessionAuth = sessionAuth;
  }

  public AuthenticationFilterProperties getFilter() {
    return filter;
  }

  public void setFilter(
      AuthenticationFilterProperties filter) {
    this.filter = filter;
  }
}
