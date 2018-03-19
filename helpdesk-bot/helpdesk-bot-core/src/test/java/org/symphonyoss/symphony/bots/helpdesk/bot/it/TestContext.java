package org.symphonyoss.symphony.bots.helpdesk.bot.it;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by crepache on 22/02/18.
 * Class to store the variables to use on any part of tests.
 */
public class TestContext {

  private static final TestContext INSTANCE = new TestContext();

  private Map<String, SymUser> users = new HashMap<>();

  private Map<String, SymphonyClient> authUsers = new HashMap<>();

  private Room queueRoom;

  private String certsDir;

  private TestContext() {
  }

  public static TestContext getInstance() {
    return INSTANCE;
  }

  public SymUser getUser(String key) {
    if (key == null) {
      return null;
    }

    return users.get(key);
  }

  public void setUsers(String key, SymUser user) {
    if (key == null) {
      throw new IllegalArgumentException("Parameter key is required.");
    }

    users.put(key, user);
  }

  public Room getQueueRoom() {
    return queueRoom;
  }

  public void setQueueRoom(Room queueRoom) {
    this.queueRoom = queueRoom;
  }

  public SymphonyClient getAuthenticatedUser(String username) {
    return authUsers.get(username);
  }

  public void setAuthenticatedUser(String username, SymphonyClient symphonyClient) {
    if (username == null) {
      throw new IllegalArgumentException("Username must not be null.");
    }

    this.authUsers.put(username, symphonyClient);
  }

  public String getCertsDir() {
    return certsDir;
  }

  public void setCertsDir(String certsDir) {
    this.certsDir = certsDir;
  }
}
