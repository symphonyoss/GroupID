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
import org.symphonyoss.symphony.bots.helpdesk.common.BotConstants;
import org.symphonyoss.symphony.bots.helpdesk.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.model.HelpDeskBotSession;
import org.symphonyoss.symphony.bots.helpdesk.model.ai.HelpDeskAiSessionContext;
import org.symphonyoss.symphony.bots.helpdesk.model.ai.HelpDeskAiSessionKey;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.TicketService;

import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.symphony.pod.model.MemberInfo;

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
      HelpDeskAiSessionKey aiSessionKey = (HelpDeskAiSessionKey) sessionContext.getAiSessionKey();
      HelpDeskAiSessionContext helpDeskAiSessionContext =
          (HelpDeskAiSessionContext) sessionContext;
      HelpDeskBotSession helpDeskBotSession = helpDeskAiSessionContext.getHelpDeskBotSession();

      try {
        Room room = helpDeskBotSession.getSymphonyClient()
            .getRoomService()
            .getRoom(aiSessionKey.getStreamId());
        for (MemberInfo membership : room.getMembershipList()) {
          if(!membership.getId().equals(helpDeskBotSession.getBotUser().getId())) {
            helpDeskBotSession.getSymphonyClient()
                .getRoomMembershipClient()
                .removeMemberFromRoom(aiSessionKey.getStreamId(), membership.getId());
          }
        }

        Ticket ticket = helpDeskBotSession.getTicketService()
            .getTicketByServiceStreamId(aiSessionKey.getStreamId());
        ticket.setState(TicketService.TicketStateType.RESOLVED.getState());
        helpDeskBotSession.getTicketService().updateTicket(ticket);
        responder.addResponse(sessionContext, successResponse(ticket));
      } catch (SymException e) {
        responder.addResponse(sessionContext, internalErrorResponse(aiSessionKey));
      }
    }

    private AiResponse successResponse(Ticket ticket) {
      HelpDeskBotConfig helpDeskBotConfig = HelpDeskBotConfig.getConfig(ticket.getGroupId());
      return response(helpDeskBotConfig.getCloseTicketSuccessResponse(), ticket.getClientStreamId());
    }

    private AiResponse internalErrorResponse(HelpDeskAiSessionKey aiSessionKey) {
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
