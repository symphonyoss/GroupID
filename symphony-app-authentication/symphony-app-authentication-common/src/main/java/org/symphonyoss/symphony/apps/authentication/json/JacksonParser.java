package org.symphonyoss.symphony.apps.authentication.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * JSON parser implementation.
 *
 * Created by rsanchez on 09/01/18.
 */
public class JacksonParser implements JsonParser {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public JacksonParser() {
    MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public String writeToString(Object obj) throws JsonProcessingException {
    return MAPPER.writeValueAsString(obj);
  }

  public <T> T writeToObject(String json, Class<T> clazz) throws IOException {
    return MAPPER.readValue(json, clazz);
  }

}
