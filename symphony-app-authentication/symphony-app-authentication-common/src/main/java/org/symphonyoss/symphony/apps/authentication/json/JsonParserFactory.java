package org.symphonyoss.symphony.apps.authentication.json;

import org.symphonyoss.symphony.apps.authentication.AbstractFactory;

/**
 * Factory to build {@link JsonParser} component
 *
 * Created by rsanchez on 10/01/18.
 */
public class JsonParserFactory extends AbstractFactory<JsonParser> {

  private static final JsonParserFactory INSTANCE = new JsonParserFactory();

  private JsonParserFactory() {}

  public static JsonParserFactory getInstance() {
    return INSTANCE;
  }

}
