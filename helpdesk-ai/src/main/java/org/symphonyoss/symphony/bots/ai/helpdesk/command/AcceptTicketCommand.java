package org.symphonyoss.symphony.bots.ai.helpdesk.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.symphonyoss.symphony.bots.ai.model.ArgumentType;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;

import java.util.HashSet;
import java.util.Set;

/**
 * Command run when accepting a ticket in queue room
 * Created by nick.tarsillo on 10/10/17.
 */
public class AcceptTicketCommand extends AiCommand {

  private static final Logger LOG = LoggerFactory.getLogger(AcceptTicketCommand.class);

  public AcceptTicketCommand(String command, String usage) {
    super(command, usage);
    setArgumentTypes(ArgumentType.STRING);
    addAction(new AcceptAction());
  }

  class AcceptAction implements AiAction {
    /**
     * Fire the AcceptTicket command action
     * @param sessionContext current session context
     * @param responder object used to perform message answering
     * @param aiArgumentMap arguments passed to execute this action
     */
    @Override
    public void doAction(AiSessionContext sessionContext, AiResponder responder,
        AiArgumentMap aiArgumentMap) {
      SymphonyAiSessionKey aiSessionKey = (SymphonyAiSessionKey) sessionContext.getAiSessionKey();
      HelpDeskAiSessionContext aiSessionContext = (HelpDeskAiSessionContext) sessionContext;
      HelpDeskAiSession helpDeskAiSession = aiSessionContext.getHelpDeskAiSession();
      HelpDeskAiConfig helpDeskAiConfig = helpDeskAiSession.getHelpDeskAiConfig();

      Set<String> keySet = aiArgumentMap.getKeySet();
      Ticket ticket = helpDeskAiSession.getTicketClient()
          .getTicket(aiArgumentMap.getArgumentAsString(keySet.iterator().next()));
      if (ticket != null) {
        try {
          helpDeskAiSession.getSymphonyClient()
              .getRoomMembershipClient()
              .addMemberToRoom(ticket.getServiceStreamId(), aiSessionKey.getUid());
          ticket.setState(TicketClient.TicketStateType.UNRESOLVED.getState());
          helpDeskAiSession.getTicketClient().updateTicket(ticket);
          responder.addResponse(sessionContext, successResponseClient(helpDeskAiConfig, ticket));
        } catch (SymException e) {
          LOG.error("Failed to add agent to service room: ", e);
          responder.addResponse(sessionContext, failedToAddAgentToService(aiSessionKey));
        }
      } else {
        responder.addResponse(sessionContext, ticketNotFoundResponse(aiSessionKey));
      }

      responder.respond(sessionContext);
    }

    /**
     * The success response
     * @param helpDeskAiConfig The HelpDesk AI Configurations
     * @param ticket The ticket just accepted
     * @return The built AI response
     */
    private AiResponse successResponseClient(HelpDeskAiConfig helpDeskAiConfig, Ticket ticket) {
      return response(helpDeskAiConfig.getAcceptTicketClientSuccessResponse(), ticket.getClientStreamId());
    }

    /**
     * Response in case the ticket was not found
     * @param aiSessionKey the AI Session Key of this AI Session Context
     * @return The built AI response
     */
    private AiResponse ticketNotFoundResponse(SymphonyAiSessionKey aiSessionKey) {
      return response(HelpDeskAiConstants.TICKET_NOT_FOUND, aiSessionKey.getStreamId());
    }

    /**
     * Response in case of fail to add agent to service room
     * @param aiSessionKey the AI Session Key of this AI Session Context
     * @return The built AI response
     */
    private AiResponse failedToAddAgentToService(SymphonyAiSessionKey aiSessionKey) {
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
