package org.symphonyoss.symphony.bots.ai.common;

import java.util.concurrent.TimeUnit;

/**
 * Constants used in the Symphony's AI context
 * <p>
 * Created by nick.tarsillo on 8/20/17.
 */
public class AiConstants {
  public static final int EXPIRE_TIME = 12;
  public static final TimeUnit EXPIRE_TIME_UNIT = TimeUnit.HOURS;
  public static final String ARGUMENT_START_CHAR = "{";
  public static final String ARGUMENT_END_CHAR = "}";
  public static final String SUGGEST = "Did you mean, ";
  public static final String NOT_COMMAND = "%s is not a command.";
  public static final String MENU_TITLE = "Check the usage: ";
}
