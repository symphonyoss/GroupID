package org.symphonyoss.symphony.bots.utility.config;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServiceInfoTest {

  private static final String SCHEME = "http";

  private static final String HOST = "helpdesk-test.symphony.com";

  private static final Integer PORT = 8080;

  private ServiceInfo serviceInfo;

  @Before
  public void setUp() throws Exception {
    serviceInfo = new ServiceInfo();
  }

  @Test
  public void setHost() {
    serviceInfo.setHost(HOST);
    assertEquals(HOST, serviceInfo.getHost());
  }

  @Test
  public void setScheme() {
    serviceInfo.setScheme(SCHEME);
    assertEquals(SCHEME, serviceInfo.getScheme());
  }

  @Test
  public void setPort() {
    serviceInfo.setPort(PORT);
    assertEquals(PORT, serviceInfo.getPort());
  }

  @Test
  public void getUrlWithNoHost() {
    String result = serviceInfo.getUrl("test");
    assertEquals(StringUtils.EMPTY, result);
  }

  @Test
  public void getUrl() {
    serviceInfo.setHost(HOST);
    serviceInfo.setPort(PORT);
    serviceInfo.setScheme(SCHEME);
    String response = serviceInfo.getUrl("test");
    assertEquals("http://helpdesk-test.symphony.com:8080/test", response);
  }

  @Test
  public void getUrlDefaultScheme() {
    serviceInfo.setHost(HOST);
    serviceInfo.setPort(PORT);
    String response = serviceInfo.getUrl("test");
    assertEquals("https://helpdesk-test.symphony.com:8080/test", response);
  }

  @Test
  public void getUrlWithoutPort() {
    serviceInfo.setHost(HOST);
    String response = serviceInfo.getUrl("test");
    assertEquals("https://helpdesk-test.symphony.com/test", response);
  }
}
