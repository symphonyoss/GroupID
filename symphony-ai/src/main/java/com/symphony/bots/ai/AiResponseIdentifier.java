package com.symphony.bots.ai;

/**
 * Created by nick.tarsillo on 8/23/17.
 */
public interface AiResponseIdentifier {
  /**
   * A name for the user (does not need to be unique)
   */
  String getIdentityName();

  /**
   * A unique identifiers that defines the user to respond to.
   */
  String getResponseIdentifier();
}
