package org.symphonyoss.symphony.bots.ai.conversation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class to manage Idles
 * created by alexandre-silva-daitan
 */
public class IdleTimerManager {

  private Map<String, ProxyIdleTimer> proxyIdleTimerMap = new HashMap<>();

  private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

  public IdleTimerManager() {
    init();
  }

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
