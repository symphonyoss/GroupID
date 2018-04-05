package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.check;

import static org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient
    .TicketStateType.UNRESOLVED;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.AttachmentsException;
import org.symphonyoss.symphony.authenticator.model.Token;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.message.ActionMessageBuilder;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.message.MakerCheckerMessageBuilder;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.AttachmentMakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client.MakercheckerClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Makerchecker;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.model.UserInfo;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.client.SymphonyClientUtil;
import org.symphonyoss.symphony.bots.utility.validation.SymphonyValidationUtil;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;

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
  private static final String MESSAGE_TO_APPROVE_MAKER_CHECKER =
      "%s approved %s attachment. It has been delivered to the client(s).";
  private static final String MESSAGE_TO_DENY_MAKER_CHECKER =
      "%s denied %s attachment. It has not been delivered to the client(s).";

  private final String ATTACHMENT = "ATTACHMENT";

  private final String botHost;

  private final String serviceHost;

  private final String groupId;

  private final TicketClient ticketClient;

  private final SymphonyClient symphonyClient;

  private final SymphonyValidationUtil symphonyValidationUtil;

  private final SymphonyClientUtil symphonyClientUtil;

  public AgentExternalCheck(String botHost, String serviceHost, String groupId,
      TicketClient ticketClient, SymphonyClient symphonyClient,
      SymphonyValidationUtil symphonyValidationUtil) {
    this.botHost = botHost;
    this.serviceHost = serviceHost;
    this.groupId = groupId;
    this.ticketClient = ticketClient;
    this.symphonyClient = symphonyClient;
    this.symphonyValidationUtil = symphonyValidationUtil;
    this.symphonyClientUtil = new SymphonyClientUtil(this.symphonyClient);
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
    Ticket ticket =
        ticketClient.getTicketByServiceStreamId(symphonyClientUtil.getAuthToken(), message.getStreamId());
    return ticket != null && UNRESOLVED.getState().equals(ticket.getState());
  }

  private boolean hasAttachmentsInMessage(SymMessage message) {
    return message.getAttachments() != null && !message.getAttachments().isEmpty();
  }

  @Override
  public Set<SymMessage> buildSymCheckerMessages(SymMessage symMessage, Set<String> proxyToIds) {
    Set<SymMessage> symCheckerMessages = new HashSet<>();

    String streamId = Base64.encodeBase64String(Base64.decodeBase64(symMessage.getStreamId()));
    Long makerId = symMessage.getFromUserId();
    Long timestamp = Long.valueOf(symMessage.getTimestamp());
    String messageId = symMessage.getId();


    for (SymAttachmentInfo attachmentInfo : symMessage.getAttachments()) {
      MakerCheckerMessageBuilder makerCheckerMessageBuilder = new MakerCheckerMessageBuilder();
      String makerCheckerId =
          RandomStringUtils.randomAlphanumeric(MAKERCHECKER_ID_LENGTH).toUpperCase();
      makerCheckerMessageBuilder.makerCheckerId(makerCheckerId);
      makerCheckerMessageBuilder.botHost(botHost);
      makerCheckerMessageBuilder.serviceHost(serviceHost);
      makerCheckerMessageBuilder.makerId(makerId);
      makerCheckerMessageBuilder.streamId(streamId);
      makerCheckerMessageBuilder.timestamp(timestamp);
      makerCheckerMessageBuilder.messageId(messageId);
      makerCheckerMessageBuilder.groupId(groupId);
      makerCheckerMessageBuilder.attachmentId(attachmentInfo.getId());

      proxyToIds.stream().forEach(makerCheckerMessageBuilder::addProxyToStreamId);

      SymMessage checkerMessage = makerCheckerMessageBuilder.build();
      checkerMessage.setId(makerCheckerId);
      checkerMessage.setStreamId(symMessage.getStreamId());
      checkerMessage.setFromUserId(makerId);
      checkerMessage.setStream(symMessage.getStream());
      checkerMessage.setTimestamp(String.valueOf(timestamp));

      SymAttachmentInfo attachment = new SymAttachmentInfo();
      attachment.setId(attachmentInfo.getId());
      attachment.setName(attachmentInfo.getName());

      checkerMessage.setAttachments(Arrays.asList(attachment));

      symCheckerMessages.add(checkerMessage);
    }

    return symCheckerMessages;
  }

  public Optional<SymAttachmentInfo> getApprovedAttachment(MakerCheckerMessage makerCheckerMessage,
      SymMessage symMessage) {
    AttachmentMakerCheckerMessage checkerMessage =
        (AttachmentMakerCheckerMessage) makerCheckerMessage;

    return symMessage.getAttachments()
        .stream()
        .filter(attachmentInfo -> attachmentInfo.getId().equals(checkerMessage.getAttachmentId()))
        .findFirst();
  }

  @Override
  public SymMessage getActionMessage(Makerchecker makerchecker,
      MakercheckerClient.AttachmentStateType attachmentState) {
    ActionMessageBuilder actionMessageBuilder = new ActionMessageBuilder();
    actionMessageBuilder.makerCheckerId(makerchecker.getId());
    actionMessageBuilder.state(attachmentState.getState());

    UserInfo checker = getUser(makerchecker.getChecker().getUserId());
    actionMessageBuilder.checker(checker);
    if (attachmentState.getState()
        .equals(MakercheckerClient.AttachmentStateType.APPROVED.getState())) {
      actionMessageBuilder.messageToAgents(
          getMessageApproved(checker.getDisplayName(), makerchecker.getAttachmentName()));
    } else if (attachmentState.getState()
        .equals(MakercheckerClient.AttachmentStateType.DENIED.getState())) {
      actionMessageBuilder.messageToAgents(
          getMessageDenied(checker.getDisplayName(), makerchecker.getAttachmentName()));
    }

    SymMessage actionMessage = actionMessageBuilder.build();
    actionMessage.setId(makerchecker.getId());
    actionMessage.setStreamId(makerchecker.getStreamId());
    actionMessage.setFromUserId(makerchecker.getMakerId());
    SymStream symStream = new SymStream();
    symStream.setStreamId(makerchecker.getStreamId());
    actionMessage.setStream(symStream);
    actionMessage.setTimestamp(String.valueOf(makerchecker.getTimeStamp()));

    return actionMessage;
  }

  private String getMessageApproved(String displayName, String attachmentName) {
    return String.format(MESSAGE_TO_APPROVE_MAKER_CHECKER, displayName, attachmentName);
  }

  private String getMessageDenied(String displayName, String attachmentName) {
    return String.format(MESSAGE_TO_DENY_MAKER_CHECKER, displayName, attachmentName);
  }

  private UserInfo getUser(Long userId) {
    UserInfo user = new UserInfo();

    SymUser symUser = symphonyValidationUtil.validateUserId(userId);
    user.setDisplayName(symUser.getDisplayName());
    user.setUserId(symUser.getId());

    return user;
  }

  @Override
  public boolean isCheckerType(MakerCheckerMessage makerCheckerMessage) {
    return StringUtils.isNotBlank(makerCheckerMessage.getType()) &&
        makerCheckerMessage.getType().equals(ATTACHMENT);
  }

  @Override
  public Set<SymMessage> makeApprovedMessages(MakerCheckerMessage makerCheckerMessage,
      SymMessage symMessage) {
    Set<SymMessage> symApprovedMessages = new HashSet<>();

    for (String streamId : makerCheckerMessage.getProxyToStreamIds()) {
      SymMessage approvedMessage = new SymMessage();

      SymStream stream = new SymStream();
      stream.setStreamId(streamId);
      approvedMessage.setStreamId(streamId);

      approvedMessage.setStream(stream);
      approvedMessage.setMessage(symMessage.getMessage());
      approvedMessage.setEntityData(symMessage.getEntityData());
      approvedMessage.setTimestamp(symMessage.getTimestamp());
      approvedMessage.setFromUserId(symMessage.getFromUserId());

      Optional<SymAttachmentInfo> symApprovedAttachmentInfo =
          getApprovedAttachment(makerCheckerMessage, symMessage);
      if (symApprovedAttachmentInfo.isPresent()) {
        SymAttachmentInfo symAttachmentInfo = symApprovedAttachmentInfo.get();

        List<SymAttachmentInfo> attachmentInfoList = new ArrayList<>();
        attachmentInfoList.add(symAttachmentInfo);
        approvedMessage.setAttachments(attachmentInfoList);

        File file = symphonyClientUtil.getFileAttachment(symAttachmentInfo, symMessage);
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

}
