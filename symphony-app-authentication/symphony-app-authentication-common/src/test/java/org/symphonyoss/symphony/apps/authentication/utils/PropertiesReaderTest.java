package org.symphonyoss.symphony.apps.authentication.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link PropertiesReader}
 *
 * Created by rsanchez on 10/01/18.
 */
public class PropertiesReaderTest {

  private static final String MOCK_PROP = "mock";

  private static final String MOCK_VALUE = "value";

  private static final String MOCK_DEFAULT_VALUE = "default";

  private static final String ERROR = "error";

  @Before
  public void init() {
    System.setProperty(MOCK_PROP, "");
  }

  @Test
  public void readEmptyProperty() {
    try {
      PropertiesReader.readRequiredProperty(MOCK_PROP, ERROR);
      fail();
    } catch (IllegalStateException e) {
      assertEquals(ERROR, e.getMessage());
    }
  }

  @Test
  public void readProperty() {
    System.setProperty(MOCK_PROP, MOCK_VALUE);
    String value = PropertiesReader.readRequiredProperty(MOCK_PROP, ERROR);

    assertEquals(MOCK_VALUE, value);
  }

  @Test
  public void readDefaultProperty() {
    String value = PropertiesReader.readProperty(MOCK_PROP, MOCK_DEFAULT_VALUE);
    assertEquals(MOCK_DEFAULT_VALUE, value);
  }

  @Test
  public void readNotRequiredProperty() {
    System.setProperty(MOCK_PROP, MOCK_VALUE);
    String value = PropertiesReader.readProperty(MOCK_PROP, MOCK_DEFAULT_VALUE);

    assertEquals(MOCK_VALUE, value);
  }
}
