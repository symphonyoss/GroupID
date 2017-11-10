package org.symphonyoss.symphony.bots.helpdesk.service.model.sql.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.symphonyoss.symphony.bots.helpdesk.service.common.ServiceConstants;
import org.symphonyoss.symphony.bots.helpdesk.service.config.HelpDeskServiceConfig;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.model.TicketDao;
import org.symphonyoss.symphony.bots.helpdesk.service.model.TicketResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.model.TicketSearchResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.model.health.HealthCheckFailedException;
import org.symphonyoss.symphony.bots.helpdesk.service.model.sql.SQLConnection;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ws.rs.InternalServerErrorException;

/**
 * Created by nick.tarsillo on 9/25/17.
 */
@Service
public class TicketSQLService implements TicketDao {
  private static final Logger LOG = LoggerFactory.getLogger(TicketSQLService.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private SQLConnection sqlConnection;

  private String tableName;

  @Autowired
  public TicketSQLService(HelpDeskServiceConfig helpDeskServiceConfig) {
    this.tableName = helpDeskServiceConfig.getTicketTableName();
  }

  @PostConstruct
  private void init() {
    createTable();
  }

  /**
   * Create a sql table for membership if does not exist.
   */
  private void createTable() {
    Statement statement = null;
    try {
      statement = sqlConnection.createStatement();
      String sql = "create table if not exists " + tableName + "(id VARCHAR(20), groupId VARCHAR(40), "
          + "serviceStreamId VARCHAR(40), clientStreamId VARCHAR(40), ticket LONGBLOB)";
      LOG.info("Executing: " + sql);
      statement.execute(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Inserts a new ticket into SQL table.
   * @param ticket the ticket to insert.
   * @return success response with created ticket.
   */
  @Override
  public TicketResponse createTicket(Ticket ticket) {
    Statement statement = null;
    try {
      statement = sqlConnection.createStatement();
      if (StringUtils.isBlank(ticket.getId())) {
        ticket.setId(RandomStringUtils.randomAlphanumeric(20));
      }
      String sql = "insert into " + tableName + " (id,groupId,serviceStreamId,ticket) values(\""
          + ticket.getId() + "\", \"" + ticket.getGroupId() + "\", \"" + ticket.getServiceStreamId() + "\",\""
          + objectMapper.writeValueAsString(ticket) + "\")";
      LOG.info("Executing: " + sql);
      statement.executeUpdate(sql);
    } catch (SQLException | IOException e) {
      LOG.error("Create ticket failed: ", e);
      throw new InternalServerErrorException();
    } finally {
      try {statement.close();} catch (SQLException e) {}
    }

    TicketResponse ticketResponse = new TicketResponse();
    ticketResponse.setMessage(ServiceConstants.CREATE_TICKET_RESPONSE);
    ticketResponse.setTicket(ticket);
    return ticketResponse;
  }

  /**
   * Deletes a ticket from SQL table.
   * @param id the ticket id to delete.
   * @return success response with deleted ticket.
   */
  @Override
  public Ticket deleteTicket(String id) {
    Ticket ticket = getTicket(id).getTicket();

    Statement statement = null;
    try {
      statement = sqlConnection.createStatement();
      String sql = "delete from " + tableName + " where id=\"" + id + "\"";
      LOG.info("Executing: " + sql);
      statement.executeUpdate(sql);

      statement.close();
    } catch (SQLException e) {
      LOG.error("Delete ticket failed: ", e);
      throw new InternalServerErrorException();
    } finally {
      try {statement.close();} catch (SQLException e) {}
    }

    return ticket;
  }

  /**
   * Gets a ticket from SQL table.
   * @param id the ticket id to get.
   * @return success response with retrieved ticket.
   */
  @Override
  public TicketResponse getTicket(String id) {
    Ticket ticket = null;

    Statement statement = null;
    try {
      statement = sqlConnection.createStatement();

      String sql = "select ticket from " + tableName + " where id=\"" + id + "\"";
      LOG.info("Executing: " + sql);
      ResultSet resultSet = statement.executeQuery(sql);
      while (resultSet.next()) {
        LOG.info("Got ticket: " + resultSet.getString("ticket"));
        ticket = objectMapper.readValue(resultSet.getString("ticket"), Ticket.class);
      }
      resultSet.close();
      statement.close();
    } catch (IOException e) {
      ticket = null;
    } catch (SQLException e) {
      LOG.error("Get ticket failed: ", e);
      throw new InternalServerErrorException();
    } finally {
      try {statement.close();} catch (SQLException e) {}
    }

    TicketResponse ticketResponse = new TicketResponse();
    ticketResponse.setMessage(ServiceConstants.GET_TICKET_RESPONSE);
    ticketResponse.setTicket(ticket);
    return ticketResponse;
  }

  /**
   * Searches for a ticket in SQL table.
   * @param id
   * @param groupId
   * @param serviceStreamId
   * @return
   */
  @Override
  public TicketSearchResponse searchTicket(String id, String groupId, String serviceStreamId, String clientStreamId) {
    List<Ticket> ticketList = new ArrayList<>();

    Statement statement = null;
    try {
      statement = sqlConnection.createStatement();

      String sql = "select ticket from " + tableName + " where id=\"" + id + "\" or groupId=\"" + groupId
          + "\" or serviceStreamId=\"" + serviceStreamId + "\" or clientStreamId=\"" + clientStreamId + "\"";
      LOG.info("Executing: " + sql);
      ResultSet resultSet = statement.executeQuery(sql);
      while (resultSet.next()) {
        ticketList.add(objectMapper.readValue(resultSet.getString("ticket"), Ticket.class));
      }
      resultSet.close();
      statement.close();
    } catch (SQLException | IOException e) {
      LOG.error("Get ticket failed: ", e);
      throw new InternalServerErrorException();
    } finally {
      try {statement.close();} catch (SQLException e) {}
    }

    TicketSearchResponse ticketResponse = new TicketSearchResponse();
    ticketResponse.setMessage(ServiceConstants.SEARCH_TICKET_RESPONSE);
    ticketResponse.setTicketList(ticketList);
    return ticketResponse;
  }

  /**
   * Update a ticket in SQL table.
   * @param ticket the ticket data to update with.
   * @return success response with updated ticket.
   */
  @Override
  public TicketResponse updateTicket(String id, Ticket ticket) {
    Statement statement = null;
    try {
      statement = sqlConnection.createStatement();
      String sql = "update " + tableName + " set groupId=\"" + ticket.getGroupId()
          + "\", set serviceStreamId=\"" + ticket.getServiceStreamId() + "\", set ticket=\""
          + objectMapper.writeValueAsString(ticket) + "\" where id=\"" + id + "\"";
      LOG.info("Executing: " + sql);
      statement.executeUpdate(sql);
    } catch (SQLException | IOException e) {
      LOG.error("Create ticket failed: ", e);
      throw new InternalServerErrorException();
    } finally {
      try {statement.close();} catch (SQLException e) {}
    }

    TicketResponse ticketResponse = new TicketResponse();
    ticketResponse.setMessage(ServiceConstants.UPDATE_TICKET_RESPONSE);
    ticketResponse.setTicket(ticket);
    return ticketResponse;
  }

  @Override
  public void healthcheck() throws HealthCheckFailedException {
    Statement statement = null;
    try {
      statement = sqlConnection.createStatement();
      String sql = "select * from " + tableName;
      statement.executeQuery(sql);
    } catch (SQLException e) {
      throw new HealthCheckFailedException(e.getMessage());
    }
  }
}
