package org.symphonyoss.symphony.apps.authentication;

/**
 * Common factory methods.
 *
 * Created by rsanchez on 10/01/18.
 */
public class AbstractFactory<T> {

  private T component;

  public void setComponent(T component) {
    if (component == null) {
      throw new IllegalArgumentException("Invalid component implementation. It mustn't be null");
    }

    this.component = component;
  }

  public T getComponent() {
    if (component == null) {
      throw new IllegalStateException("There is no implementation defined for this component");
    }

    return component;
  }

  public boolean hasComponent() {
    return component != null;
  }

}
