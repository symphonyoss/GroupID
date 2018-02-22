package org.symphonyoss.symphony.bots.helpdesk.bot.it;

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

  private Map<UsersEnum, SymUser> users = new HashMap();

  private Room queueRoom;

  private TestContext() {
  }

  public static TestContext getInstance() {
    return INSTANCE;
  }

  public SymUser getUser(UsersEnum key) {
    if (key == null) {
      return null;
    }

    return users.get(key);
  }

  public void setUsers(UsersEnum key, SymUser user) {
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
}
