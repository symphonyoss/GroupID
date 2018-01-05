package org.symphonyoss.symphony.bots.ai.model;

/**
 * Defines the type of an argument. Used for Ai commands.
 */
public enum ArgumentType {
  STRING,
  LONG {
    @Override
    public boolean checkArgument(String value) {
      try {
        Long.parseLong(value);
        return true;
      } catch (NumberFormatException e) {
        return false;
      }
    }
  },
  DOUBLE {
    @Override
    public boolean checkArgument(String value) {
      try {
        Double.parseDouble(value);
        return true;
      } catch (NumberFormatException e) {
        return false;
      }
    }
  },
  INTEGER {
    @Override
    public boolean checkArgument(String value) {
      try {
        Integer.parseInt(value);
        return true;
      } catch (NumberFormatException e) {
        return false;
      }
    }
  };

  public boolean checkArgument(String value) {
    return true;
  }

}