package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.check;

import static org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient
    .TicketStateType.UNRESOLVED;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.AttachmentsException;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.message.MakerCheckerMessageBuilder;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.AttachmentMakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.swing.text.html.Option;
import javax.ws.rs.BadRequestException;

/**
 * Created by nick.tarsillo on 10/20/17.
 */
public class AgentExternalCheck implements Checker {

  private static final int MAKERCHECKER_ID_LENGTH = 10;
  private static final String MESSAGE_COULD_NOT_CREATE_TEMP_FILE = "Couldn't create a temp file.";
  private static final String MESSAGE_ATTACHMENT_NOT_FOUND = "Attachment not found.";
  private static final String MESSAGE_FAILED_TO_CREATE_FILE = "Failed to create File";

  private final String ATTACHMENT = "ATTACHMENT";

  private final String botHost;

  private final String serviceHost;

  private final String groupId;

  private final TicketClient ticketClient;

  private final SymphonyClient symphonyClient;

  public AgentExternalCheck(String botHost, String serviceHost, String groupId,
      TicketClient ticketClient, SymphonyClient symphonyClient) {
    this.botHost = botHost;
    this.serviceHost = serviceHost;
    this.groupId = groupId;
    this.ticketClient = ticketClient;
    this.symphonyClient = symphonyClient;
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
      messageBuilder.makerCheckerId(makerCheckerId);
      messageBuilder.botHost(botHost);
      messageBuilder.serviceHost(serviceHost);
      messageBuilder.makerId(makerId);
      messageBuilder.streamId(streamId);
      messageBuilder.timestamp(timestamp);
      messageBuilder.messageId(messageId);
      messageBuilder.groupId(groupId);
      messageBuilder.attachmentId(attachmentInfo.getId());

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
  public Optional<SymAttachmentInfo> getApprovedAttachment(MakerCheckerMessage makerCheckerMessage,
      SymMessage symMessage) {
    AttachmentMakerCheckerMessage checkerMessage = (AttachmentMakerCheckerMessage) makerCheckerMessage;

    return symMessage.getAttachments()
        .stream()
        .filter(attachmentInfo -> attachmentInfo.getId().equals(checkerMessage.getAttachmentId()))
        .findFirst();
  }

  @Override
  public boolean isCheckerType(MakerCheckerMessage makerCheckerMessage) {
    return StringUtils.isNotBlank(makerCheckerMessage.getType()) &&
        makerCheckerMessage.getType().equals(ATTACHMENT);
  }

  @Override
  public Set<SymMessage> makeApprovedMessages(MakerCheckerMessage makerCheckerMessage, SymMessage symMessage) {
    Set<SymMessage> symApprovedMessages = new HashSet<>();

    for(String streamId: makerCheckerMessage.getProxyToStreamIds()) {
      SymMessage approvedMessage = new SymMessage();

      SymStream stream = new SymStream();
      stream.setStreamId(streamId);
      approvedMessage.setStreamId(streamId);

      approvedMessage.setStream(stream);
      approvedMessage.setMessage(symMessage.getMessage());
      approvedMessage.setEntityData(symMessage.getEntityData());
      approvedMessage.setTimestamp(symMessage.getTimestamp());
      approvedMessage.setFromUserId(symMessage.getFromUserId());

      Optional<SymAttachmentInfo> symApprovedAttachmentInfo = getApprovedAttachment(makerCheckerMessage, symMessage);
      if (symApprovedAttachmentInfo.isPresent()) {
        SymAttachmentInfo symAttachmentInfo = symApprovedAttachmentInfo.get();

        List<SymAttachmentInfo> attachmentInfoList = new ArrayList<>();
        attachmentInfoList.add(symAttachmentInfo);
        approvedMessage.setAttachments(attachmentInfoList);

        File file = getFileAttachment(symAttachmentInfo, symMessage);
        approvedMessage.setAttachment(file);
      }

      symApprovedMessages.add(approvedMessage);
    }

    return symApprovedMessages;
  }

  private File getFileAttachment(SymAttachmentInfo symAttachmentInfo, SymMessage symMessage) {
    File tempFile;
    try {
      String prefix = symAttachmentInfo.getName().split("\\.")[0];
      String suffix = "." + symAttachmentInfo.getName().split("\\.")[1];
      tempFile = File.createTempFile(prefix, suffix);
    } catch (IOException e) {
      throw new BadRequestException(MESSAGE_COULD_NOT_CREATE_TEMP_FILE);
    }

    byte[] aByte;
    try {
      aByte = symphonyClient.getAttachmentsClient().getAttachmentData(symAttachmentInfo, symMessage);
    } catch (AttachmentsException e) {
      throw new BadRequestException(MESSAGE_ATTACHMENT_NOT_FOUND);
    }

    InputStream inputStream = new ByteArrayInputStream(aByte);
    try {
      FileUtils.copyInputStreamToFile(inputStream, tempFile);
    } catch (IOException e) {
      throw new BadRequestException(MESSAGE_FAILED_TO_CREATE_FILE);
    }

    return tempFile;
  }


}
