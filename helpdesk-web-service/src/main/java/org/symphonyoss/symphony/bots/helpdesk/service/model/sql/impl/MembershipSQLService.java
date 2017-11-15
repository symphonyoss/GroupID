package org.symphonyoss.symphony.bots.helpdesk.service.model.sql.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.symphonyoss.symphony.bots.helpdesk.service.common.ServiceConstants;
import org.symphonyoss.symphony.bots.helpdesk.service.config.HelpDeskServiceConfig;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.model.MembershipDao;
import org.symphonyoss.symphony.bots.helpdesk.service.model.MembershipResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.model.health.HealthCheckFailedException;
import org.symphonyoss.symphony.bots.helpdesk.service.model.sql.SQLConnection;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.PostConstruct;
import javax.ws.rs.InternalServerErrorException;

/**
 * Created by nick.tarsillo on 9/25/17.
 */
@Service
public class MembershipSQLService implements MembershipDao {
  private static final Logger LOG = LoggerFactory.getLogger(MembershipSQLService.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private SQLConnection sqlConnection;

  private String tableName;

  @Autowired
  public MembershipSQLService(HelpDeskServiceConfig helpDeskServiceConfig) {
    this.tableName = helpDeskServiceConfig.getMembershipTableName();
  }

  @PostConstruct
  private void init() {
    createTable();
  }

  /**
   * Creates a table for memberships in SQL, if it does not already exist.
   */
  private void createTable() {
    Statement statement = null;
    try {
      statement = sqlConnection.createStatement();
      String sql = "create table if not exists " + tableName + "(id VARCHAR(20), groupId VARCHAR(40), membership LONGBLOB)";
      LOG.info("Executing: " + sql);
      statement.execute(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Create a new membership in SQL table.
   * @param membership the membership to create.
   */
  @Override
  public MembershipResponse createMembership(Membership membership) {
    Statement statement = null;
    try {
      statement = sqlConnection.createStatement();
      if (StringUtils.isBlank(membership.getId())) {
        membership.setId(RandomStringUtils.randomAlphanumeric(20));
      }
      String sql = "insert into " + tableName + " (id,groupId,membership) values(\"" + membership.getId()
          + "\", \"" + membership.getGroupId() + "\", \"" + objectMapper.writeValueAsString(membership).replaceAll("\"","\'")
          + "\")";
      LOG.info("Executing: " + sql);
      statement.executeUpdate(sql);
    } catch (SQLException | IOException e) {
      LOG.error("Create membership failed: ", e);
      throw new InternalServerErrorException();
    } finally {
      try {statement.close();} catch (SQLException e) {}
    }

    MembershipResponse membershipResponse = new MembershipResponse();
    membershipResponse.setMessage(ServiceConstants.CREATE_MEMBERSHIP_RESPONSE);
    membershipResponse.setMembership(membership);
    return membershipResponse;
  }

  /**
   * Deletes a membership in SQL table.
   * @param id the ID of the membership.
   * @param groupId the groupId the membership belonged to.
   * @return the membership that was deleted.
   */
  @Override
  public Membership deleteMembership(String id, String groupId) {
    Membership membership = getMembership(id, groupId).getMembership();

    Statement statement = null;
    try {
      statement = sqlConnection.createStatement();
      String sql = "delete from " + tableName + "where id=\"" + id + "\" and groupId=" + groupId;
      LOG.info("Executing: " + sql);
      statement.executeUpdate(sql);

      statement.close();
    } catch (SQLException e) {
      LOG.error("Delete membership failed: ", e);
      throw new InternalServerErrorException();
    } finally {
      try {statement.close();} catch (SQLException e) {}
    }

    return membership;
  }

  /**
   * Get a membership in SQL table.
   * @param id the id of the membership to get.
   * @param groupId the group id the membership belongs to.
   */
  @Override
  public MembershipResponse getMembership(String id, String groupId) {
    Membership membership = null;

    Statement statement = null;
    try {
      statement = sqlConnection.createStatement();

      String sql = "select membership from " + tableName + " where id=\"" + id + "\" and groupId=\""
          + groupId + "\"";

      LOG.info("Executing: " + sql);
      ResultSet resultSet = statement.executeQuery(sql);
      while (resultSet.next()) {
        LOG.info("Got membership: " + resultSet.getString("membership"));
        membership = objectMapper.readValue(resultSet.getString("membership").replaceAll("\'", "\""), Membership.class);
      }
      resultSet.close();
      statement.close();
    } catch (IOException e) {
      membership = null;
    } catch (SQLException e) {
      LOG.error("Get membership failed: ", e);
      throw new InternalServerErrorException();
    } finally {
      try {statement.close();} catch (SQLException e) {}
    }

    MembershipResponse membershipResponse = new MembershipResponse();
    membershipResponse.setMessage(ServiceConstants.GET_MEMBERSHIP_RESPONSE);
    membershipResponse.setMembership(membership);
    return membershipResponse;
  }

  /**
   * Updates a membership in SQL table.
   * @param id the id of the membership to update.
   * @param groupId the group id the membership belongs to.
   * @param membership the membership to update.
   */
  @Override
  public MembershipResponse updateMembership(String id, String groupId, Membership membership) {
    Statement statement = null;
    try {
      statement = sqlConnection.createStatement();
      String sql = "update " + tableName + " set membership=\""
          + objectMapper.writeValueAsString(membership).replaceAll("\"","\'") +
          "\" where id=\"" + id + "\" and groupId=\"" + groupId + "\"";
      LOG.info("Executing: " + sql);
      statement.executeUpdate(sql);
    } catch (SQLException | IOException e) {
      LOG.error("Create membership failed: ", e);
      throw new InternalServerErrorException();
    } finally {
      try {statement.close();} catch (SQLException e) {}
    }

    MembershipResponse membershipResponse = new MembershipResponse();
    membershipResponse.setMessage(ServiceConstants.UPDATE_MEMBERSHIP_RESPONSE);
    membershipResponse.setMembership(membership);
    return membershipResponse;
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
