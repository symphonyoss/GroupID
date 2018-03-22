package org.symphonyoss.symphony.bots.utility.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class EntityBuilderTest {


  private static final String FIELD = "FIELD";
  private static final String TYPE = "TYPE";
  private static final String VERSION = "VERSION";

  @Test
  public void createEntity() {
    EntityBuilder entity = EntityBuilder.createEntity();
    Map<String, Object> object = entity.toObject();
    assertTrue(object.isEmpty());
  }

  @Test
  public void createEntityWithValues() {
    EntityBuilder builder = EntityBuilder.createEntity();
    Map<String, Object> object = builder.toObject();
    assertEquals(null,object.get("type"));
    assertEquals(null, object.get("version"));
  }

  @Test
  public void addField() {
    EntityBuilder builder = EntityBuilder.createEntity();
    builder.addField(FIELD, FIELD);
    Map<String, Object> object = builder.toObject();
    assertEquals(FIELD,object.get(FIELD));
    assertEquals(1, object.size());
  }

  @Test
  public void toObject() {
    EntityBuilder builder = EntityBuilder.createEntity(TYPE,VERSION);
    Map<String, Object> object = builder.toObject();
    assertEquals(2, object.size());
  }

  @Test
  public void build() throws JsonProcessingException {
    EntityBuilder builder = EntityBuilder.createEntity(TYPE,VERSION);
    builder.addField(FIELD,FIELD);

    String result = builder.build();
    assertNotNull(result);
    assertEquals("{\"type\":\"TYPE\",\"version\":\"VERSION\",\"FIELD\":\"FIELD\"}" , result);
  }
}