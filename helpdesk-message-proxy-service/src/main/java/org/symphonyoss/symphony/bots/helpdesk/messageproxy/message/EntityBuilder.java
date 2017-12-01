package org.symphonyoss.symphony.bots.helpdesk.messageproxy.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by rsanchez on 01/12/17.
 */
public class EntityBuilder {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private Map<String, Object> content = new LinkedHashMap<>();

  private EntityBuilder() {}

  private EntityBuilder(String type, String version) {
    this.content.put("type", type);
    this.content.put("version", version);
  }

  public static EntityBuilder createEntity() {
    return new EntityBuilder();
  }

  public static EntityBuilder createEntity(String type, String version) {
    return new EntityBuilder(type, version);
  }

  public EntityBuilder addField(String field, Object value) {
    if (value != null) {
      this.content.put(field, value);
    }

    return this;
  }

  public Map<String, Object> toObject() {
    return content;
  }

  public String build() throws JsonProcessingException {
    return MAPPER.writeValueAsString(content);
  }

}
