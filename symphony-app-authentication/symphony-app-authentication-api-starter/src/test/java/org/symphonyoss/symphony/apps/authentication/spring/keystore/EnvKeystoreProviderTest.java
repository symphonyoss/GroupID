package org.symphonyoss.symphony.apps.authentication.spring.keystore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.symphonyoss.symphony.apps.authentication.keystore.model.KeystoreSettings;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Unit tests for {@link EnvKeystoreProvider}
 *
 * Created by rsanchez on 09/03/18.
 */
public class EnvKeystoreProviderTest {

  private static final String DEFAULT_KEYSTORE_FILE = "test.p12";

  private static final String DEFAULT_KEYSTORE_PASS = "changeit";

  private static final String KEYSTORE = "app_keystore";

  private static final String KEYSTORE_PASS = KEYSTORE + "_password";

  private static final String APP_ID = "APP";

  private EnvKeystoreProvider provider = new EnvKeystoreProvider();

  @Before
  public void init() throws URISyntaxException {
    URL resource = getClass().getClassLoader().getResource(DEFAULT_KEYSTORE_FILE);
    File keystoreFile =new File(resource.toURI());

    System.setProperty(KEYSTORE, keystoreFile.getAbsolutePath());
    System.setProperty(KEYSTORE_PASS, DEFAULT_KEYSTORE_PASS);
  }

  @Test
  public void testWithoutKeystoreProp() {
    System.setProperty(KEYSTORE, "");

    try {
      provider.getApplicationKeystore(APP_ID);
      fail();
    } catch (IllegalStateException e) {
      assertEquals("App keystore not provided in the system properties or environment variables.",
          e.getMessage());
    }
  }

  @Test
  public void testWithoutKeystorePassProp() {
    System.setProperty(KEYSTORE_PASS, "");

    try {
      provider.getApplicationKeystore(APP_ID);
      fail();
    } catch (IllegalStateException e) {
      assertEquals(
          "App keystore password not provided in the system properties or environment variables.",
          e.getMessage());
    }
  }

  @Test
  public void testInvalidKeystore() {
    System.setProperty(KEYSTORE, "invalid.p12");

    try {
      provider.getApplicationKeystore(APP_ID);
      fail();
    } catch (LoadKeyStoreException e) {
      assertEquals("Fail to load keystore file at invalid.p12", e.getMessage());
    }
  }

  @Test
  public void testSuccess() {
    KeystoreSettings appKeystore = provider.getApplicationKeystore(APP_ID);
    assertNotNull(appKeystore.getData());
    assertEquals(DEFAULT_KEYSTORE_PASS, appKeystore.getPassword());
  }

}
