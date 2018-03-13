package org.symphonyoss.symphony.apps.authentication.spring.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.symphonyoss.symphony.apps.authentication.keystore.KeystoreProvider;
import org.symphonyoss.symphony.apps.authentication.spring.keystore.EnvKeystoreProvider;

/**
 * Spring Configuration to create application keystore provider.
 *
 * Created by rsanchez on 08/03/18.
 */
@Configuration
@ConditionalOnProperty(name = "app-authentication.api.enabled", havingValue = "true")
public class KeystoreProviderConfiguration {

  @Bean
  @ConditionalOnMissingBean(KeystoreProvider.class)
  public KeystoreProvider keystoreProvider() {
    return new EnvKeystoreProvider();
  }

}
