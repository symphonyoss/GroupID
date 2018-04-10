package org.symphonyoss.symphony.bots.ai.helpdesk.command;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.symphony.authenticator.model.Token;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.helpdesk.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.ai.helpdesk.conversation.IdleTimerManager;
import org.symphonyoss.symphony.bots.ai.model.AiArgumentMap;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiResponse;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;
import org.symphonyoss.symphony.bots.helpdesk.service.HelpDeskApiException;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.pod.model.MemberInfo;
import org.symphonyoss.symphony.pod.model.MembershipList;

/**
 * Command when closing a ticket
 * Created by nick.tarsillo on 10/9/17.
 */
public class CloseTicketCommand extends AiCommand {

  private static final String INTERNAL_ERROR = "Something went wrong internally.";

  private final HelpDeskAiConfig helpDeskAiConfig;

  private final TicketClient ticketClient;

  private final SymphonyClient symphonyClient;

  private final IdleTimerManager idleTimerManager;

  public CloseTicketCommand(HelpDeskAiConfig config, TicketClient ticketClient,
      SymphonyClient symphonyClient, IdleTimerManager idleTimerManager) {
    super(config.getCloseTicketCommand());
    this.helpDeskAiConfig = config;
    this.ticketClient = ticketClient;
    this.symphonyClient = symphonyClient;
    this.idleTimerManager = idleTimerManager;
  }

  /**
   * Fire the CloseTicket command action
   * @param sessionKey the session key
   * @param responder object used to perform message answering
   * @param aiArgumentMap arguments passed to execute this action
   */
  @Override
  public void executeCommand(AiSessionKey sessionKey, AiResponder responder,
      AiArgumentMap aiArgumentMap) {
    try {
      Token sessionToken = symphonyClient.getSymAuth().getSessionToken();
      String jwt = sessionToken.getToken();

      String streamId = sessionKey.getStreamId();

      Ticket ticket = ticketClient.getTicketByServiceStreamId(jwt, streamId);
      String currentState = ticket.getState();

      ticket.setState(TicketClient.TicketStateType.RESOLVED.getState());
      ticketClient.updateTicket(jwt, ticket);

      try {
        MembershipList membershipList = symphonyClient.getRoomMembershipClient().getRoomMembership(streamId);

        for (MemberInfo membership : membershipList) {
          if (!membership.getId().equals(symphonyClient.getLocalUser().getId())) {
            symphonyClient
                .getRoomMembershipClient()
                .removeMemberFromRoom(streamId, membership.getId());
          }
        }

        responder.respond(successResponse(ticket));

        idleTimerManager.remove(ticket.getId());
      } catch (SymException e) {
        responder.respond(internalErrorResponse(sessionKey));

        ticket.setState(currentState);
        ticketClient.updateTicket(jwt, ticket);
      }
    } catch (HelpDeskApiException e) {
      responder.respond(internalErrorResponse(sessionKey));
    }
  }

  /**
   * Response when ticket is closed successfully
   * @param ticket the closed ticket
   * @return the built AI response
   */
  private AiResponse successResponse(Ticket ticket) {
    return response(helpDeskAiConfig.getCloseTicketSuccessResponse(), ticket.getClientStreamId());
  }

  /**
   * Response when the server returns an internal error
   * @param aiSessionKey The Key for this AI Session Context
   * @return the built AI response
   */
  private AiResponse internalErrorResponse(AiSessionKey aiSessionKey) {
    return response(INTERNAL_ERROR, aiSessionKey.getStreamId());
  }

  /**
   * Build an AI response
   * @param message The string that will compose the AI Message
   * @param stream The stream where to send the response
   * @return the built AI response
   */
  private AiResponse response(String message, String stream) {
    AiMessage aiMessage = new AiMessage(message);
    return new AiResponse(aiMessage, stream);
  }
}
