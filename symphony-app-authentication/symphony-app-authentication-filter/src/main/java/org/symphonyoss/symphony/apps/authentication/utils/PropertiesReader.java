package org.symphonyoss.symphony.apps.authentication.utils;

/**
 * Utility class to read parameters from system properties or environment variables.
 *
 * Created by rsanchez on 10/01/18.
 */
public class PropertiesReader {

  public static String readRequiredProperty(String property, String errorMessage) {
    String value = System.getenv(property.toUpperCase());

    if ((value == null) || (value.isEmpty())) {
      value = System.getProperty(property.toLowerCase());
    }

    if ((value == null) || (value.isEmpty())) {
      throw new IllegalStateException(errorMessage);
    }

    return value;
  }

  public static String readProperty(String property, String defaultValue) {
    String value = System.getenv(property.toUpperCase());

    if ((value == null) || (value.isEmpty())) {
      value = System.getProperty(property.toLowerCase(), defaultValue);
    }

    return value;
  }

}
