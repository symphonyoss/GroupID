package org.symphonyoss.symphony.apps.authentication.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Unit tests for {@link JacksonParser}
 *
 * Created by rsanchez on 10/01/18.
 */
public class JacksonParserTest {

  private static final String KEY = "mock";

  private static final String VALUE = "value";

  private static final String EXPECTED_JSON = "{\"mock\":\"value\"}";

  private JacksonParser parser = new JacksonParser();

  @Test
  public void testWriteToString() throws JsonProcessingException {
    Map<String, String> object = new LinkedHashMap<>();
    object.put(KEY, VALUE);

    String result = parser.writeToString(object);
    assertEquals(EXPECTED_JSON, result);
  }

  @Test
  public void testWriteToObject() throws IOException {
    Map<String, Object> result = parser.writeToObject(EXPECTED_JSON, Map.class);
    assertTrue(result.containsKey(KEY));
    assertEquals(VALUE, result.get(KEY));
  }

}
