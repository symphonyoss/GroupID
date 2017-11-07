package org.symphonyoss.symphony.bots.helpdesk.service.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.bots.helpdesk.service.api.V1HelpDeskApi;
import org.symphonyoss.symphony.bots.helpdesk.service.api.factories.V1ApiServiceFactory;
import org.symphonyoss.symphony.bots.helpdesk.service.config.ServiceConfig;
import org.symphonyoss.symphony.bots.helpdesk.service.model.MembershipSQLService;
import org.symphonyoss.symphony.bots.helpdesk.service.model.SQLConnection;
import org.symphonyoss.symphony.bots.helpdesk.service.model.TicketSQLService;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

/**
 * Created by nick.tarsillo on 9/25/17.
 */
public class HelpDeskServiceInit extends HttpServlet {
  private static final Logger LOG = LoggerFactory.getLogger(HelpDeskServiceInit.class);

  @Override
  public void init(ServletConfig config){
    ServiceConfig.init();

    LOG.info("HelpDesk Service starting...");

    SQLConnection sqlConnection = new SQLConnection(
        System.getProperty(ServiceConfig.DATABASE_DRIVER),
        System.getProperty(ServiceConfig.DATABASE_URL),
        System.getProperty(ServiceConfig.DATABASE_USER),
        System.getProperty(ServiceConfig.DATABASE_PASSWORD));
    MembershipSQLService membershipSQLService = new MembershipSQLService(sqlConnection,
        System.getProperty(ServiceConfig.MEMBERSHIP_TABLE_NAME));
    TicketSQLService ticketSQLService = new TicketSQLService(sqlConnection,
        System.getProperty(ServiceConfig.TICKET_TABLE_NAME));
    V1HelpDeskApi v1HelpDeskApi = new V1HelpDeskApi(membershipSQLService, ticketSQLService);
    V1ApiServiceFactory.setService(v1HelpDeskApi);

    LOG.info("HelpDesk Service is ready to go!");
  }

}
