package org.symphonyoss.symphony.bots.helpdesk.service.model.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
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

  @Autowired
  public SQLConnection(@Value(HelpDeskServiceConfig.DATABASE_DRIVER) String databaseDriver,
      @Value(HelpDeskServiceConfig.DATABASE_URL) String databaseUrl,
      @Value(HelpDeskServiceConfig.DATABASE_USER) String databaseUser,
      @Value(HelpDeskServiceConfig.DATABASE_PASSWORD) String databasePassword) {
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
