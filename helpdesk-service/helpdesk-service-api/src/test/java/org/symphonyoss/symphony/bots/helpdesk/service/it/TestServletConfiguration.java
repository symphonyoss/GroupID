package org.symphonyoss.symphony.bots.helpdesk.service.it;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.symphonyoss.symphony.apps.authentication.certificate.PodCertificateClient;
import org.symphonyoss.symphony.apps.authentication.certificate.PodCertificateClientFactory;
import org.symphonyoss.symphony.apps.authentication.certificate.PodCertificateJerseyClient;
import org.symphonyoss.symphony.apps.authentication.endpoints.ServicesInfoProvider;
import org.symphonyoss.symphony.apps.authentication.endpoints.ServicesInfoProviderFactory;
import org.symphonyoss.symphony.apps.authentication.json.JacksonParser;
import org.symphonyoss.symphony.apps.authentication.json.JsonParser;
import org.symphonyoss.symphony.apps.authentication.json.JsonParserFactory;
import org.symphonyoss.symphony.apps.authentication.keystore.KeystoreProvider;
import org.symphonyoss.symphony.apps.authentication.keystore.KeystoreProviderFactory;
import org.symphonyoss.symphony.apps.authentication.servlets.AppAuthenticationServlet;
import org.symphonyoss.symphony.apps.authentication.servlets.JwtValidationServlet;
import org.symphonyoss.symphony.apps.authentication.servlets.TokensValidationServlet;
import org.symphonyoss.symphony.apps.authentication.spring.keystore.EnvKeystoreProvider;
import org.symphonyoss.symphony.apps.authentication.spring.properties.AuthenticationProperties;
import org.symphonyoss.symphony.apps.authentication.spring.properties
    .AuthenticationServletProperties;
import org.symphonyoss.symphony.apps.authentication.spring.tokens.LocalStoreTokensProvider;
import org.symphonyoss.symphony.apps.authentication.tokens.StoreTokensProvider;
import org.symphonyoss.symphony.apps.authentication.tokens.StoreTokensProviderFactory;
import org.symphonyoss.symphony.apps.authentication.utils.PropertiesReader;

import java.util.Arrays;

/**
 * Test Spring Configuration to enable authentication servlets
 *
 * Created by rsanchez on 10/01/18.
 */
@Configuration
@EnableConfigurationProperties({AuthenticationServletProperties.class, AuthenticationProperties.class})
public class TestServletConfiguration {

  private static final String AUTHENTICATE_PATH = "/authenticate";

  private static final String TOKENS_PATH = "/tokens/validate";

  private static final String JWT_PATH = "/jwt/validate";

  private static final String KEYSTORE_FILE = "root.p12";

  @Bean
  public JsonParser jsonParser() {
    JsonParser parser = new JacksonParser();

    JsonParserFactory.getInstance().setComponent(parser);

    return parser;
  }

  @Bean
  public StoreTokensProvider storeTokensProvider() {
    LocalStoreTokensProvider provider = new LocalStoreTokensProvider();

    StoreTokensProviderFactory.getInstance().setComponent(provider);

    return provider;
  }

  @Bean
  public PodCertificateClient podCertificateClient() {
    PodCertificateClient client = new PodCertificateJerseyClient(1000, 1000);

    PodCertificateClientFactory.getInstance().setComponent(client);

    return client;
  }

  @Bean
  public KeystoreProvider keystoreProvider() {
    KeystoreProvider keystoreProvider = new EnvKeystoreProvider();

    KeystoreProviderFactory.getInstance().setComponent(keystoreProvider);

    return keystoreProvider;
  }

  @Bean
  public ServicesInfoProvider servicesInfoProvider() {
    ServicesInfoProvider servicesInfoProvider = new ServicesInfoProvider() {

      private static final String LOCAL_PORT = "local_port";

      @Override
      public String getPodBaseUrl() {
        String port = PropertiesReader.readRequiredProperty(LOCAL_PORT, "Missing local port");
        return String.format("http://localhost:%s/pod", port);
      }

      @Override
      public String getSessionAuthBaseUrl() {
        String port = PropertiesReader.readRequiredProperty(LOCAL_PORT, "Missing local port");
        return String.format("http://localhost:%s/sessionauth", port);
      }

    };

    ServicesInfoProviderFactory.getInstance().setComponent(servicesInfoProvider);

    return servicesInfoProvider;
  }

  @Bean
  @ConditionalOnBean({ JsonParser.class, StoreTokensProvider.class, PodCertificateClient.class,
      KeystoreProvider.class, ServicesInfoProvider.class })
  public ServletRegistrationBean authenticateServlet(AuthenticationServletProperties servletProperties) {

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
  @ConditionalOnBean({ JsonParser.class, StoreTokensProvider.class, PodCertificateClient.class,
      KeystoreProvider.class, ServicesInfoProvider.class })
  public ServletRegistrationBean tokenValidationServlet(AuthenticationServletProperties servletProperties) {
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
  @ConditionalOnBean({ JsonParser.class, StoreTokensProvider.class, PodCertificateClient.class,
      KeystoreProvider.class, ServicesInfoProvider.class})
  public ServletRegistrationBean jwtValidationServlet(AuthenticationServletProperties servletProperties) {
    JwtValidationServlet servlet = new JwtValidationServlet();

    ServletRegistrationBean registration = new ServletRegistrationBean(servlet);

    if (servletProperties == null) {
      return registration;
    }

    String basePath = servletProperties.getBasePath();

    registration.setUrlMappings(Arrays.asList(basePath + JWT_PATH));

    return registration;
  }

  @Bean
  public MockPodCertificateService certificateService() {
    return new MockPodCertificateService();
  }
}
