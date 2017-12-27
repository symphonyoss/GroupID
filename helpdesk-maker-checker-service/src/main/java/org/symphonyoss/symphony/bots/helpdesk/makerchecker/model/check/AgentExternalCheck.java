package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.check;

import static org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient
    .TicketStateType.UNRESOLVED;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.message.MakerCheckerMessageBuilder;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.AttachmentMakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nick.tarsillo on 10/20/17.
 */
public class AgentExternalCheck implements Checker {

  private static final int MAKERCHECKER_ID_LENGTH = 10;

  private final String ATTACHMENT = "ATTACHMENT";

  private final String botHost;

  private final String serviceHost;

  private final String groupId;

  private final TicketClient ticketClient;

  public AgentExternalCheck(String botHost, String serviceHost, String groupId,
      TicketClient ticketClient) {
    this.botHost = botHost;
    this.serviceHost = serviceHost;
    this.groupId = groupId;
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
  public Set<SymMessage> buildSymCheckerMessages(SymMessage symMessage, Object opaque) {
    Set<SymMessage> symCheckerMessages = new HashSet<>();

    String streamId = Base64.encodeBase64String(Base64.decodeBase64(symMessage.getStreamId()));
    Long makerId = symMessage.getFromUserId();
    Long timestamp = Long.valueOf(symMessage.getTimestamp());
    String messageId = symMessage.getId();
    Set<String> proxyToIds = (Set<String>) opaque;

    for(SymAttachmentInfo attachmentInfo: symMessage.getAttachments()) {
      MakerCheckerMessageBuilder messageBuilder = new MakerCheckerMessageBuilder();
      String makerCheckerId = RandomStringUtils.randomAlphanumeric(MAKERCHECKER_ID_LENGTH).toUpperCase();
      messageBuilder.attachmentId(makerCheckerId);
      messageBuilder.botHost(botHost);
      messageBuilder.serviceHost(serviceHost);
      messageBuilder.makerId(makerId);
      messageBuilder.streamId(streamId);
      messageBuilder.timestamp(timestamp);
      messageBuilder.messageId(messageId);
      messageBuilder.groupId(groupId);

      proxyToIds.stream().forEach(id -> messageBuilder.addProxyToStreamId(id));

      SymMessage checkerMessage = messageBuilder.build();
      checkerMessage.setId(makerCheckerId);
      checkerMessage.setStreamId(symMessage.getStreamId());
      checkerMessage.setFromUserId(makerId);
      checkerMessage.setStream(symMessage.getStream());

      SymAttachmentInfo attachment = new SymAttachmentInfo();
      attachment.setId(attachmentInfo.getId());

      checkerMessage.setAttachments(Arrays.asList(attachment));

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

}
