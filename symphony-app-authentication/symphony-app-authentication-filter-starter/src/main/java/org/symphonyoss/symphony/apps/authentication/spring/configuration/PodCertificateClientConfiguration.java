package org.symphonyoss.symphony.apps.authentication.spring.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.symphonyoss.symphony.apps.authentication.certificate.PodCertificateClient;
import org.symphonyoss.symphony.apps.authentication.certificate.PodCertificateJerseyClient;
import org.symphonyoss.symphony.apps.authentication.spring.properties.AuthenticationProperties;
import org.symphonyoss.symphony.apps.authentication.spring.properties.HttpClientProperties;

/**
 * Spring Configuration to create pod certificate client
 *
 * Created by rsanchez on 10/01/18.
 */
@Configuration
@ConditionalOnProperty(name = "app-authentication.enabled", havingValue = "true")
@EnableConfigurationProperties(AuthenticationProperties.class)
public class PodCertificateClientConfiguration {

  @Bean
  @ConditionalOnMissingBean(PodCertificateClient.class)
  public PodCertificateClient podCertificateClient(AuthenticationProperties properties) {
    HttpClientProperties httpClientProperties = properties.getHttpClient();

    if (httpClientProperties == null) {
      httpClientProperties = new HttpClientProperties();
    }

    Integer connectTimeout = httpClientProperties.getConnectTimeout();
    Integer readTimeout = httpClientProperties.getReadTimeout();

    return new PodCertificateJerseyClient(connectTimeout, readTimeout);
  }

}
