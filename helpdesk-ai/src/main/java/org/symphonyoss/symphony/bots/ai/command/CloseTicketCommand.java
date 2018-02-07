package org.symphonyoss.symphony.bots.ai.command;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.symphony.bots.ai.AiAction;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.AiResponseIdentifier;
import org.symphonyoss.symphony.bots.ai.HelpDeskAiSession;
import org.symphonyoss.symphony.bots.ai.HelpDeskAiSessionContext;
import org.symphonyoss.symphony.bots.ai.common.HelpDeskAiConstants;
import org.symphonyoss.symphony.bots.ai.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.ai.impl.AiResponseIdentifierImpl;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiSessionKey;
import org.symphonyoss.symphony.bots.ai.model.AiArgumentMap;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
import org.symphonyoss.symphony.bots.ai.model.AiMessage;
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
 * Created by nick.tarsillo on 10/9/17.
 */
public class CloseTicketCommand extends AiCommand {
  public CloseTicketCommand(String command, String usage) {
    super(command, usage);
    addAction(new ExitAction());
  }

  class ExitAction implements AiAction {
    @Override
    public void doAction(AiSessionContext sessionContext, AiResponder responder,
        AiArgumentMap aiArgumentMap) {
      SymphonyAiSessionKey aiSessionKey = (SymphonyAiSessionKey) sessionContext.getAiSessionKey();
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
        } catch (SymException e) {
          responder.addResponse(sessionContext, internalErrorResponse(aiSessionKey));
          updateTicket(ticketClient, ticket, currentState);
        }
      } catch (HelpDeskApiException e) {
        responder.addResponse(sessionContext, internalErrorResponse(aiSessionKey));
      }
    }

    private Ticket updateTicket(TicketClient client, Ticket ticket, String state) {
      ticket.setState(state);
      return client.updateTicket(ticket);
    }

    private AiResponse successResponse(HelpDeskAiConfig helpDeskAiConfig, Ticket ticket) {
      return response(helpDeskAiConfig.getCloseTicketSuccessResponse(), ticket.getClientStreamId());
    }

    private AiResponse internalErrorResponse(SymphonyAiSessionKey aiSessionKey) {
      return response(HelpDeskAiConstants.INTERNAL_ERROR, aiSessionKey.getStreamId());
    }

    private AiResponse response(String message, String stream) {
      AiMessage aiMessage = new AiMessage(message);
      Set<AiResponseIdentifier> responseIdentifiers = new HashSet<>();
      responseIdentifiers.add(new AiResponseIdentifierImpl(stream));
      return new AiResponse(aiMessage, responseIdentifiers);
    }
  }
}
