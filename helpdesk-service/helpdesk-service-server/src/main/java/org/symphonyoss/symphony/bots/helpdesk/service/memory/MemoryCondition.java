package org.symphonyoss.symphony.bots.helpdesk.service.memory;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Condition to store the application data in-memory
 * Created by rsanchez on 24/11/17.
 */
public class MemoryCondition implements Condition {

  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata annotatedTypeMetadata) {
    String property = context.getEnvironment().getProperty("env");
    return property != null && property.equals("dev");
  }

}
