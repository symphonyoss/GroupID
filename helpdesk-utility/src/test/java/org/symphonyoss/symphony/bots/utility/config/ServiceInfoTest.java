package org.symphonyoss.symphony.bots.utility.config;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServiceInfoTest {

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
    assertEquals(HOST,serviceInfo.getHost());
  }

  @Test
  public void setPort() {
    serviceInfo.setPort(PORT);
    assertEquals(PORT,serviceInfo.getPort());
  }

  @Test
  public void getUrlWithNoHost() {
    String result = serviceInfo.getUrl("test");
    assertEquals(StringUtils.EMPTY,result);
  }

  @Test
  public void getUrl() {
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