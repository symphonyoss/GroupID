package org.symphonyoss.symphony.bots.utility.validation;

import javax.ws.rs.BadRequestException;

/**
 * Created by nick.tarsillo on 11/16/17.
 */
public class ValidationUtil {
  private static String INVALID = " is invalid.";

  public Long validateParseLong(String name, String val) {
    Long longVal;
    try {
      longVal = Long.parseLong(val);
    } catch (NumberFormatException e) {
      throw new BadRequestException(name + INVALID);
    }

    return longVal;
  }
}
