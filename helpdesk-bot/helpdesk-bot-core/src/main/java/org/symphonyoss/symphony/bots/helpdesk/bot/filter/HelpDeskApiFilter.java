package org.symphonyoss.symphony.bots.helpdesk.bot.filter;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.symphonyoss.symphony.bots.helpdesk.bot.HelpDeskBot;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

/**
 * Created by rsanchez on 18/12/17.
 */
public class HelpDeskApiFilter implements Filter {

  private static final String INFO_KEY = "info";

  private static final String ERROR_MESSAGE = "HelpDesk Bot is out of service. Please verify the "
      + "health-check to get more details";

  private HelpDeskBot helpDeskBot;

  /**
   * Initialize Spring components.
   * @param config Filter configuration
   * @throws ServletException Report failure to initialize the filter
   */
  @Override
  public void init(FilterConfig config) throws ServletException {
    WebApplicationContext applicationContext =
        WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
    this.helpDeskBot = applicationContext.getBean(HelpDeskBot.class);
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    if (helpDeskBot.isReady()) {
      filterChain.doFilter(servletRequest, servletResponse);
    } else {
      HttpServletResponse response = (HttpServletResponse) servletResponse;

      response.setContentType(APPLICATION_JSON);
      response.setStatus(Response.Status.SERVICE_UNAVAILABLE.getStatusCode());

      ObjectNode message = JsonNodeFactory.instance.objectNode();
      message.put(INFO_KEY, ERROR_MESSAGE);

      response.getWriter().write(message.toString());
    }
  }

  @Override
  public void destroy() {
    // Do nothing
  }
}
