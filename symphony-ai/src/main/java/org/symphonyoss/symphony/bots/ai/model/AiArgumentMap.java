package org.symphonyoss.symphony.bots.ai.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by nick.tarsillo on 8/20/17.
 * A map of arguments.
 */
public class AiArgumentMap {
  private Map<String, String> argumentMap = new LinkedHashMap<>();

  public void addArgument(String argument, String val) {
    argumentMap.put(argument, val);
  }

  public Set<String> getKeySet() {
    return argumentMap.keySet();
  }

  public String getArgumentAsString(String argument) {
    String val = argumentMap.get(argument);
    return val;
  }

  public Integer getArgumentAsInteger(String argument) {
    String val = argumentMap.get(argument);
    return Integer.parseInt(val);
  }

  public Long getArgumentAsLong(String argument) {
    String val = argumentMap.get(argument);
    return Long.parseLong(val);
  }

  public Double getArgumentAsDouble(String argument) {
    String val = argumentMap.get(argument);
    return Double.parseDouble(val);
  }
}
