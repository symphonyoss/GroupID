package org.symphonyoss.symphony.bots.ai.helpdesk.conversation;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.bots.ai.helpdesk.conversation.IdleTimerManager;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by alexandre-silva-daitan on 16/3/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class IdleTimerManagerTest {

  private static final String TICKET_ID = "TICKET_ID";

  private static final String UNEXISTENT_TICKET_ID = "UNEXISTENT_TICKET_ID";

  @Mock
  private ScheduledExecutorService executorService;

  @InjectMocks
  private IdleTimerManager idleTimerManager;

  @Test
  public void put() {
    idleTimerManager.put(TICKET_ID, new ProxyIdleTimer(5000L, TimeUnit.MILLISECONDS) {
      @Override
      public void onIdleTimeout() {
        // Do nothing
      }
    });

    ProxyIdleTimer proxyIdleTimer = idleTimerManager.get(TICKET_ID);

    assertEquals(new Long(5000), proxyIdleTimer.getIdleTime());
    assertEquals(TimeUnit.MILLISECONDS, proxyIdleTimer.getTimeUnit());
  }

  @Test
  public void remove() {
    put();

    idleTimerManager.remove(TICKET_ID);

    ProxyIdleTimer proxyIdleTimer = idleTimerManager.get(TICKET_ID);
    assertNull(proxyIdleTimer);
  }

  @Test
  public void containsKey() {
    put();

    boolean response = idleTimerManager.containsKey(TICKET_ID);
    assertEquals(true, response);
  }

  @Test
  public void containsKeyWithNullTicketId() {
    boolean response = idleTimerManager.containsKey(null);
    assertEquals(false, response);
  }

  @Test
  public void containsKeyWithUnexistentTicketId() {
    boolean response = idleTimerManager.containsKey(UNEXISTENT_TICKET_ID);
    assertEquals(false, response);
  }

  @Test
  public void get() {
    put();

    ProxyIdleTimer proxyIdleTimer = idleTimerManager.get(TICKET_ID);
    assertNotNull(proxyIdleTimer);
  }

  @Test
  public void getWithNullTicketId() {
    ProxyIdleTimer proxyIdleTimer = idleTimerManager.get(null);
    assertEquals(null, proxyIdleTimer);
  }

  @Test
  public void shutdown() {
    idleTimerManager.shutdown();
    
    verify(executorService, times(1)).shutdown();
  }
}