package org.symphonyoss.symphony.bots.helpdesk.service.mongo;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Condition class to decide if the Mongo DAO's must be created.
 *
 * Created by rsanchez on 24/11/17.
 */
public class MongoCondition implements Condition {

  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata annotatedTypeMetadata) {
    String property = context.getEnvironment().getProperty("env");
    return property == null || !property.equals("dev");
  }

}
