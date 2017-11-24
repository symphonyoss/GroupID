package org.symphonyoss.symphony.bots.helpdesk.service.ticket.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.exception.CreateTicketException;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.exception.DeleteTicketException;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.exception.GetTicketException;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.exception.TicketNotFoundException;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.exception.UpdateTicketException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Mongo DAO for ticket.
 *
 * Created by rsanchez on 22/11/17.
 */
@Component
public class MongoTicketDAO implements TicketDao {

  private static final String COLLECTION_NAME = "helpdeskticket";

  private final MongoTemplate mongoTemplate;

  public MongoTicketDAO(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public Ticket createTicket(Ticket ticket) {
    try {
      this.mongoTemplate.insert(ticket, COLLECTION_NAME);
      return ticket;
    } catch (Exception e) {
      throw new CreateTicketException(e);
    }
  }

  @Override
  public void deleteTicket(String id) {
    Ticket ticket = getTicket(id);

    try {
      if (ticket != null) {
        this.mongoTemplate.remove(ticket, COLLECTION_NAME);
      }
    } catch (Exception e) {
      throw new DeleteTicketException(id, e);
    }
  }

  @Override
  public Ticket getTicket(String id) {
    try {
      return this.mongoTemplate.findById(id, Ticket.class, COLLECTION_NAME);
    } catch (Exception e) {
      throw new GetTicketException(id, e);
    }
  }

  @Override
  public List<Ticket> searchTicket(String groupId, String serviceStreamId, String clientStreamId) {
    Criteria groupIdCriteria = where("groupId").is(groupId);
    List<Criteria> criterias = new ArrayList<>();

    if (serviceStreamId != null) {
      Criteria serviceStreamIdCriteria = where("serviceStreamId").is(serviceStreamId);
      criterias.add(serviceStreamIdCriteria);
    }

    if (clientStreamId != null) {
      Criteria clientStreamIdCriteria = where("clientStreamId").is(clientStreamId);
      criterias.add(clientStreamIdCriteria);
    }

    if (!criterias.isEmpty()) {
      Criteria[] criteriaArray = criterias.stream().toArray(Criteria[]::new);
      groupIdCriteria = groupIdCriteria.andOperator(criteriaArray);
    }

    List<Ticket> tickets = mongoTemplate.find(query(groupIdCriteria), Ticket.class, COLLECTION_NAME);
    return tickets;
  }

  @Override
  public Ticket updateTicket(String id, Ticket ticket) {
    Ticket saved = getTicket(id);

    if (saved == null) {
      throw new TicketNotFoundException(id);
    }

    try {
      ticket.setId(id);

      this.mongoTemplate.save(ticket, COLLECTION_NAME);

      return ticket;
    } catch (Exception e) {
      throw new UpdateTicketException(id, e);
    }
  }

}
