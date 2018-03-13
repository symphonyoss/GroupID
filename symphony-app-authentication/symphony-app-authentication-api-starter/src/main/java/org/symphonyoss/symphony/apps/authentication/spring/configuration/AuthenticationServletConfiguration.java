package org.symphonyoss.symphony.apps.authentication.spring.configuration;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.symphonyoss.symphony.apps.authentication.certificate.PodCertificateClient;
import org.symphonyoss.symphony.apps.authentication.certificate.PodCertificateClientFactory;
import org.symphonyoss.symphony.apps.authentication.endpoints.ServicesInfoProvider;
import org.symphonyoss.symphony.apps.authentication.endpoints.ServicesInfoProviderFactory;
import org.symphonyoss.symphony.apps.authentication.json.JsonParser;
import org.symphonyoss.symphony.apps.authentication.json.JsonParserFactory;
import org.symphonyoss.symphony.apps.authentication.keystore.KeystoreProvider;
import org.symphonyoss.symphony.apps.authentication.keystore.KeystoreProviderFactory;
import org.symphonyoss.symphony.apps.authentication.servlets.AppAuthenticationServlet;
import org.symphonyoss.symphony.apps.authentication.servlets.JwtValidationServlet;
import org.symphonyoss.symphony.apps.authentication.servlets.TokensValidationServlet;
import org.symphonyoss.symphony.apps.authentication.spring.properties.AuthenticationProperties;
import org.symphonyoss.symphony.apps.authentication.spring.properties.AuthenticationServletProperties;
import org.symphonyoss.symphony.apps.authentication.tokens.StoreTokensProvider;
import org.symphonyoss.symphony.apps.authentication.tokens.StoreTokensProviderFactory;

import java.util.Arrays;

/**
 * Spring Configuration to create authentication servlets
 *
 * Created by rsanchez on 10/01/18.
 */
@Configuration
@ConditionalOnProperty(name = "app-authentication.enabled", havingValue = "true")
@EnableConfigurationProperties({AuthenticationServletProperties.class})
@AutoConfigureAfter({ JsonParserConfiguration.class, PodCertificateClientConfiguration.class,
    ServicesInfoProviderConfiguration.class, KeystoreProviderConfiguration.class,
    StoreTokensProviderConfiguration.class })
public class AuthenticationServletConfiguration {

  private static final String AUTHENTICATE_PATH = "/authenticate";

  private static final String TOKENS_PATH = "/tokens/validate";

  private static final String JWT_PATH = "/jwt/validate";

  private JsonParserFactory parserFactory = JsonParserFactory.getInstance();

  private KeystoreProviderFactory keystoreProviderFactory = KeystoreProviderFactory.getInstance();

  private ServicesInfoProviderFactory providerFactory = ServicesInfoProviderFactory.getInstance();

  private PodCertificateClientFactory certificateClientFactory = PodCertificateClientFactory.getInstance();

  private StoreTokensProviderFactory storeTokensProviderFactory = StoreTokensProviderFactory.getInstance();

  @Bean
  @ConditionalOnBean({JsonParser.class, KeystoreProvider.class, ServicesInfoProvider.class,
      StoreTokensProvider.class, AuthenticationServletProperties.class})
  @ConditionalOnProperty(name = "app-authentication.api.enabled", havingValue = "true")
  public ServletRegistrationBean authenticateServlet(JsonParser jsonParser,
      KeystoreProvider keystoreProvider, ServicesInfoProvider provider,
      StoreTokensProvider storeTokensProvider, AuthenticationServletProperties servletProperties) {
    parserFactory.setComponent(jsonParser);
    keystoreProviderFactory.setComponent(keystoreProvider);
    providerFactory.setComponent(provider);
    storeTokensProviderFactory.setComponent(storeTokensProvider);

    AppAuthenticationServlet servlet = new AppAuthenticationServlet();

    ServletRegistrationBean registration = new ServletRegistrationBean(servlet);

    if (servletProperties == null) {
      return registration;
    }

    String basePath = servletProperties.getBasePath();

    registration.setUrlMappings(Arrays.asList(basePath + AUTHENTICATE_PATH));

    return registration;
  }

  @Bean
  @ConditionalOnBean({JsonParser.class, StoreTokensProvider.class, AuthenticationServletProperties.class})
  @ConditionalOnProperty(name = "app-authentication.api.enabled", havingValue = "true")
  public ServletRegistrationBean tokenValidationServlet(JsonParser jsonParser,
      StoreTokensProvider storeTokensProvider, AuthenticationServletProperties servletProperties) {
    parserFactory.setComponent(jsonParser);
    storeTokensProviderFactory.setComponent(storeTokensProvider);

    TokensValidationServlet servlet = new TokensValidationServlet();

    ServletRegistrationBean registration = new ServletRegistrationBean(servlet);

    if (servletProperties == null) {
      return registration;
    }

    String basePath = servletProperties.getBasePath();

    registration.setUrlMappings(Arrays.asList(basePath + TOKENS_PATH));

    return registration;
  }

  @Bean
  @ConditionalOnBean({JsonParser.class, PodCertificateClient.class, ServicesInfoProvider.class,
      AuthenticationProperties.class, AuthenticationServletProperties.class})
  @ConditionalOnProperty(name = "app-authentication.api.enabled", havingValue = "true")
  public ServletRegistrationBean jwtValidationServlet(JsonParser jsonParser,
      PodCertificateClient client, ServicesInfoProvider provider,
      AuthenticationServletProperties servletProperties) {
    parserFactory.setComponent(jsonParser);
    certificateClientFactory.setComponent(client);
    providerFactory.setComponent(provider);

    JwtValidationServlet servlet = new JwtValidationServlet();

    ServletRegistrationBean registration = new ServletRegistrationBean(servlet);

    if (servletProperties == null) {
      return registration;
    }

    String basePath = servletProperties.getBasePath();

    registration.setUrlMappings(Arrays.asList(basePath + JWT_PATH));

    return registration;
  }
}
