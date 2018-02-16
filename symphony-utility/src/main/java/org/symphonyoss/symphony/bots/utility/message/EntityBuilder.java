package org.symphonyoss.symphony.bots.utility.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builder class to create entity objects.
 * <p>
 * Created by rsanchez on 12/01/17.
 */
public class EntityBuilder {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private Map<String, Object> content = new LinkedHashMap<>();

  private EntityBuilder() {}

  private EntityBuilder(String type, String version) {
    this.content.put("type", type);
    this.content.put("version", version);
  }

  /**
   * Create a new entity without type and version.
   * @return Builder class
   */
  public static EntityBuilder createEntity() {
    return new EntityBuilder();
  }

  /**
   * Create a new entity with type and version.
   *
   * @param type Entity type
   * @param version Entity version
   * @return Builder class
   */
  public static EntityBuilder createEntity(String type, String version) {
    return new EntityBuilder(type, version);
  }

  /**
   * Add a new field
   *
   * @param field Field name
   * @param value Field content
   * @return Builder class
   */
  public EntityBuilder addField(String field, Object value) {
    if (value != null) {
      this.content.put(field, value);
    }

    return this;
  }

  public Map<String, Object> toObject() {
    return content;
  }

  /**
   * Builds entity object as string.
   *
   * @return Entity object as string
   * @throws JsonProcessingException Failure to serialize JSON content as a string.
   */
  public String build() throws JsonProcessingException {
    return MAPPER.writeValueAsString(content);
  }

}
