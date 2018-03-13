package org.symphonyoss.symphony.apps.authentication.json;

import java.io.IOException;

/**
 * Interface to parse JSON documents.
 *
 * Created by rsanchez on 10/01/18.
 */
public interface JsonParser {

  String writeToString(Object obj) throws IOException;

  <T> T writeToObject(String json, Class<T> clazz) throws IOException;

}
