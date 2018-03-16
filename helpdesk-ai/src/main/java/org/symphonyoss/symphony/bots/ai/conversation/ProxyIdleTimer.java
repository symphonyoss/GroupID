package org.symphonyoss.symphony.bots.ai.conversation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Timer to keep track of how long a ticket room has been unattended by agents.
 * Created by nick.tarsillo on 11/28/17.
 */
public abstract class ProxyIdleTimer {

  private int time;
  private long idleTime;
  private TimeUnit timeUnit;

  public ProxyIdleTimer(long idleTime, TimeUnit timeUnit) {
    this.idleTime = idleTime;
    this.timeUnit = timeUnit;
  }

  public void reset() {
    time = 0;
  }

  public abstract void onIdleTimeout();

  public Long getIdleTime() {
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