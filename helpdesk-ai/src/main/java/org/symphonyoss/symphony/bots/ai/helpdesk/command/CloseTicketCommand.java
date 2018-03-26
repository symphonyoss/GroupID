package org.symphonyoss.symphony.bots.ai.helpdesk.command;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.symphony.bots.ai.AiAction;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.AiResponseIdentifier;
import org.symphonyoss.symphony.bots.ai.helpdesk.HelpDeskAiSession;
import org.symphonyoss.symphony.bots.ai.helpdesk.HelpDeskAiSessionContext;
import org.symphonyoss.symphony.bots.ai.helpdesk.common.HelpDeskAiConstants;
import org.symphonyoss.symphony.bots.ai.helpdesk.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiResponseIdentifierImpl;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;
import org.symphonyoss.symphony.bots.ai.model.SymphonyAiSessionKey;
import org.symphonyoss.symphony.bots.ai.model.AiArgumentMap;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
import org.symphonyoss.symphony.bots.ai.model.AiResponse;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.helpdesk.service.HelpDeskApiException;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.pod.model.MemberInfo;
import org.symphonyoss.symphony.pod.model.MembershipList;

import java.util.HashSet;
import java.util.Set;

/**
 * Command when closing a ticket
 * Created by nick.tarsillo on 10/9/17.
 */
public class CloseTicketCommand extends AiCommand {

  public CloseTicketCommand(HelpDeskAiConfig config) {
    super(config.getCloseTicketCommand(),
        config.getAgentServiceRoomPrefix() + config.getCloseTicketCommand());
    addAction(new ExitAction());
  }

  private class ExitAction implements AiAction {
    /**
     * Fire the CloseTicket command action
     * @param sessionContext current session context
     * @param responder object used to perform message answering
     * @param aiArgumentMap arguments passed to execute this action
     */
    @Override
    public void doAction(AiSessionContext sessionContext, AiResponder responder,
        AiArgumentMap aiArgumentMap) {
      SymphonyAiSessionKey aiSessionKey = sessionContext.getAiSessionKey();
      HelpDeskAiSessionContext aiSessionContext = (HelpDeskAiSessionContext) sessionContext;
      HelpDeskAiSession helpDeskAiSession = aiSessionContext.getHelpDeskAiSession();
      HelpDeskAiConfig helpDeskAiConfig = helpDeskAiSession.getHelpDeskAiConfig();
      TicketClient ticketClient = helpDeskAiSession.getTicketClient();
      SymphonyClient symphonyClient = helpDeskAiSession.getSymphonyClient();

      try {
        Ticket ticket = ticketClient.getTicketByServiceStreamId(aiSessionKey.getStreamId());
        String currentState = ticket.getState();

        updateTicket(ticketClient, ticket, TicketClient.TicketStateType.RESOLVED.getState());

        try {
          MembershipList membershipList = symphonyClient.getRoomMembershipClient()
              .getRoomMembership(aiSessionKey.getStreamId());

          for (MemberInfo membership : membershipList) {
            if (!membership.getId().equals(symphonyClient.getLocalUser().getId())) {
              symphonyClient
                  .getRoomMembershipClient()
                  .removeMemberFromRoom(aiSessionKey.getStreamId(), membership.getId());
            }
          }

          responder.addResponse(sessionContext, successResponse(helpDeskAiConfig, ticket));

          aiSessionContext.getIdleTimerManager().remove(ticket.getId());
        } catch (SymException e) {
          responder.addResponse(sessionContext, internalErrorResponse(aiSessionKey));
          updateTicket(ticketClient, ticket, currentState);
        }
      } catch (HelpDeskApiException e) {
        responder.addResponse(sessionContext, internalErrorResponse(aiSessionKey));
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
     * @param helpDeskAiConfig The HelpDesk AI Configurations
     * @param ticket the closed ticket
     * @return the built AI response
     */
    private AiResponse successResponse(HelpDeskAiConfig helpDeskAiConfig, Ticket ticket) {
      return response(helpDeskAiConfig.getCloseTicketSuccessResponse(), ticket.getClientStreamId());
    }

    /**
     * Response when the server returns an internal error
     * @param aiSessionKey The Key for this AI Session Context
     * @return the built AI response
     */
    private AiResponse internalErrorResponse(SymphonyAiSessionKey aiSessionKey) {
      return response(HelpDeskAiConstants.INTERNAL_ERROR, aiSessionKey.getStreamId());
    }

    /**
     * Build an AI response
     * @param message The string that will compose the AI Message
     * @param stream The stream where to send the response
     * @return the built AI response
     */
    private AiResponse response(String message, String stream) {
      SymphonyAiMessage aiMessage = new SymphonyAiMessage(message);
      Set<AiResponseIdentifier> responseIdentifiers = new HashSet<>();
      responseIdentifiers.add(new SymphonyAiResponseIdentifierImpl(stream));
      return new AiResponse(aiMessage, responseIdentifiers);
    }
  }
}
