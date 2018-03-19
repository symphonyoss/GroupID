package org.symphonyoss.symphony.bots.helpdesk.bot.it.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.UserNotFoundException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.UserAttributes;
import org.symphonyoss.symphony.pod.model.UserCreate;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class to create users.
 *
 * Created by rsanchez on 01/03/18.
 */
public class UserUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserUtils.class);

  private static final String ROLE_INDIVIDUAL = "INDIVIDUAL";

  private final SymphonyClient symphonyClient;

  public UserUtils(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
  }

  /**
   * Creates a new service account.
   *
   * @param username
   */
  public SymUser createServiceAccount(String username) {
    return createServiceAccount(username, Arrays.asList(ROLE_INDIVIDUAL));
  }

  public SymUser createServiceAccount(String username, List<String> roles) {
    try {
      SymUser user = symphonyClient.getUsersClient().getUserFromName(username);
      LOGGER.info("User " + username + " already exists");

      return user;
    } catch (UserNotFoundException e) {
      return createServiceUser(username, roles);
    } catch (UsersClientException e) {
      throw new IllegalStateException("Cannot search user.", e);
    }
  }

  /**
   * Creates a new end user.
   *
   * @param username
   */
  public SymUser createEndUser(String username) {
    return createEndUser(username, Arrays.asList(ROLE_INDIVIDUAL));
  }

  public SymUser createEndUser(String username, List<String> roles) {
    try {
      SymUser user = symphonyClient.getUsersClient().getUserFromName(username);
      LOGGER.info("User " + username + " already exists");

      return user;
    } catch (UserNotFoundException e) {
      return createNormalUser(username, roles);
    } catch (UsersClientException e) {
      throw new IllegalStateException("Cannot search user.", e);
    }
  }

  /**
   * Creates a new service account user on POD
   *
   * @param username username
   * @param roles list of roles
   * @return SymUser object
   */
  private SymUser createServiceUser(String username, List<String> roles) {
    UserCreate userCreate = new UserCreate();

    UserAttributes userAttributes = buildUserAttributes(username, UserAttributes.AccountTypeEnum.SYSTEM);
    userCreate.setUserAttributes(userAttributes);

    userCreate.setRoles(roles);

    try {
      return symphonyClient.getUsersClient().createUser(userCreate);
    } catch (UsersClientException e) {
      throw new IllegalStateException("Cannot create user.", e);
    }
  }

  /**
   * Creates a new user on POD
   *
   * @param username username
   * @param roles list of roles
   * @return SymUser object
   */
  private SymUser createNormalUser(String username, List<String> roles) {
    UserCreate userCreate = new UserCreate();

    UserAttributes userAttributes = buildUserAttributes(username, UserAttributes.AccountTypeEnum.NORMAL);
    userAttributes.setFirstName(username);
    userAttributes.setLastName("Test");

    userCreate.setUserAttributes(userAttributes);

    userCreate.setRoles(roles);

    try {
      return symphonyClient.getUsersClient().createUser(userCreate);
    } catch (UsersClientException e) {
      throw new IllegalStateException("Cannot create user.", e);
    }
  }

  /**
   * Build user attributes
   *
   * @param userName userName
   * @param userType type of account
   * @return the SymUser
   */
  private UserAttributes buildUserAttributes(String userName, UserAttributes.AccountTypeEnum userType) {
    UserAttributes userAttrs = new UserAttributes();
    userAttrs.setEmailAddress(userName + "@example.com");
    userAttrs.setDisplayName(userName);
    userAttrs.setUserName(userName);
    userAttrs.setAccountType(userType);

    return userAttrs;
  }

}
