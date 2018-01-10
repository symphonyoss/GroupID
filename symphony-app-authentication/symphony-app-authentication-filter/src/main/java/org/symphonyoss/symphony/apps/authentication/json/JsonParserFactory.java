package org.symphonyoss.symphony.apps.authentication.json;

/**
 * Factory to build {@link JsonParser} component
 *
 * Created by rsanchez on 10/01/18.
 */
public class JsonParserFactory {

  private static final JsonParserFactory INSTANCE = new JsonParserFactory();

  private JsonParser parser;

  private JsonParserFactory() {}

  public static JsonParserFactory getInstance() {
    return INSTANCE;
  }

  public void setComponent(JsonParser parser) {
    if (parser == null) {
      throw new IllegalArgumentException("Invalid parser implementation. It mustn't be null");
    }

    this.parser = parser;
  }

  public JsonParser getComponent() {
    if (parser == null) {
      throw new IllegalStateException("There is no implementation defined for this component");
    }

    return parser;
  }

}
