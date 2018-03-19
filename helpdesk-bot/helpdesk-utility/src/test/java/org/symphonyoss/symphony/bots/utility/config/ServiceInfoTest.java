package org.symphonyoss.symphony.bots.utility.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 * Unit tests for {@link ServiceInfo}
 * Created by rsanchez on 14/02/18.
 */
public class ServiceInfoTest {

  private static final String MOCK_CONTEXT = "app";

  private static final String MOCK_HOST = "test.symphony.com";

  private static final Integer MOCK_PORT = 123456;

  @Test
  public void testEmptyHost() {
    ServiceInfo serviceInfo = new ServiceInfo();

    String url = serviceInfo.getUrl(MOCK_CONTEXT);
    assertEquals(StringUtils.EMPTY, url);
  }

  @Test
  public void testEmptyPort() {
    ServiceInfo serviceInfo = new ServiceInfo();
    serviceInfo.setHost(MOCK_HOST);

    String url = serviceInfo.getUrl(MOCK_CONTEXT);
    assertEquals("https://test.symphony.com/app", url);
    assertEquals(MOCK_HOST, serviceInfo.getHost());
    assertNull(serviceInfo.getPort());
  }

  @Test
  public void testUrl() {
    ServiceInfo serviceInfo = new ServiceInfo();
    serviceInfo.setHost(MOCK_HOST);
    serviceInfo.setPort(MOCK_PORT);

    String url = serviceInfo.getUrl(MOCK_CONTEXT);
    assertEquals("https://test.symphony.com:123456/app", url);
    assertEquals(MOCK_HOST, serviceInfo.getHost());
    assertEquals(MOCK_PORT, serviceInfo.getPort());
  }
}
