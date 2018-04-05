package org.symphonyoss.symphony.bots.ai.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Map functions for AI command line arguments manipulation
 * <p>
 * Created by nick.tarsillo on 8/20/17.
 */
public class AiArgumentMap {

  private Map<String, String> argumentMap = new LinkedHashMap<>();

  /**
   * Adds an argument and its value to the map
   * @param argument argument name
   * @param val argument value
   */
  public void addArgument(String argument, String val) {
    argumentMap.put(argument, val);
  }

  /**
   * Retrieve the argument map key set
   * @return the argument map key set
   */
  public Set<String> getKeySet() {
    return argumentMap.keySet();
  }

  /**
   * Get the given argument value as a {@link String}
   * @param argument argument name
   * @return argument value as a {@link String}
   */
  public String getArgumentAsString(String argument) {
    String val = argumentMap.get(argument);
    return val;
  }

  /**
   * Get the given argument value as a {@link Integer}
   * @param argument argument name
   * @return argument value as a {@link Integer}
   */
  public Integer getArgumentAsInteger(String argument) {
    String val = argumentMap.get(argument);
    return Integer.parseInt(val);
  }

  /**
   * Get the given argument value as a {@link Long}
   * @param argument argument name
   * @return argument value as a {@link Long}
   */
  public Long getArgumentAsLong(String argument) {
    String val = argumentMap.get(argument);
    return Long.parseLong(val);
  }

  /**
   * Get the given argument value as a {@link Double}
   * @param argument argument name
   * @return argument value as a {@link Double}
   */
  public Double getArgumentAsDouble(String argument) {
    String val = argumentMap.get(argument);
    return Double.parseDouble(val);
  }
}
