package org.symphonyoss.symphony.bots.ai.helpdesk.command;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.helpdesk.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.ai.helpdesk.conversation.IdleTimerManager;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiArgumentMap;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
import org.symphonyoss.symphony.bots.ai.model.AiResponse;
import org.symphonyoss.symphony.bots.ai.model.SymphonyAiSessionKey;
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
  public void executeCommand(SymphonyAiSessionKey sessionKey, AiResponder responder,
      AiArgumentMap aiArgumentMap) {
    try {
      String streamId = sessionKey.getStreamId();

      Ticket ticket = ticketClient.getTicketByServiceStreamId(streamId);
      String currentState = ticket.getState();

      updateTicket(ticketClient, ticket, TicketClient.TicketStateType.RESOLVED.getState());

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
        updateTicket(ticketClient, ticket, currentState);
      }
    } catch (HelpDeskApiException e) {
      responder.respond(internalErrorResponse(sessionKey));
    }
  }

  /**
   * Updates the ticket to a new state
   * @param client the TicketClient
   * @param ticket the Ticket itself
   * @param state the new state
   * @return the updated ticket
   */
  private Ticket updateTicket(TicketClient client, Ticket ticket, String state) {
    ticket.setState(state);
    return client.updateTicket(ticket);
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
  private AiResponse internalErrorResponse(SymphonyAiSessionKey aiSessionKey) {
    return response(INTERNAL_ERROR, aiSessionKey.getStreamId());
  }

  /**
   * Build an AI response
   * @param message The string that will compose the AI Message
   * @param stream The stream where to send the response
   * @return the built AI response
   */
  private AiResponse response(String message, String stream) {
    SymphonyAiMessage aiMessage = new SymphonyAiMessage(message);
    return new AiResponse(aiMessage, stream);
  }
}
