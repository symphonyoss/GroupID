package org.symphonyoss.symphony.bots.utility.config;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServiceInfoTest {

  private final String host = "helpdesk-test";

  private final Integer port = new Integer("8080");

  private ServiceInfo serviceInfo;

  @Before
  public void setUp() throws Exception {
    serviceInfo = new ServiceInfo();
  }

  @Test
  public void getHost() {
    serviceInfo.getHost();
    assertEquals(null,serviceInfo.getHost());
  }

  @Test
  public void setHost() {
    serviceInfo.setHost(host);
    assertEquals(host,serviceInfo.getHost());
  }

  @Test
  public void getPort() {
    serviceInfo.getPort();
    assertEquals(null, serviceInfo.getPort());
  }

  @Test
  public void setPort() {
    serviceInfo.setPort(port);
    assertEquals(port,serviceInfo.getPort());
  }

  @Test
  public void getUrlWithNoHost() {
    String result = serviceInfo.getUrl("test");
    assertEquals(StringUtils.EMPTY,result);
  }

  @Test
  public void getUrl() {
    serviceInfo.setHost(host);
    serviceInfo.setPort(port);
    String response = serviceInfo.getUrl("test");
    assertEquals("https://helpdesk-test:8080/test", response);
  }

  @Test
  public void getUrlWithoutPort() {
    serviceInfo.setHost(host);
    String response = serviceInfo.getUrl("test");
    assertEquals("https://helpdesk-test/test", response);
  }

}