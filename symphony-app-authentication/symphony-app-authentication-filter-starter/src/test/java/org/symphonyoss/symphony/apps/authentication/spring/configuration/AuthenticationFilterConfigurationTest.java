package org.symphonyoss.symphony.apps.authentication.spring.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
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

import java.util.Collection;
import java.util.Collections;

/**
 * Unit tests for {@link AuthenticationFilterConfiguration}
 *
 * Created by rsanchez on 12/01/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthenticationFilterConfigurationTest {

  private static final String CACHE_TIMEOUT_PARAM = "cache_timeout";

  private static final String CACHE_SIZE_PARAM = "cache_size";

  private static final Integer MOCK_CACHE_TIMEOUT = 10;

  private static final Integer MOCK_CACHE_SIZE = 100;

  private static final String MOCK_URL_PATTERN = "/v1/*";

  @Mock
  private JsonParserFactory parserFactory;

  @Mock
  private PodCertificateClientFactory certificateClientFactory;

  @Mock
  private ServicesInfoProviderFactory providerFactory;

  @Mock
  private JsonParser jsonParser;

  @Mock
  private PodCertificateClient certificateClient;

  @Mock
  private ServicesInfoProvider servicesInfoProvider;

  @InjectMocks
  private AuthenticationFilterConfiguration configuration;

  @Before
  public void init() {
    System.setProperty(CACHE_SIZE_PARAM, "");
    System.setProperty(CACHE_TIMEOUT_PARAM, "");
  }

  @Test
  public void testNullCacheInfo() {
    FilterRegistrationBean registrationBean =
        configuration.authenticationFilter(jsonParser, certificateClient, servicesInfoProvider,
            new AuthenticationProperties());

    verify(parserFactory, times(1)).setComponent(jsonParser);
    verify(certificateClientFactory, times(1)).setComponent(certificateClient);
    verify(providerFactory, times(1)).setComponent(servicesInfoProvider);

    assertEquals("", System.getProperty(CACHE_SIZE_PARAM));
    assertEquals("", System.getProperty(CACHE_TIMEOUT_PARAM));

    assertTrue(registrationBean.getUrlPatterns().isEmpty());
  }

  @Test
  public void testWithoutCacheInfo() {
    AuthenticationProperties properties = new AuthenticationProperties();
    properties.setCache(new CacheProperties());

    FilterRegistrationBean registrationBean =
        configuration.authenticationFilter(jsonParser, certificateClient, servicesInfoProvider,
            properties);

    verify(parserFactory, times(1)).setComponent(jsonParser);
    verify(certificateClientFactory, times(1)).setComponent(certificateClient);
    verify(providerFactory, times(1)).setComponent(servicesInfoProvider);

    assertEquals("", System.getProperty(CACHE_SIZE_PARAM));
    assertEquals("", System.getProperty(CACHE_TIMEOUT_PARAM));

    assertTrue(registrationBean.getUrlPatterns().isEmpty());
  }

  @Test
  public void testCacheInfo() {
    CacheProperties cacheProperties = new CacheProperties();
    cacheProperties.setSize(MOCK_CACHE_SIZE);
    cacheProperties.setTimeout(MOCK_CACHE_TIMEOUT);

    AuthenticationProperties properties = new AuthenticationProperties();
    properties.setCache(cacheProperties);

    FilterRegistrationBean registrationBean =
        configuration.authenticationFilter(jsonParser, certificateClient, servicesInfoProvider,
            properties);

    verify(parserFactory, times(1)).setComponent(jsonParser);
    verify(certificateClientFactory, times(1)).setComponent(certificateClient);
    verify(providerFactory, times(1)).setComponent(servicesInfoProvider);

    assertEquals(MOCK_CACHE_SIZE.toString(), System.getProperty(CACHE_SIZE_PARAM));
    assertEquals(MOCK_CACHE_TIMEOUT.toString(), System.getProperty(CACHE_TIMEOUT_PARAM));

    assertTrue(registrationBean.getUrlPatterns().isEmpty());
  }

  @Test
  public void testEmptyUrlPatterns() {
    CacheProperties cacheProperties = new CacheProperties();
    cacheProperties.setSize(MOCK_CACHE_SIZE);
    cacheProperties.setTimeout(MOCK_CACHE_TIMEOUT);

    AuthenticationProperties properties = new AuthenticationProperties();
    properties.setCache(cacheProperties);

    properties.setFilter(new AuthenticationFilterProperties());

    FilterRegistrationBean registrationBean =
        configuration.authenticationFilter(jsonParser, certificateClient, servicesInfoProvider,
            properties);

    verify(parserFactory, times(1)).setComponent(jsonParser);
    verify(certificateClientFactory, times(1)).setComponent(certificateClient);
    verify(providerFactory, times(1)).setComponent(servicesInfoProvider);

    assertEquals(MOCK_CACHE_SIZE.toString(), System.getProperty(CACHE_SIZE_PARAM));
    assertEquals(MOCK_CACHE_TIMEOUT.toString(), System.getProperty(CACHE_TIMEOUT_PARAM));

    assertTrue(registrationBean.getUrlPatterns().isEmpty());
  }

  @Test
  public void testUrlPatterns() {
    CacheProperties cacheProperties = new CacheProperties();
    cacheProperties.setSize(MOCK_CACHE_SIZE);
    cacheProperties.setTimeout(MOCK_CACHE_TIMEOUT);

    AuthenticationProperties properties = new AuthenticationProperties();
    properties.setCache(cacheProperties);

    AuthenticationFilterProperties filterProperties = new AuthenticationFilterProperties();
    filterProperties.setUrlPatterns(Collections.singletonList(MOCK_URL_PATTERN));

    properties.setFilter(filterProperties);

    FilterRegistrationBean registrationBean =
        configuration.authenticationFilter(jsonParser, certificateClient, servicesInfoProvider,
            properties);

    verify(parserFactory, times(1)).setComponent(jsonParser);
    verify(certificateClientFactory, times(1)).setComponent(certificateClient);
    verify(providerFactory, times(1)).setComponent(servicesInfoProvider);

    assertEquals(MOCK_CACHE_SIZE.toString(), System.getProperty(CACHE_SIZE_PARAM));
    assertEquals(MOCK_CACHE_TIMEOUT.toString(), System.getProperty(CACHE_TIMEOUT_PARAM));

    Collection<String> urlPatterns = registrationBean.getUrlPatterns();

    assertEquals(1, urlPatterns.size());
    assertEquals(MOCK_URL_PATTERN, urlPatterns.iterator().next());
  }
}
