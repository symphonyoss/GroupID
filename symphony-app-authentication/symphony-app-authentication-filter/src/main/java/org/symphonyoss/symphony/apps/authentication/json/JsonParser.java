package org.symphonyoss.symphony.apps.authentication.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * JSON parser utility.
 *
 * Created by rsanchez on 09/01/18.
 */
public class JsonParser {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static String writeToString(Object obj) throws JsonProcessingException {
    return MAPPER.writeValueAsString(obj);
  }

  public static <T> T writeToObject(String json, Class<T> clazz) throws IOException {
    return MAPPER.readValue(json, clazz);
  }

}
