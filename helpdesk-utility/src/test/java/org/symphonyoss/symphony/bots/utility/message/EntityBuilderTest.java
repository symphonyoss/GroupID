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

  private EntityBuilder entityBuilder;

  @Before
  public void setUp() throws Exception {
    entityBuilder = new EntityBuilder();
  }

  @Test
  public void createEntity() {
    EntityBuilder entity = entityBuilder.createEntity();
    Map<String, Object> object = entity.toObject();
    assertTrue(object.isEmpty());
    assertEquals(0, object.size());
  }

  @Test
  public void createEntityWithValues() {
    EntityBuilder builder = entityBuilder.createEntity(TYPE,VERSION);
    Map<String, Object> object = builder.toObject();
    assertNotNull(object.get("type"));
    assertNotNull(object.get("version"));
    assertEquals(2, object.size());
  }

  @Test
  public void addField() {
    EntityBuilder builder = entityBuilder.addField(FIELD, FIELD);
    Map<String, Object> object = builder.toObject();
    assertNotNull(object.get(FIELD));
    assertEquals(1, object.size());
  }

  @Test
  public void toObject() {
    EntityBuilder builder = entityBuilder.createEntity(TYPE,VERSION);
    Map<String, Object> object = builder.toObject();
    assertEquals(2, object.size());
  }

  @Test
  public void build() throws JsonProcessingException {
    EntityBuilder builder = entityBuilder.createEntity(TYPE,VERSION);
    builder.addField(FIELD,FIELD);

    String result = builder.build();
    assertNotNull(result);
    assertEquals("{\"type\":\"TYPE\",\"version\":\"VERSION\",\"FIELD\":\"FIELD\"}" , result);
  }
}