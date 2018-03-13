package org.symphonyoss.symphony.apps.authentication.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Properties for the web filter.
 *
 * Created by rsanchez on 10/01/18.
 */
@Configuration
@ConfigurationProperties(prefix = "app-authentication.filter")
public class AuthenticationFilterProperties {

  private boolean enabled;

  private List<String> urlPatterns = new ArrayList<>();

  private List<String> excludedPaths = new ArrayList<>();

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public List<String> getUrlPatterns() {
    return urlPatterns;
  }

  public void setUrlPatterns(List<String> urlPatterns) {
    this.urlPatterns = urlPatterns;
  }

  public List<String> getExcludedPaths() {
    return excludedPaths;
  }

  public void setExcludedPaths(List<String> excludedPaths) {
    this.excludedPaths = excludedPaths;
  }
}
