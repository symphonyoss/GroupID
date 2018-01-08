package org.symphonyoss.symphony.bots.helpdesk.bot.api;


import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.symphony.bots.ai.AiResponseIdentifier;
import org.symphonyoss.symphony.bots.ai.HelpDeskAi;
import org.symphonyoss.symphony.bots.ai.impl.AiResponseIdentifierImpl;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.MakerCheckerMessageDetail;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.MakerCheckerResponse;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.TicketResponse;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.User;
import org.symphonyoss.symphony.bots.helpdesk.bot.ticket.AcceptTicketService;
import org.symphonyoss.symphony.bots.helpdesk.bot.ticket.JoinConversationService;
import org.symphonyoss.symphony.bots.helpdesk.bot.util.ValidateMembershipService;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.MakerCheckerService;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.AttachmentMakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client.MakercheckerClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Makerchecker;
import org.symphonyoss.symphony.bots.helpdesk.service.model.UserInfo;
import org.symphonyoss.symphony.bots.utility.validation.SymphonyValidationUtil;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.BadRequestException;

/**
 * Created by nick.tarsillo on 9/25/17.
 */
@RestController
public class V1HelpDeskController extends V1ApiController {

  private static final String MAKER_CHECKER_SUCCESS_RESPONSE = "Maker checker message accepted.";
  private static final String MAKER_CHECKER_DENY_RESPONSE = "Maker checker message denied.";
  private static final String MAKER_CHECKER_NOT_FOUND = "Makerchecker not found.";
  private static final String OPEN_MAKERCHECKER_NOT_FOUND =
      "This action can not be perfomed because this attachment was approved/denied before.";
  private static final String OWN_ATTACHMENT_EXCPETION =
      "You can not perform this action in your own attachment.";

  @Autowired
  private SymphonyValidationUtil symphonyValidationUtil;

  @Autowired
  private MakercheckerClient makercheckerClient;

  @Qualifier("agentMakerCheckerService")
  @Autowired
  private MakerCheckerService agentMakerCheckerService;

  @Autowired
  private HelpDeskAi helpDeskAi;

  @Autowired
  private AcceptTicketService acceptTicketService;

  @Autowired
  private JoinConversationService joinConversationService;

  @Autowired
  private ValidateMembershipService validateMembershipService;

  @Override
  public TicketResponse acceptTicket(String ticketId, Long agentId) {
    return acceptTicketService.execute(ticketId, agentId);
  }

  @Override
  public TicketResponse joinConversation(String ticketId, Long agentId) {
    return joinConversationService.execute(ticketId, agentId);
  }

  /**
   * Accept a maker checker message.
   * @param detail the maker checker message detail
   * @return a maker checker message response
   */
  @Override
  public MakerCheckerResponse approveMakerCheckerMessage(MakerCheckerMessageDetail detail) {
    validateRequiredParameter("streamId", detail.getStreamId(), "body");
    symphonyValidationUtil.validateStream(detail.getStreamId());
    validateRequiredParameter("groupId", detail.getGroupId(), "body");
    validateRequiredParameter("attachmentId", detail.getAttachmentId(), "body");
    validateRequiredParameter("timestamp", detail.getTimeStamp(), "body");
    validateRequiredParameter("messageId", detail.getMessageId(), "body");
    validateRequiredParameter("userId", detail.getUserId(), "body");

    Makerchecker makerchecker = makercheckerClient.getMakerchecker(detail.getMakerCheckerId());
    if (makerchecker == null) {
      throw new BadRequestException(MAKER_CHECKER_NOT_FOUND);
    }

    if (makerchecker.getMakerId().equals(detail.getUserId())) {
      throw new BadRequestException(OWN_ATTACHMENT_EXCPETION);
    }

    try {
      validateMembershipService.updateMembership(detail.getUserId());
    } catch (SymException e) {
      throw new BadRequestException("User is not an agent");
    }

    if (MakercheckerClient.AttachmentStateType.OPENED.getState().equals(makerchecker.getState())) {
      SymUser agentUser = symphonyValidationUtil.validateUserId(detail.getUserId());
      sendApprovedMakerChekerMessage(detail);
      UserInfo checker = getChecker(agentUser);
      makerchecker.setChecker(checker);
      makerchecker.setState(MakercheckerClient.AttachmentStateType.APPROVED.getState());
      makercheckerClient.updateMakerchecker(makerchecker);

      return buildMakerCheckerResponse(agentUser, detail);
    } else {
      throw new BadRequestException(OPEN_MAKERCHECKER_NOT_FOUND);
    }

  }

