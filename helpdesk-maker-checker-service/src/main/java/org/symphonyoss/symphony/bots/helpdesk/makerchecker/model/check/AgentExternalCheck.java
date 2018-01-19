package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.check;

import static org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient
    .TicketStateType.UNRESOLVED;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.AttachmentsException;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.message.ActionMessageBuilder;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.message.MakerCheckerMessageBuilder;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.AttachmentMakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Makerchecker;
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

import javax.ws.rs.BadRequestException;

/**
 * Created by nick.tarsillo on 10/20/17.
 */
public class AgentExternalCheck implements Checker {

  private static final int MAKERCHECKER_ID_LENGTH = 10;
  private static final String MESSAGE_COULD_NOT_CREATE_FILE = "Couldn't create a file.";
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
      MakerCheckerMessageBuilder makerCheckerMessageBuilder = new MakerCheckerMessageBuilder();
      String makerCheckerId = RandomStringUtils.randomAlphanumeric(MAKERCHECKER_ID_LENGTH).toUpperCase();
      makerCheckerMessageBuilder.makerCheckerId(makerCheckerId);
      makerCheckerMessageBuilder.botHost(botHost);
      makerCheckerMessageBuilder.serviceHost(serviceHost);
      makerCheckerMessageBuilder.makerId(makerId);
      makerCheckerMessageBuilder.streamId(streamId);
      makerCheckerMessageBuilder.timestamp(timestamp);
      makerCheckerMessageBuilder.messageId(messageId);
      makerCheckerMessageBuilder.groupId(groupId);
      makerCheckerMessageBuilder.attachmentId(attachmentInfo.getId());

      proxyToIds.stream().forEach(id -> makerCheckerMessageBuilder.addProxyToStreamId(id));

      SymMessage checkerMessage = makerCheckerMessageBuilder.build();
      checkerMessage.setId(makerCheckerId);
      checkerMessage.setStreamId(symMessage.getStreamId());
      checkerMessage.setFromUserId(makerId);
      checkerMessage.setStream(symMessage.getStream());
      checkerMessage.setTimestamp(String.valueOf(timestamp));

      SymAttachmentInfo attachment = new SymAttachmentInfo();
      attachment.setId(attachmentInfo.getId());

      checkerMessage.setAttachments(Arrays.asList(attachment));

      symCheckerMessages.add(checkerMessage);
    }

    return symCheckerMessages;
  }

  public Optional<SymAttachmentInfo> getApprovedAttachment(MakerCheckerMessage makerCheckerMessage,
      SymMessage symMessage) {
    AttachmentMakerCheckerMessage checkerMessage = (AttachmentMakerCheckerMessage) makerCheckerMessage;

    return symMessage.getAttachments()
        .stream()
        .filter(attachmentInfo -> attachmentInfo.getId().equals(checkerMessage.getAttachmentId()))
        .findFirst();
  }

  @Override
  public SymMessage getActionMessage(Makerchecker makerchecker) {
    ActionMessageBuilder actionMessageBuilder = new ActionMessageBuilder();
    actionMessageBuilder.makerCheckerId(makerchecker.getId());
    actionMessageBuilder.state(makerchecker.getState());
    actionMessageBuilder.userId(makerchecker.getMakerId());

    SymMessage actionMessage = actionMessageBuilder.build();
    actionMessage.setId(makerchecker.getId());
    actionMessage.setStreamId(makerchecker.getStreamId());
    actionMessage.setFromUserId(makerchecker.getMakerId());
    actionMessage.setStreamId(makerchecker.getStreamId());
    SymStream symStream = new SymStream();
    symStream.setStreamId(makerchecker.getStreamId());
    actionMessage.setStream(symStream);
    actionMessage.setTimestamp(String.valueOf(makerchecker.getTimeStamp()));

    return actionMessage;
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

  @Override
  public void afterSendApprovedMessage(SymMessage symMessage) {
    String tmpDir = System.getProperty("java.io.tmpdir");

    for (SymAttachmentInfo symAttachmentInfo : symMessage.getAttachments()) {
      File directory = new File(tmpDir + File.separator + symAttachmentInfo.getId());

      File[] files = directory.listFiles();
      Arrays.stream(files).forEach(file -> file.delete());

      directory.delete();
    }
  }

  private File getFileAttachment(SymAttachmentInfo symAttachmentInfo, SymMessage symMessage) {
    String tmpDir = System.getProperty("java.io.tmpdir");
    File directory = new File(tmpDir + File.separator + symAttachmentInfo.getId());
    if (!directory.exists()) {
      directory.mkdir();
    }

    File file = new File(directory + File.separator + symAttachmentInfo.getName());
    try {
      file.createNewFile();
    } catch (IOException e) {
      throw new BadRequestException(MESSAGE_COULD_NOT_CREATE_FILE);
    }

    byte[] aByte;
    try {
      aByte = symphonyClient.getAttachmentsClient().getAttachmentData(symAttachmentInfo, symMessage);
    } catch (AttachmentsException e) {
      throw new BadRequestException(MESSAGE_ATTACHMENT_NOT_FOUND);
    }

    InputStream inputStream = new ByteArrayInputStream(aByte);
    try {
      FileUtils.copyInputStreamToFile(inputStream, file);
    } catch (IOException e) {
      throw new BadRequestException(MESSAGE_FAILED_TO_CREATE_FILE);
    }

    return file;
  }

}
