package org.symphonyoss.symphony.bots.utility.validation;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.symphony.clients.model.SymStreamAttributes;
import org.symphonyoss.symphony.clients.model.SymUser;

import javax.ws.rs.BadRequestException;

/**
 * Created by nick.tarsillo on 11/16/17.
 */
public class SymphonyValidationUtil {
  private static String INVALID = " is invalid.";

  private SymphonyClient symphonyClient;

  public SymphonyValidationUtil(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
  }

  public SymUser validateUserId(String userId) {
    try {
      return symphonyClient.getUsersClient().getUserFromId(
          validateParseLong("User Id", userId));
    } catch (UsersClientException e) {
      throw new BadRequestException("The user id " + userId + INVALID);
    }
  }

  public SymStreamAttributes validateStream(String streamId) {
    try {
      return symphonyClient.getStreamsClient().getStreamAttributes(streamId);
    } catch (StreamsException e) {
      throw new BadRequestException("The stream " + streamId + INVALID);
    }
  }

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
