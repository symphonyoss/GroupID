package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.check;

import static org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient
    .TicketStateType.UNRESOLVED;

import org.apache.commons.lang3.StringUtils;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.config.MakerCheckerServiceConfig;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.AttachmentMakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerServiceSession;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.template.AttachmentEntityTemplateData;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.template.MessageTemplate;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nick.tarsillo on 10/20/17.
 */
public class AgentExternalCheck implements Checker {

  private final String ATTACHMENT = "ATTACHMENT";

  private MakerCheckerServiceSession session;

  private TicketClient ticketClient;

  public AgentExternalCheck(TicketClient ticketClient) {
    this.ticketClient = ticketClient;
  }

  @Override
  public Set<Object> check(SymMessage message) {
    if (hasOpenTicketInServiceRoom(message) && hasAttachmentsInMessage(message)) {
      Set<Object> flagged = new HashSet<>();
      flagged.add(message.getAttachments());
      flagged.add(message.getEntityData());
      return flagged;
    }

    return null;
  }

  private boolean hasOpenTicketInServiceRoom(SymMessage message) {
    Ticket ticket = ticketClient.getTicketByServiceStreamId(message.getStreamId());
    return ticket != null && UNRESOLVED.getState().equals(ticket.getState());
  }

  private boolean hasAttachmentsInMessage(SymMessage message) {
    return message.getAttachments() != null && !message.getAttachments().isEmpty();
  }

  @Override
  public Set<SymMessage> buildSymCheckerMessages(SymMessage symMessage) {
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
