package org.symphonyoss.symphony.bots.helpdesk.service.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.bots.helpdesk.service.config.HelpDeskServiceConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by nick.tarsillo on 9/25/17.
 */
@Component
public class SQLConnection {
  private static final Logger LOG = LoggerFactory.getLogger(SQLConnection.class);

  private Connection conn = null;
  private String databaseDriver;
  private String databaseUrl;
  private String databaseUser;
  private String databasePassword;

  @Autowired
  public SQLConnection(HelpDeskServiceConfig helpDeskServiceConfig) {
    this.databaseDriver = helpDeskServiceConfig.getDatabaseDriver();
    this.databaseUrl = helpDeskServiceConfig.getDatabaseUrl();
    this.databaseUser = helpDeskServiceConfig.getDatabaseUser();
    this.databasePassword = helpDeskServiceConfig.getDatabasePassword();

    LOG.info("Creating sql connection: " + databaseDriver + ", " + databaseUrl
        + ", " + databaseUser + ", " + databasePassword);
    try {
      Class.forName(databaseDriver);

      conn = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
      LOG.info("SQL connection created.");
    } catch (SQLException | ClassNotFoundException e) {
      LOG.error("Could not establish SQL connection: ", e);
    }
  }

  public Statement createStatement() throws SQLException {
    return conn.createStatement();
  }
}
