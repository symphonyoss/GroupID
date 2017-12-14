package org.symphonyoss.symphony.bots.helpdesk.bot.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.client.exceptions.InitException;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.authenticator.model.Token;

/**
 * Created by rsanchez on 14/12/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class HelpDeskSymphonyClientTest {

  private static final String DEFAULT_EMAIL = "mock@test.com";

  private static final String AGENT_URL = "https://test.symphony.com/agent";

  private static final String POD_URL = "https://test.symphony.com/pod";

  @Mock
  private SymAuth symAuth;

  private HelpDeskSymphonyClient client = new HelpDeskSymphonyClient();

  @Test
  public void testNullAgentUrl() {
    try {
      client.init(symAuth, DEFAULT_EMAIL, null, null);
      fail();
    } catch (InitException e) {
      assertEquals("Failed to provide agent URL", e.getMessage());
    }
  }

  @Test
  public void testNullPodUrl() {
    try {
      client.init(symAuth, DEFAULT_EMAIL, AGENT_URL, null);
      fail();
    } catch (InitException e) {
      assertEquals("Failed to provide service URL", e.getMessage());
    }
  }

  @Test
  public void testNullSymAuth() {
    try {
      client.init(null, DEFAULT_EMAIL, AGENT_URL, POD_URL);
      fail();
    } catch (InitException e) {
      assertEquals(
          "Symphony Authorization is not valid. Currently not logged into Agent, please check "
              + "certificates and tokens.",
          e.getMessage());
    }
  }

  @Test
  public void testNullSessionToken() {
    try {
      client.init(symAuth, DEFAULT_EMAIL, AGENT_URL, POD_URL);
      fail();
    } catch (InitException e) {
      assertEquals(
          "Symphony Authorization is not valid. Currently not logged into Agent, please check "
              + "certificates and tokens.",
          e.getMessage());
    }
  }

  @Test
  public void testNullKeyToken() {
    doReturn(new Token()).when(symAuth).getSessionToken();

    try {
      client.init(symAuth, DEFAULT_EMAIL, AGENT_URL, POD_URL);
      fail();
    } catch (InitException e) {
      assertEquals(
          "Symphony Authorization is not valid. Currently not logged into Agent, please check "
              + "certificates and tokens.",
          e.getMessage());
    }
  }

}
