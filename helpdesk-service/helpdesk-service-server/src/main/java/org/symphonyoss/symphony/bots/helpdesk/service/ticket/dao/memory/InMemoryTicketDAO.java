package org.symphonyoss.symphony.bots.helpdesk.service.ticket.dao.memory;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.bots.helpdesk.service.memory.MemoryCondition;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.dao.TicketDao;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.exception.TicketNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Mongo DAO for ticket.
 *
 * Created by rsanchez on 22/11/17.
 */
@Component
@Conditional(MemoryCondition.class)
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
