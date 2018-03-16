package org.symphonyoss.symphony.bots.ai;

/**
 * This class defines identifiers for an AI response. It contains the receiver name and the
 * sender user id, i.e., to where the response should be sent.
 * <p>
 * Created by nick.tarsillo on 8/23/17.
 */
public interface AiResponseIdentifier {

  /**
   * Retrieve the name given for the user
   * @return the identity name
   */
  String getIdentityName();

  /**
   * Retrieve the unique identifier that defines the user to respond to.
   * @return The response user identifier
   */
  String getResponseIdentifier();
}