  private MakerCheckerResponse buildMakerCheckerResponse(SymUser agentUser,
      MakerCheckerMessageDetail detail) {
    MakerCheckerResponse makerCheckerResponse = new MakerCheckerResponse();
    makerCheckerResponse.setMessage(MAKER_CHECKER_SUCCESS_RESPONSE);
    makerCheckerResponse.setMakerCheckerMessageDetail(detail);

    User user = getUser(detail, agentUser);

    makerCheckerResponse.setUser(user);
    makerCheckerResponse.setState(MakercheckerClient.AttachmentStateType.APPROVED.getState());

    return makerCheckerResponse;
  }

  private void sendApprovedMakerChekerMessage(MakerCheckerMessageDetail detail) {
    AttachmentMakerCheckerMessage checkerMessage = new AttachmentMakerCheckerMessage();
    checkerMessage.setAttachmentId(detail.getAttachmentId());
    checkerMessage.setGroupId(detail.getGroupId());
    checkerMessage.setMessageId(detail.getMessageId());
    checkerMessage.setStreamId(Base64.encodeBase64URLSafeString(Base64.decodeBase64(detail.getStreamId())));
    checkerMessage.setProxyToStreamIds(detail.getProxyToStreamIds());
    checkerMessage.setTimeStamp(detail.getTimeStamp());
    checkerMessage.setType(detail.getType());

    AiSessionKey aiSessionKey = helpDeskAi.getSessionKey(detail.getUserId(), detail.getStreamId());

    Set<SymMessage> symMessages = agentMakerCheckerService.getApprovedMakercheckerMessage(checkerMessage);

    for (SymMessage symMessage : symMessages) {
      SymphonyAiMessage symphonyAiMessage = new SymphonyAiMessage(symMessage);

      Set<AiResponseIdentifier> identifiers = new HashSet<>();
      identifiers.add(new AiResponseIdentifierImpl(symMessage.getStreamId()));

      helpDeskAi.sendMessage(symphonyAiMessage, identifiers, aiSessionKey);

      if (symphonyAiMessage.getAttachment() != null) {
        symphonyAiMessage.getAttachment().delete();
      }
    }
  }

  /**
   * Deny a maker checker message.
   * @param detail the maker checker message detail
   * @return a maker checker message response
   */
  @Override
  public MakerCheckerResponse denyMakerCheckerMessage(MakerCheckerMessageDetail detail) {
    symphonyValidationUtil.validateStream(detail.getStreamId());

    Makerchecker makerchecker = makercheckerClient.getMakerchecker(detail.getMakerCheckerId());
    if (makerchecker == null) {
      throw new BadRequestException(MAKER_CHECKER_NOT_FOUND);
    }

    if (makerchecker.getMakerId().equals(detail.getUserId())) {
      throw new BadRequestException(OWN_ATTACHMENT_EXCPETION);
    }

    try {
      validateMembershipService.updateMembership(detail.getUserId());
    } catch (SymException e) {
      throw new BadRequestException("User is not an agent");
    }

    if (MakercheckerClient.AttachmentStateType.OPENED.getState().equals(makerchecker.getState())) {
      SymUser agentUser = symphonyValidationUtil.validateUserId(detail.getUserId());
      UserInfo checker = getChecker(agentUser);

      makerchecker.setChecker(checker);
      makerchecker.setState(MakercheckerClient.AttachmentStateType.DENIED.getState());
      makercheckerClient.updateMakerchecker(makerchecker);

      MakerCheckerResponse makerCheckerResponse = new MakerCheckerResponse();
      makerCheckerResponse.setMessage(MAKER_CHECKER_DENY_RESPONSE);
      makerCheckerResponse.setMakerCheckerMessageDetail(detail);

      User user = getUser(detail, agentUser);

      makerCheckerResponse.setUser(user);
      makerCheckerResponse.setState(MakercheckerClient.AttachmentStateType.DENIED.getState());

      return makerCheckerResponse;
    } else {
      throw new BadRequestException(OPEN_MAKERCHECKER_NOT_FOUND);
    }
  }

  private User getUser(MakerCheckerMessageDetail detail, SymUser agentUser) {
    User user = new User();
    user.setDisplayName(agentUser.getDisplayName());
    user.setUserId(detail.getUserId());
    return user;
  }

  private UserInfo getChecker(SymUser agentUser) {
    UserInfo user = new UserInfo();
    user.setDisplayName(agentUser.getDisplayName());
    user.setUserId(agentUser.getId());
    return user;
  }

}
