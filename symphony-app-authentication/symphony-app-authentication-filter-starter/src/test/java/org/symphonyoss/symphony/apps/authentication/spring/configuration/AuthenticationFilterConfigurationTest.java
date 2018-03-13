package org.symphonyoss.symphony.apps.authentication.spring.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.symphonyoss.symphony.apps.authentication.AuthenticationFilter.EXCLUDED_PATHS;

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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

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

  private static final String MOCK_PATH1 = "/servlet/path1";

  private static final String MOCK_PATH2 = "/servlet/path2";

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
            new AuthenticationProperties(), null);

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
            properties, null);

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
            properties, null);

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

    FilterRegistrationBean registrationBean =
        configuration.authenticationFilter(jsonParser, certificateClient, servicesInfoProvider,
            properties, new AuthenticationFilterProperties());

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

    FilterRegistrationBean registrationBean =
        configuration.authenticationFilter(jsonParser, certificateClient, servicesInfoProvider,
            properties, filterProperties);

    verify(parserFactory, times(1)).setComponent(jsonParser);
    verify(certificateClientFactory, times(1)).setComponent(certificateClient);
    verify(providerFactory, times(1)).setComponent(servicesInfoProvider);

    assertEquals(MOCK_CACHE_SIZE.toString(), System.getProperty(CACHE_SIZE_PARAM));
    assertEquals(MOCK_CACHE_TIMEOUT.toString(), System.getProperty(CACHE_TIMEOUT_PARAM));

    Collection<String> urlPatterns = registrationBean.getUrlPatterns();

    assertEquals(1, urlPatterns.size());
    assertEquals(MOCK_URL_PATTERN, urlPatterns.iterator().next());
  }

  @Test
  public void testExcludedPaths() {
    CacheProperties cacheProperties = new CacheProperties();
    cacheProperties.setSize(MOCK_CACHE_SIZE);
    cacheProperties.setTimeout(MOCK_CACHE_TIMEOUT);

    AuthenticationProperties properties = new AuthenticationProperties();
    properties.setCache(cacheProperties);

    AuthenticationFilterProperties filterProperties = new AuthenticationFilterProperties();
    filterProperties.setUrlPatterns(Collections.singletonList(MOCK_URL_PATTERN));
    filterProperties.setExcludedPaths(Arrays.asList(MOCK_PATH1, MOCK_PATH2));

    FilterRegistrationBean registrationBean =
        configuration.authenticationFilter(jsonParser, certificateClient, servicesInfoProvider,
            properties, filterProperties);

    Collection<String> urlPatterns = registrationBean.getUrlPatterns();
    Map<String, String> initParameters = registrationBean.getInitParameters();

    assertEquals(1, urlPatterns.size());
    assertEquals(MOCK_URL_PATTERN, urlPatterns.iterator().next());
    assertTrue(initParameters.containsKey(EXCLUDED_PATHS));
    assertEquals(MOCK_PATH1 + "," + MOCK_PATH2, initParameters.get(EXCLUDED_PATHS));
  }

}
