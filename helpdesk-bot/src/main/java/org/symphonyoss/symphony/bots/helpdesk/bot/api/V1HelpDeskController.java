package org.symphonyoss.symphony.bots.helpdesk.bot.api;


import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.symphony.bots.ai.helpdesk.HelpDeskAi;
import org.symphonyoss.symphony.bots.ai.model.AiMessage;
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
import org.symphonyoss.symphony.bots.utility.client.SymphonyClientUtil;
import org.symphonyoss.symphony.bots.utility.validation.SymphonyValidationUtil;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;

import javax.ws.rs.BadRequestException;

import java.util.Set;

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
  private static final String ATTACHMENT_TYPE = "ATTACHMENT";

  private final SymphonyValidationUtil symphonyValidationUtil;

  private final MakercheckerClient makercheckerClient;

  @Qualifier("agentMakerCheckerService")
  private final MakerCheckerService agentMakerCheckerService;

  private final HelpDeskAi helpDeskAi;

  private final AcceptTicketService acceptTicketService;

  private final JoinConversationService joinConversationService;

  private final ValidateMembershipService validateMembershipService;

  private final SymphonyClientUtil symphonyClientUtil;

  public V1HelpDeskController(SymphonyValidationUtil symphonyValidationUtil,
      MakercheckerClient makercheckerClient, MakerCheckerService agentMakerCheckerService,
      HelpDeskAi helpDeskAi, AcceptTicketService acceptTicketService,
      JoinConversationService joinConversationService,
      ValidateMembershipService validateMembershipService, SymphonyClient symphonyClient) {
    this.symphonyValidationUtil = symphonyValidationUtil;
    this.makercheckerClient = makercheckerClient;
    this.agentMakerCheckerService = agentMakerCheckerService;
    this.helpDeskAi = helpDeskAi;
    this.acceptTicketService = acceptTicketService;
    this.joinConversationService = joinConversationService;
    this.validateMembershipService = validateMembershipService;
    this.symphonyClientUtil = new SymphonyClientUtil(symphonyClient);
  }

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
   * @param makerCheckerId the identify of maker checker
   * @param userId the userId of approve maker checker
   * @return a maker checker message response
   */
  @Override
  public MakerCheckerResponse approveMakerCheckerMessage(String makerCheckerId, Long userId) {
    validateRequiredParameter("makerCheckerId", makerCheckerId, "body");
    validateRequiredParameter("userId", userId, "body");

    String jwt = symphonyClientUtil.getAuthToken();

    SymUser agentUser = symphonyValidationUtil.validateUserId(userId);

    Makerchecker makerchecker = makercheckerClient.getMakerchecker(jwt, makerCheckerId);
    if (makerchecker == null) {
      throw new BadRequestException(MAKER_CHECKER_NOT_FOUND);
    }

    if (makerchecker.getMakerId().equals(userId)) {
      throw new BadRequestException(OWN_ATTACHMENT_EXCPETION);
    }

    try {
      validateMembershipService.updateMembership(userId);
    } catch (SymException e) {
      throw new BadRequestException("User is not an agent");
    }

    if (MakercheckerClient.AttachmentStateType.OPENED.getState().equals(makerchecker.getState())) {
      sendApprovedMakerChekerMessage(makerchecker);

      UserInfo checker = getChecker(agentUser);
      makerchecker.setChecker(checker);
      makerchecker.setState(MakercheckerClient.AttachmentStateType.APPROVED.getState());

      agentMakerCheckerService.sendActionMakerCheckerMessage(makerchecker, MakercheckerClient.AttachmentStateType.APPROVED);

      makercheckerClient.updateMakerchecker(jwt, makerchecker);

      return buildMakerCheckerResponse(agentUser, makerchecker);
    } else {
      throw new BadRequestException(OPEN_MAKERCHECKER_NOT_FOUND);
    }
  }


  /**
   * Deny a maker checker message.
   * @param makerCheckerId the identify of maker checker
   * @param userId the userId of deny maker checker
   * @return a maker checker message response
   */
  @Override
  public MakerCheckerResponse denyMakerCheckerMessage(String makerCheckerId, Long userId) {
    validateRequiredParameter("makerCheckerId", makerCheckerId, "body");
    validateRequiredParameter("userId", userId, "body");

    String jwt = symphonyClientUtil.getAuthToken();

    SymUser agentUser = symphonyValidationUtil.validateUserId(userId);

    Makerchecker makerchecker = makercheckerClient.getMakerchecker(jwt, makerCheckerId);
    if (makerchecker == null) {
      throw new BadRequestException(MAKER_CHECKER_NOT_FOUND);
    }

    if (makerchecker.getMakerId().equals(userId)) {
      throw new BadRequestException(OWN_ATTACHMENT_EXCPETION);
    }

    try {
      validateMembershipService.updateMembership(userId);
    } catch (SymException e) {
      throw new BadRequestException("User is not an agent");
    }

    if (MakercheckerClient.AttachmentStateType.OPENED.getState().equals(makerchecker.getState())) {
      UserInfo checker = getChecker(agentUser);

      makerchecker.setChecker(checker);
      makerchecker.setState(MakercheckerClient.AttachmentStateType.DENIED.getState());

      agentMakerCheckerService.sendActionMakerCheckerMessage(makerchecker, MakercheckerClient.AttachmentStateType.DENIED);

      makercheckerClient.updateMakerchecker(jwt, makerchecker);

      return buildMakerCheckerResponse(agentUser, makerchecker);
    } else {
      throw new BadRequestException(OPEN_MAKERCHECKER_NOT_FOUND);
    }
  }

  private MakerCheckerResponse buildMakerCheckerResponse(SymUser agentUser,
      Makerchecker makerchecker) {
    MakerCheckerResponse makerCheckerResponse = new MakerCheckerResponse();

    User user = getUser(makerchecker, agentUser);

    makerCheckerResponse.setUser(user);

    if (MakercheckerClient.AttachmentStateType.APPROVED.getState().equals(makerchecker.getState())) {
      makerCheckerResponse.setMessage(MAKER_CHECKER_SUCCESS_RESPONSE);
      makerCheckerResponse.setState(MakercheckerClient.AttachmentStateType.APPROVED.getState());
    } else {
      makerCheckerResponse.setMessage(MAKER_CHECKER_DENY_RESPONSE);
      makerCheckerResponse.setState(MakercheckerClient.AttachmentStateType.DENIED.getState());
    }

    return makerCheckerResponse;
  }

  private void sendApprovedMakerChekerMessage(Makerchecker makerchecker) {
    AttachmentMakerCheckerMessage checkerMessage = new AttachmentMakerCheckerMessage();
    checkerMessage.setAttachmentId(makerchecker.getAttachmentId());
    checkerMessage.setGroupId(makerchecker.getGroupId());
    checkerMessage.setMessageId(makerchecker.getMessageId());
    checkerMessage.setStreamId(Base64.encodeBase64URLSafeString(Base64.decodeBase64(makerchecker.getStreamId())));
    checkerMessage.setProxyToStreamIds(makerchecker.getProxyToStreamIds());
    checkerMessage.setTimeStamp(makerchecker.getTimeStamp());
    checkerMessage.setType(ATTACHMENT_TYPE);

    Set<SymMessage> symMessages = agentMakerCheckerService.getApprovedMakercheckerMessage(checkerMessage);

    for (SymMessage symMessage : symMessages) {
      AiMessage aiMessage = new AiMessage(symMessage);
      helpDeskAi.sendMessage(aiMessage, symMessage.getStreamId());

      if (aiMessage.getAttachment() != null) {
        agentMakerCheckerService.afterSendApprovedMessage(symMessage);
      }
    }
  }

  private User getUser(Makerchecker makerchecker, SymUser agentUser) {
    User user = new User();
    user.setDisplayName(agentUser.getDisplayName());
    user.setUserId(makerchecker.getMakerId());
    return user;
  }

  private UserInfo getChecker(SymUser agentUser) {
    UserInfo user = new UserInfo();
    user.setDisplayName(agentUser.getDisplayName());
    user.setUserId(agentUser.getId());
    return user;
  }

}
