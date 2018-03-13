package org.symphonyoss.symphony.apps.authentication.tokens;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Unit tests for {@link StoreTokensProviderFactory}
 *
 * Created by robson on 06/03/18.
 */
public class StoreTokensProviderFactoryTest {

  private StoreTokensProvider provider = new MockStoreTokensProvider();

  private StoreTokensProviderFactory factory = StoreTokensProviderFactory.getInstance();

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
