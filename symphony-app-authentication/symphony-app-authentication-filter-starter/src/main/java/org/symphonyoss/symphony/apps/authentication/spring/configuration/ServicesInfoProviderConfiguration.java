package org.symphonyoss.symphony.apps.authentication.spring.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.symphonyoss.symphony.apps.authentication.spring.SpringServiceInfoProvider;
import org.symphonyoss.symphony.apps.authentication.endpoints.ServicesInfoProvider;
import org.symphonyoss.symphony.apps.authentication.spring.properties.AuthenticationProperties;

/**
 * Spring Configuration to create authentication filter
 *
 * Created by rsanchez on 10/01/18.
 */
@Configuration
@ConditionalOnProperty(name = "app-authentication.enabled", havingValue = "true")
@EnableConfigurationProperties(AuthenticationProperties.class)
public class ServicesInfoProviderConfiguration {

  @Bean
  @ConditionalOnMissingBean(ServicesInfoProvider.class)
  public ServicesInfoProvider servicesInfoProvider(AuthenticationProperties properties) {
    return new SpringServiceInfoProvider(properties);
  }

}
