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
@ConfigurationProperties(prefix = "app-authentication.api")
public class AuthenticationServletProperties {

  private boolean enabled;

  private String basePath;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getBasePath() {
    return basePath;
  }

  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }
}
