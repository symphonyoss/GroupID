package org.symphonyoss.symphony.bots.ai.conversation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class to manage idles from all created tickets, exists a service that check for each 5 seconds
 * if there is any ticket idle timer in the list and if it is valid.
 * If not, the service will send a message that a ticket has been idle and reset the time.
 * Close tickets are removed from idle list.
 *
 * Created by alexandre-silva-daitan on 16/3/17.
 */
public class IdleTimerManager {

  private Map<String, ProxyIdleTimer> proxyIdleTimerMap = new HashMap<>();

  private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

  public IdleTimerManager() {
    init();
  }

  /**
   * This service executes even 5 seconds and check if there is a idle in the list
   * if exists, compare the time of the idle with the configuration idle time and
   * if the time pass, a message will be sent to bot warning that ticket has been idle and the idle
   * time will be reset
   */
  private void init() {
    executorService.scheduleAtFixedRate(() -> {
      proxyIdleTimerMap.values().stream()
          .filter((proxyIdleTimer) -> {
            proxyIdleTimer.setTime(proxyIdleTimer.getTime() + 5);

            long idle = proxyIdleTimer.getIdleTime();
            int current = proxyIdleTimer.getTime();

            TimeUnit type = proxyIdleTimer.getTimeUnit();
            return type.toSeconds(idle) < current;
          })
          .forEach((proxyIdleTimer) -> {
            proxyIdleTimer.onIdleTimeout();
            proxyIdleTimer.reset();
          });
    }, 1000, 5000, TimeUnit.MILLISECONDS);
  }

  public void put(String ticketId, ProxyIdleTimer proxyIdleTimer) {
    proxyIdleTimerMap.put(ticketId, proxyIdleTimer);
  }

  public void remove(String ticketId) {
    proxyIdleTimerMap.remove(ticketId);
  }

  public boolean containsKey(String ticketId) {
    return ticketId != null ? proxyIdleTimerMap.containsKey(ticketId) : false;
  }

  public ProxyIdleTimer get(String ticketId) {
    return ticketId != null ? proxyIdleTimerMap.get(ticketId) : null;
  }

  private void shutdown() {
    executorService.shutdown();
  }
}
