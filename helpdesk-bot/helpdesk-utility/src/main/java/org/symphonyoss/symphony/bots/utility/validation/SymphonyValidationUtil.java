package org.symphonyoss.symphony.bots.utility.validation;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.symphony.clients.model.SymStreamAttributes;
import org.symphonyoss.symphony.clients.model.SymUser;

import javax.ws.rs.BadRequestException;

/**
 * Helper class for validating different requited symphony identifiers.
 * <p>
 * Created by nick.tarsillo on 11/16/17.
 */
public class SymphonyValidationUtil {

  private static final String INVALID = " is invalid.";

  private SymphonyClient symphonyClient;

  public SymphonyValidationUtil(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
  }

  /**
   * Checks if a user exists under a given user id
   * @param userId the user id
   * @return if user exists, return the symphony user
   * @throws BadRequestException on user id is invalid or does not exists
   */
  public SymUser validateUserId(Long userId) {
    try {
      return symphonyClient.getUsersClient().getUserFromId(userId);
    } catch (UsersClientException e) {
      throw new BadRequestException("The user id " + userId + INVALID);
    }
  }

  /**
   * Checks if a given stream exists
   * @param streamId the stream id
   * @return if stream exists, the stream attributes
   * @throws BadRequestException on stream does not exists
   */
  public SymStreamAttributes validateStream(String streamId) {
    try {
      return symphonyClient.getStreamsClient().getStreamAttributes(streamId);
    } catch (StreamsException e) {
      throw new BadRequestException("The stream " + streamId + INVALID);
    }
  }

}
