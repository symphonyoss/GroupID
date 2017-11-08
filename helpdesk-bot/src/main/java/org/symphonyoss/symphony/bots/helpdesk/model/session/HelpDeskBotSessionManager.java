package org.symphonyoss.symphony.bots.helpdesk.model.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nick.tarsillo on 11/6/17.
 */
public class HelpDeskBotSessionManager {
  private static final Logger LOG = LoggerFactory.getLogger(HelpDeskBotSessionManager.class);
  private static HelpDeskBotSessionManager defaultSessionManager;

  private Map<String, HelpDeskBotSession> helpDeskBotSessionMap;

  public HelpDeskBotSessionManager() {
    helpDeskBotSessionMap = new HashMap<>();
  }

  public static HelpDeskBotSessionManager getDefaultSessionManager() {
    return defaultSessionManager;
  }

  public static void setDefaultSessionManager(
      HelpDeskBotSessionManager defaultSessionManager) {
    HelpDeskBotSessionManager.defaultSessionManager = defaultSessionManager;
  }

  public HelpDeskBotSession getSession(String groupId) {
    try {
      return helpDeskBotSessionMap.get(groupId);
    } catch (NullPointerException e) {
      LOG.warn(groupId + " not found.");
      return null;
    }
  }

  public void registerSession(String groupId, HelpDeskBotSession helpDeskBotSession) {
    helpDeskBotSessionMap.put(groupId, helpDeskBotSession);
  }
}
