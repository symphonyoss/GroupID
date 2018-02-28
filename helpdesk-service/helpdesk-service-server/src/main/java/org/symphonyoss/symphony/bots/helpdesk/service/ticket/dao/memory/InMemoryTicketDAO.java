package org.symphonyoss.symphony.bots.helpdesk.service.ticket.dao.memory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.dao.TicketDao;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.dao.mongo.MongoTicketDAO;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.exception.TicketNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DAO component responsible for managing ticket objects in-memory. This component will be
 * created only if the {@link MongoTicketDAO} component wasn't created previously.
 * <p>
 * This class should be used only for tests purpose.
 * <p>
 * Created by rsanchez on 22/11/17.
 */
@Component
@ConditionalOnMissingBean(MongoTicketDAO.class)
public class InMemoryTicketDAO implements TicketDao {

  private final Map<String, Ticket> database = new HashMap<>();

  @Override
  public Ticket createTicket(Ticket ticket) {
    this.database.put(ticket.getId(), ticket);
    return ticket;
  }

  @Override
  public void deleteTicket(String id) {
    this.database.remove(id);
  }

  @Override
  public Ticket getTicket(String id) {
    return this.database.get(id);
  }

  @Override
  public List<Ticket> searchTicket(String groupId, String serviceStreamId, String clientStreamId) {
    Stream<Ticket> ticketStream =
        this.database.values().stream().filter((ticket) -> groupId.equals(ticket.getGroupId()));

    if (serviceStreamId != null) {
      ticketStream = ticketStream.filter((ticket) -> serviceStreamId.equals(ticket.getServiceStreamId()));
    }

    if (clientStreamId != null) {
      ticketStream = ticketStream.filter((ticket) -> clientStreamId.equals(ticket.getClientStreamId()));
    }

    return ticketStream.collect(Collectors.toList());
  }

  @Override
  public Ticket updateTicket(String id, Ticket ticket) {
    Ticket saved = getTicket(id);

    if (saved == null) {
      throw new TicketNotFoundException(id);
    }

    ticket.setId(id);
    return createTicket(ticket);
  }

}
