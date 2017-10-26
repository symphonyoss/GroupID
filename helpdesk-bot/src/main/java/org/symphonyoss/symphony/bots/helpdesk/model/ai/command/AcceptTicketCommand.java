package org.symphonyoss.symphony.bots.helpdesk.model.ai.command;

import org.symphonyoss.symphony.bots.ai.AiAction;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.AiResponseIdentifier;
import org.symphonyoss.symphony.bots.ai.impl.AiResponseIdentifierImpl;
import org.symphonyoss.symphony.bots.ai.model.AiArgumentMap;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiResponse;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.ai.model.ArgumentType;
import org.symphonyoss.symphony.bots.helpdesk.common.BotConstants;
import org.symphonyoss.symphony.bots.helpdesk.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.model.HelpDeskBotSession;
import org.symphonyoss.symphony.bots.helpdesk.model.ai.HelpDeskAiSessionContext;
import org.symphonyoss.symphony.bots.helpdesk.model.ai.HelpDeskAiSessionKey;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.TicketService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.exceptions.SymException;

import java.util.HashSet;
import java.util.Set;

/**
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
    @Override
    public void doAction(AiSessionContext sessionContext, AiResponder responder,
        AiArgumentMap aiArgumentMap) {
      HelpDeskAiSessionKey aiSessionKey = (HelpDeskAiSessionKey) sessionContext.getAiSessionKey();
      HelpDeskAiSessionContext helpDeskAiSessionContext = (HelpDeskAiSessionContext) sessionContext;
      HelpDeskBotSession helpDeskBotSession = helpDeskAiSessionContext.getHelpDeskBotSession();

      Set<String> keySet = aiArgumentMap.getKeySet();
      if (keySet.size() != 0) {
        Ticket ticket = helpDeskBotSession.getTicketService()
            .getTicket(aiArgumentMap.getArgumentAsString(keySet.iterator().next()));
        if(ticket != null) {
          try {
            helpDeskBotSession.getSymphonyClient().getRoomMembershipClient().addMemberToRoom(
                ticket.getServiceStreamId(), Long.parseLong(aiSessionKey.getUid()));
            ticket.setState(TicketService.TicketStateType.UNRESOLVED.getState());
            helpDeskBotSession.getTicketService().updateTicket(ticket);
            responder.addResponse(sessionContext, successResponse(aiSessionKey));
            responder.addResponse(sessionContext, successResponseClient(ticket));
          } catch (SymException e) {
            LOG.error("Failed to add agent to service room: ", e);
            responder.addResponse(sessionContext, failedToAddAgentToService(aiSessionKey));
          }
        } else {
          responder.addResponse(sessionContext, ticketNotFoundResponse(aiSessionKey));
        }
      } else {
        responder.respondWithUseMenu(sessionContext);
      }

      responder.respond(sessionContext);
    }

    private AiResponse successResponse(HelpDeskAiSessionKey aiSessionKey) {
      HelpDeskBotConfig helpDeskBotConfig = HelpDeskBotConfig.getConfig(aiSessionKey.getGroupId());

      return response(helpDeskBotConfig.getAcceptTicketSuccessResponse(), aiSessionKey.getStreamId());
    }

    private AiResponse successResponseClient(Ticket ticket) {
      HelpDeskBotConfig helpDeskBotConfig = HelpDeskBotConfig.getConfig(ticket.getGroupId());

      return response(helpDeskBotConfig.getClientServiceNotificationResponse(), ticket.getClientStreamId());
    }

    private AiResponse ticketNotFoundResponse(HelpDeskAiSessionKey aiSessionKey) {
      return response(BotConstants.TICKET_NOT_FOUND, aiSessionKey.getStreamId());
    }

    private AiResponse failedToAddAgentToService(HelpDeskAiSessionKey aiSessionKey) {
      return response(BotConstants.INTERNAL_ERROR, aiSessionKey.getStreamId());
    }

    private AiResponse response(String message, String stream) {
      AiMessage aiMessage = new AiMessage(message);
      Set<AiResponseIdentifier> responseIdentifiers = new HashSet<>();
      responseIdentifiers.add(new AiResponseIdentifierImpl(stream));
      return new AiResponse(aiMessage, responseIdentifiers);
    }
  }
}
