package org.symphonyoss.symphony.bots.utility.message;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EntityBuilderTest {


  private EntityBuilder entityBuilder;

  @Before
  public void setUp() throws Exception {
    entityBuilder = new EntityBuilder();
  }

  @Test
  public void createEntity() {
    entityBuilder.createEntity();
  }

  @Test
  public void addField() {
  }

  @Test
  public void toObject() {
  }

  @Test
  public void build() {
  }
}