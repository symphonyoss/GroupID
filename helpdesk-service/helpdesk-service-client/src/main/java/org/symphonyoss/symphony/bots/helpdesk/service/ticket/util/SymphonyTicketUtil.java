package org.symphonyoss.symphony.bots.helpdesk.service.ticket.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.model.TimePeriod;
import org.symphonyoss.symphony.clients.MessagesClient;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by nick.tarsillo on 11/29/17.
 *
 * Utility class for symphony based ticket functions.
 */
public class SymphonyTicketUtil {
  private static final Logger LOG = LoggerFactory.getLogger(SymphonyTicketUtil.class);

  private SymphonyClient symphonyClient;

  public SymphonyTicketUtil(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
  }

  /**
   * Creates a client transcript of message text from ticket timestamps.
   * @param ticket the ticket
   * @return the client transcript
   */
  public Set<String> getTicketTranscript(Ticket ticket) {
    MessagesClient messagesClient = symphonyClient.getMessagesClient();
    SymStream stream = new SymStream();
    stream.setStreamId(ticket.getClientStreamId());
    try {
      Set<String> symMessages = new LinkedHashSet<>();
      for (TimePeriod timePeriod : ticket.getTranscript()) {
        List<SymMessage> messages =
            messagesClient.getMessagesFromStream(stream, timePeriod.getStartTimestamp(), 0, 100)
                .stream().filter((message) -> Long.parseLong(message.getTimestamp())
                <= timePeriod.getEndTimestamp() && !message.getFromUserId()
                .equals(symphonyClient.getLocalUser().getId()))
                .collect(Collectors.toList());

        Collections.reverse(messages);
        for (SymMessage symMessage: messages) {
          symMessages.add(symMessage.getMessageText());
        }
      }

      return symMessages;
    } catch (MessagesException e) {
      LOG.error("Failed to search for messages: " + e);
    }

    return null;
  }

  public String getTicketQuestion(Ticket ticket) {
    MessagesClient messagesClient = symphonyClient.getMessagesClient();
    SymStream stream = new SymStream();
    stream.setStreamId(ticket.getClientStreamId());
    try {
      TimePeriod latest = null;
      for (TimePeriod timePeriod : ticket.getTranscript()) {
        if(latest == null) {
          latest = timePeriod;
        } else if(latest.getStartTimestamp() < timePeriod.getStartTimestamp()) {
          latest = timePeriod;
        }
      }

      List<SymMessage> symMessages =
          messagesClient.getMessagesFromStream(stream, latest.getStartTimestamp(), 0, 10);
      for(SymMessage symMessage: symMessages) {
        if(symMessage.getTimestamp().equals(latest.getStartTimestamp().toString())) {
          return symMessage.getMessageText();
        }
      }
    } catch (MessagesException e) {
      e.printStackTrace();
    }

    return null;
  }
}
