package org.symphonyoss.symphony.apps.authentication.spring.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.symphonyoss.symphony.apps.authentication.spring.tokens.LocalStoreTokensProvider;
import org.symphonyoss.symphony.apps.authentication.tokens.StoreTokensProvider;

/**
 * Spring Configuration to create a new provider to store application tokens.
 *
 * Created by rsanchez on 08/03/18.
 */
@Configuration
@ConditionalOnProperty(name = "app-authentication.api.enabled", havingValue = "true")
public class StoreTokensProviderConfiguration {

  @Bean
  @ConditionalOnMissingBean(StoreTokensProvider.class)
  public StoreTokensProvider storeTokensProvider() {
    return new LocalStoreTokensProvider();
  }

}
