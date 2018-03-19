package org.symphonyoss.symphony.bots.helpdesk.bot.filter;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.bots.helpdesk.bot.HelpDeskBot;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by rsanchez on 18/12/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class HelpDeskApiFilterTest {

  @Mock
  private HelpDeskBot helpDeskBot;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain chain;

  @Mock
  private PrintWriter writer;

  @InjectMocks
  private HelpDeskApiFilter filter;

  @Test
  public void testReady() throws IOException, ServletException {
    doReturn(true).when(helpDeskBot).isReady();

    filter.doFilter(request, response, chain);

    verify(chain, times(1)).doFilter(request, response);
  }

  @Test
  public void testNotReady() throws IOException, ServletException {
    doReturn(false).when(helpDeskBot).isReady();
    doReturn(writer).when(response).getWriter();

    filter.doFilter(request, response, chain);

    verify(writer, times(1)).write("{\"info\":\"HelpDesk Bot is out of service. Please verify "
        + "the health-check to get more details\"}");
  }
}
