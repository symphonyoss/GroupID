package org.symphonyoss.symphony.apps.authentication.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Unit tests for {@link JsonParserFactory}
 *
 * Created by robson on 10/01/18.
 */
public class JsonParserFactoryTest {

  private JsonParser parser = new JacksonParser();

  private JsonParserFactory factory = JsonParserFactory.getInstance();

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
    try {
      factory.getComponent();
      fail();
    } catch (IllegalStateException e) {
      assertEquals("There is no implementation defined for this component", e.getMessage());
    }

    factory.setComponent(parser);

    assertEquals(parser, factory.getComponent());
  }

}
