package org.symphonyoss.symphony.apps.authentication.keystore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.symphonyoss.symphony.apps.authentication.keystore.model.KeystoreSettings;

/**
 * Unit tests for {@link KeystoreProviderFactory}
 *
 * Created by robson on 06/03/18.
 */
public class KeystoreProviderFactoryTest {

  private KeystoreProvider provider = new MockKeystoreProvider(new KeystoreSettings());

  private KeystoreProviderFactory factory = KeystoreProviderFactory.getInstance();

  @Test
  public void testNullComponent() {
    try {
      factory.setComponent(null);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid component implementation. It mustn't be null", e.getMessage());
    }
  }

  @Test
  public void testFactory() {
    factory.setComponent(provider);
    assertEquals(provider, factory.getComponent());
  }

}
