package com.symphony.helpdesk.service.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by nick.tarsillo on 9/25/17.
 */
public class SQLConnection {
  private static final Logger LOG = LoggerFactory.getLogger(SQLConnection.class);

  private Connection conn = null;

  public SQLConnection(String databaseDriver, String databaseUrl, String databaseUser, String databasePassword) {
    try {
      Class.forName(databaseDriver);

      conn = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
    } catch (SQLException | ClassNotFoundException e) {
      LOG.error("Could not establish SQL connection: ", e);
    }
  }

  public Statement createStatement() throws SQLException {
    return conn.createStatement();
  }
}
