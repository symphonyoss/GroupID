package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.check;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.config.MakerCheckerServiceConfig;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.AttachmentMakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerServiceSession;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.template
    .AttachmentEntityTemplateData;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.template.MessageTemplate;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStreamAttributes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nick.tarsillo on 10/20/17.
 */
public class AgentExternalCheck implements Checker {
  private static final Logger LOG = LoggerFactory.getLogger(AgentExternalCheck.class);
  private final String ATTACHMENT = "ATTACHMENT";

  private MakerCheckerServiceSession session;
  private TicketClient ticketClient;
  private SymphonyClient symphonyClient;

  public AgentExternalCheck(SymphonyClient symphonyClient, TicketClient ticketClient) {
    this.ticketClient = ticketClient;
    this.symphonyClient = symphonyClient;
  }

  @Override
  public Set<Object> check(SymMessage message) {
    Ticket ticket = ticketClient.getTicketByServiceStreamId(message.getStreamId());
    if(ticket != null) {
      try {
        SymStreamAttributes streamAttributes =
            symphonyClient.getStreamsClient().getStreamAttributes(ticket.getClientStreamId());
        if((message.getAttachments() != null || !message.getAttachments().isEmpty()) &&
            streamAttributes.getCrossPod()) {
          Set<Object> flagged = new HashSet<>();
          flagged.add(message.getAttachments());
          return flagged;
        }
      } catch (StreamsException e) {
        LOG.error("Could not get stream for client stream: ", e);
      }
    }

    return null;
  }

  @Override
  public Set<SymMessage> buildSymCheckerMessages(Set<Object> flaggedData, SymMessage symMessage) {
    Set<SymMessage> symCheckerMessages = new HashSet<>();
    MakerCheckerServiceConfig config = session.getMakerCheckerServiceConfig();
    for(SymAttachmentInfo attachmentInfo: symMessage.getAttachments()) {
      SymMessage checkerMessage = new SymMessage();

      checkerMessage.setMessage(config.getAttachmentMessageTemplate());

      MessageTemplate entityTemplate = new MessageTemplate(config.getAttachmentEntityTemplate());
      checkerMessage.setEntityData(entityTemplate.buildFromData(
          new AttachmentEntityTemplateData(attachmentInfo.getId(), ATTACHMENT)));

      symCheckerMessages.add(checkerMessage);
    }

    if(!flaggedData.contains(symMessage.getMessage())) {
      SymMessage checkerMessage = new SymMessage();
      checkerMessage.setMessage(symMessage.getMessage());
      symCheckerMessages.add(checkerMessage);
    }

    return symCheckerMessages;
  }

  @Override
  public Set<SymMessage> makeApprovedMessages(MakerCheckerMessage makerCheckerMessage,
      SymMessage symMessage) {
    AttachmentMakerCheckerMessage checkerMessage = (AttachmentMakerCheckerMessage) makerCheckerMessage;

    SymMessage acceptMessage = new SymMessage();
    List<SymAttachmentInfo> attachmentInfoSet = new ArrayList<>();
    for(SymAttachmentInfo attachmentInfo: symMessage.getAttachments()) {
      if(attachmentInfo.getId().equals(checkerMessage.getAttachmentId())) {
        attachmentInfoSet.add(attachmentInfo);
        break;
      }
    }
    acceptMessage.setAttachments(attachmentInfoSet);
    acceptMessage.setMessageText("");

    Set<SymMessage> acceptMessages = new HashSet<>();
    acceptMessages.add(acceptMessage);
    return acceptMessages;
  }

  @Override
  public boolean isCheckerType(MakerCheckerMessage makerCheckerMessage) {
    return StringUtils.isNotBlank(makerCheckerMessage.getType()) &&
        makerCheckerMessage.getType().equals(ATTACHMENT);
  }

  @Override
  public void setSession(MakerCheckerServiceSession makerCheckerServiceSession) {
    this.session = makerCheckerServiceSession;
  }
}
