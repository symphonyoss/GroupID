package org.symphonyoss.symphony.apps.authentication.spring.configuration;

import static org.symphonyoss.symphony.apps.authentication.AuthenticationFilter.EXCLUDED_PATHS;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.symphonyoss.symphony.apps.authentication.AuthenticationFilter;
import org.symphonyoss.symphony.apps.authentication.certificate.PodCertificateClient;
import org.symphonyoss.symphony.apps.authentication.certificate.PodCertificateClientFactory;
import org.symphonyoss.symphony.apps.authentication.endpoints.ServicesInfoProvider;
import org.symphonyoss.symphony.apps.authentication.endpoints.ServicesInfoProviderFactory;
import org.symphonyoss.symphony.apps.authentication.json.JsonParser;
import org.symphonyoss.symphony.apps.authentication.json.JsonParserFactory;
import org.symphonyoss.symphony.apps.authentication.spring.properties
    .AuthenticationFilterProperties;
import org.symphonyoss.symphony.apps.authentication.spring.properties.AuthenticationProperties;
import org.symphonyoss.symphony.apps.authentication.spring.properties.CacheProperties;

import java.util.List;

/**
 * Spring Configuration to create authentication filter
 *
 * Created by rsanchez on 10/01/18.
 */
@Configuration
@ConditionalOnProperty(name = "app-authentication.enabled", havingValue = "true")
@EnableConfigurationProperties({AuthenticationProperties.class, AuthenticationFilterProperties.class})
@AutoConfigureAfter({ JsonParserConfiguration.class, PodCertificateClientConfiguration.class,
    ServicesInfoProviderConfiguration.class })
public class AuthenticationFilterConfiguration {

  private static final String CACHE_TIMEOUT_PARAM = "cache_timeout";

  private static final String CACHE_SIZE_PARAM = "cache_size";

  private JsonParserFactory parserFactory = JsonParserFactory.getInstance();

  private PodCertificateClientFactory certificateClientFactory = PodCertificateClientFactory.getInstance();

  private ServicesInfoProviderFactory providerFactory = ServicesInfoProviderFactory.getInstance();

  @Bean
  @ConditionalOnBean({JsonParser.class, PodCertificateClient.class, ServicesInfoProvider.class,
      AuthenticationProperties.class, AuthenticationFilterProperties.class})
  @ConditionalOnProperty(name = "app-authentication.filter.enabled", havingValue = "true", matchIfMissing = true)
  public FilterRegistrationBean authenticationFilter(JsonParser jsonParser,
      PodCertificateClient client, ServicesInfoProvider provider,
      AuthenticationProperties properties, AuthenticationFilterProperties filterProperties) {
    setupCacheProperties(properties);

    parserFactory.setComponent(jsonParser);
    certificateClientFactory.setComponent(client);
    providerFactory.setComponent(provider);

    AuthenticationFilter filter = new AuthenticationFilter();
    FilterRegistrationBean registration = new FilterRegistrationBean(filter);

    if (filterProperties == null) {
      return registration;
    }

    List<String> urlPatterns = filterProperties.getUrlPatterns();
    if (!urlPatterns.isEmpty()) {
      registration.setUrlPatterns(urlPatterns);
    }

    List<String> paths = filterProperties.getExcludedPaths();
    if (!paths.isEmpty()) {
      StringBuilder excludedPaths = new StringBuilder(paths.get(0));

      for (int i = 1; i < paths.size(); i++) {
        excludedPaths.append(",");
        excludedPaths.append(paths.get(i));
      }

      registration.addInitParameter(EXCLUDED_PATHS, excludedPaths.toString());
    }

    return registration;
  }

  /**
   * Setup cache properties
   * @param properties YAML properties
   */
  private void setupCacheProperties(AuthenticationProperties properties) {
    if ((properties == null) || (properties.getCache() == null)) {
      return;
    }

    CacheProperties cacheProperties = properties.getCache();

    Integer cacheSize = cacheProperties.getSize();
    Integer cacheTimeout = cacheProperties.getTimeout();

    if (cacheSize != null) {
      System.setProperty(CACHE_SIZE_PARAM, cacheSize.toString());
    }

    if (cacheTimeout != null) {
      System.setProperty(CACHE_TIMEOUT_PARAM, cacheTimeout.toString());
    }
  }

}
