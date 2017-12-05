package org.symphonyoss.symphony.bots.ai.conversation;

import java.util.Collections;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by nick.tarsillo on 11/28/17.
 */
public abstract class ProxyIdleTimer {
  private static Timer timer;
  private static Set<ProxyIdleTimer> proxyIdleTimers;
  static {
    timer = new Timer();
    proxyIdleTimers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        for(ProxyIdleTimer proxyIdleTimer: proxyIdleTimers) {
          proxyIdleTimer.setTime(proxyIdleTimer.getTime() + 1);

          int idle = proxyIdleTimer.getIdleTime();
          int current = proxyIdleTimer.getTime();
          TimeUnit type = proxyIdleTimer.getTimeUnit();
          if(type.toSeconds(idle) < current) {
            proxyIdleTimer.onIdleTimeout();
            proxyIdleTimer.reset();
          }
        }
      }
    }, 10000, 10000);
  }

  private int time;
  private int idleTime;
  private TimeUnit timeUnit;

  public ProxyIdleTimer(int idleTime, TimeUnit timeUnit) {
    this.idleTime = idleTime;
    this.timeUnit = timeUnit;
  }

  public void start() {
    ProxyIdleTimer.proxyIdleTimers.add(this);
  }

  public void stop() {
    ProxyIdleTimer.proxyIdleTimers.remove(this);
  }

  public void reset() {
    time = 0;
  }

  public abstract void onIdleTimeout();

  public int getIdleTime() {
    return idleTime;
  }

  public void setIdleTime(int idleTime) {
    this.idleTime = idleTime;
  }

  public TimeUnit getTimeUnit() {
    return timeUnit;
  }

  public void setTimeUnit(TimeUnit timeUnit) {
    this.timeUnit = timeUnit;
  }

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }
}