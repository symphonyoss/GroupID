package org.symphonyoss.symphony.bots.utility.validation;

import org.springframework.stereotype.Component;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.symphony.clients.model.SymStreamAttributes;
import org.symphonyoss.symphony.clients.model.SymUser;

import javax.ws.rs.BadRequestException;

/**
 * Created by nick.tarsillo on 11/16/17.
 *
 * Helper class for validating different requited symphony identifiers.
 */
@Component
public class SymphonyValidationUtil {
  private static final String INVALID = " is invalid.";

  private SymphonyClient symphonyClient;

  public SymphonyValidationUtil(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
  }

  /**
   * Checks if a user exists under a given user id
   * Checks if the user Id is valid
   * @param userId the user id
   * @return if valid, return the sym user
   * @throws BadRequestException on user id is invalid or does not exists
   */
  public SymUser validateUserId(String userId) {
    try {
      return symphonyClient.getUsersClient().getUserFromId(
          validateParseLong("User Id", userId));
    } catch (UsersClientException e) {
      throw new BadRequestException("The user id " + userId + INVALID);
    }
  }

  /**
   * Checks if a given stream exists
   * @param streamId the stream id
   * @return if valid, the stream attributes
   * @throws BadRequestException on stream does not exists
   */
  public SymStreamAttributes validateStream(String streamId) {
    try {
      return symphonyClient.getStreamsClient().getStreamAttributes(streamId);
    } catch (StreamsException e) {
      throw new BadRequestException("The stream " + streamId + INVALID);
    }
  }

  /**
   * Validate if a given parameter is
   * @param name
   * @param val
   * @return
   */
  private Long validateParseLong(String name, String val) {
    try {
      return Long.parseLong(val);
    } catch (NumberFormatException e) {
      throw new BadRequestException(name + INVALID);
    }
  }
}
