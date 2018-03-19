package org.symphonyoss.symphony.bots.helpdesk.service.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.symphonyoss.symphony.apps.authentication.keystore.model.KeystoreSettings;
import org.symphonyoss.symphony.apps.authentication.spring.keystore.LoadKeyStoreException;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

/**
 * Unit tests for {@link HelpDeskKeystoreProvider}
 *
 * Created by rsanchez on 13/03/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class HelpDeskKeystoreProviderTest {

  private static final String APP_ID = "ID";

  private static final String DEFAULT_KEYSTORE_FILE = "root.p12";

  private static final String INVALID_KEYSTORE_FILE = "invalid.p12";

  private static final String DEFAULT_KEYSTORE_PASS = "changeit";

  private static final String KEYSTORE = "keystoreFile";

  private static final String KEYSTORE_PASS = "keystorePassword";

  @InjectMocks
  private HelpDeskKeystoreProvider provider;

  @Before
  public void init() throws URISyntaxException {
    URL resource = getClass().getClassLoader().getResource(DEFAULT_KEYSTORE_FILE);
    File keystoreFile = new File(resource.toURI());

    ReflectionTestUtils.setField(provider, KEYSTORE, Optional.of(keystoreFile.getAbsolutePath()));
    ReflectionTestUtils.setField(provider, KEYSTORE_PASS, Optional.of(DEFAULT_KEYSTORE_PASS));
  }

  @Test
  public void testEmptyKeystorePath() {
    ReflectionTestUtils.setField(provider, KEYSTORE, Optional.empty());

    try {
      provider.getApplicationKeystore(APP_ID);
      fail();
    } catch (IllegalStateException e) {
      assertEquals("App keystore not provided in the YAML config", e.getMessage());
    }
  }

  @Test
  public void testEmptyKeystorePassword() {
    ReflectionTestUtils.setField(provider, KEYSTORE_PASS, Optional.empty());

    try {
      provider.getApplicationKeystore(APP_ID);
      fail();
    } catch (IllegalStateException e) {
      assertEquals("App keystore password not provided in the YAML config", e.getMessage());
    }
  }

  @Test
  public void testEmptyWrongPath() {
    ReflectionTestUtils.setField(provider, KEYSTORE, Optional.of(INVALID_KEYSTORE_FILE));

    try {
      provider.getApplicationKeystore(APP_ID);
      fail();
    } catch (LoadKeyStoreException e) {
      assertEquals("Fail to load keystore file at invalid.p12", e.getMessage());
    }
  }

  @Test
  public void testKeystore() {
    KeystoreSettings appKeystore = provider.getApplicationKeystore(APP_ID);

    assertNotNull(appKeystore.getData());
    assertEquals(DEFAULT_KEYSTORE_PASS, appKeystore.getPassword());
  }
}
