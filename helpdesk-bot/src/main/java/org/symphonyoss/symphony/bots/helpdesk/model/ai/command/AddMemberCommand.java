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
import org.symphonyoss.symphony.bots.helpdesk.service.membership.MembershipService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.Stream;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by nick.tarsillo on 10/12/17.
 */
public class AddMemberCommand extends AiCommand {
  private static final Logger LOG = LoggerFactory.getLogger(AddMemberCommand.class);

  public AddMemberCommand(String command, String usage) {
    super(command, usage);
    setArgumentTypes(ArgumentType.STRING, ArgumentType.STRING);
    addAction(new AddMemberAction());
  }

  class AddMemberAction implements AiAction {
    @Override
    public void doAction(AiSessionContext sessionContext, AiResponder responder,
        AiArgumentMap aiArgumentMap) {
      HelpDeskAiSessionKey aiSessionKey = (HelpDeskAiSessionKey) sessionContext.getAiSessionKey();
      HelpDeskAiSessionContext helpDeskAiSessionContext = (HelpDeskAiSessionContext) sessionContext;
      HelpDeskBotSession helpDeskBotSession = helpDeskAiSessionContext.getHelpDeskBotSession();

      Iterator<String> keySet = aiArgumentMap.getKeySet().iterator();
      String mention = aiArgumentMap.getArgumentAsString(keySet.next());
      String type = aiArgumentMap.getArgumentAsString(keySet.next());

      try {
        SymUser user = helpDeskBotSession.getSymphonyClient().getUsersClient().getUserFromEmail(mention.substring(1));
        if(user != null) {
          if(isMembershipEnumIgnoreCase(type)) {
            Membership membership = new Membership();
            membership.setId(user.getId().toString());
            membership.setGroupId(aiSessionKey.getGroupId());
            membership.setType(type.toUpperCase());

            helpDeskBotSession.getMembershipService().updateMembership(membership);

            Stream stream = helpDeskBotSession.getSymphonyClient().getStreamsClient().getStream(user);

            responder.addResponse(sessionContext, successResponseAgent(aiSessionKey));
            responder.addResponse(sessionContext, successResponseClient(aiSessionKey, stream));
          } else {
            responder.addResponse(sessionContext, invalidMembershipTypeResponse(aiSessionKey));
          }
        } else {
          responder.addResponse(sessionContext, userNotFoundResponse(aiSessionKey));
        }
      } catch (UsersClientException | StreamsException e) {
        responder.addResponse(sessionContext, internalErrorResponse(aiSessionKey));
        LOG.error("Failed to search for user when adding member: ", e);
      }

      responder.respond(sessionContext);
    }

    private boolean isMembershipEnumIgnoreCase(String val) {
      for(MembershipService.MembershipType membershipType : MembershipService.MembershipType.values()) {
        if(membershipType.getType().equalsIgnoreCase(val)) {
          return true;
        }
      }

      return false;
    }

    private AiResponse successResponseAgent(HelpDeskAiSessionKey aiSessionKey) {
      HelpDeskBotConfig helpDeskBotConfig = HelpDeskBotConfig.getConfig(aiSessionKey.getGroupId());
      return response(helpDeskBotConfig.getAddMemberAgentSuccessResponse(), aiSessionKey.getStreamId());
    }

    private AiResponse successResponseClient(HelpDeskAiSessionKey aiSessionKey, Stream stream) {
      HelpDeskBotConfig helpDeskBotConfig = HelpDeskBotConfig.getConfig(aiSessionKey.getGroupId());
      return response(helpDeskBotConfig.getAddMemberClientSuccessResponse(), stream.getId());
    }

    private AiResponse invalidMembershipTypeResponse(HelpDeskAiSessionKey aiSessionKey) {
      return response(BotConstants.INVALID_MEMBERSHIP_TYPE, aiSessionKey.getStreamId());
    }

    private AiResponse userNotFoundResponse(HelpDeskAiSessionKey aiSessionKey) {
      return response(BotConstants.USER_NOT_FOUND, aiSessionKey.getStreamId());
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
