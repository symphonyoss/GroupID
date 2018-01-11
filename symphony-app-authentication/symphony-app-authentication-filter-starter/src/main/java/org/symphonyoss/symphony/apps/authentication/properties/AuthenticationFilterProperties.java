package org.symphonyoss.symphony.apps.authentication.properties;

import java.util.ArrayList;
import java.util.List;

/**
 * Properties for the web filter.
 *
 * Created by rsanchez on 10/01/18.
 */
public class AuthenticationFilterProperties {

  private boolean enabled;

  private List<String> urlPatterns = new ArrayList<>();

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

}
