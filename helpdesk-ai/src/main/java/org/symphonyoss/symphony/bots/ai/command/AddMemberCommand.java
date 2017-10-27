package org.symphonyoss.symphony.bots.ai.command;

import org.symphonyoss.symphony.bots.ai.AiAction;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.AiResponseIdentifier;
import org.symphonyoss.symphony.bots.ai.HelpDeskAiSession;
import org.symphonyoss.symphony.bots.ai.HelpDeskAiSessionContext;
import org.symphonyoss.symphony.bots.ai.common.HelpDeskAiConstants;
import org.symphonyoss.symphony.bots.ai.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.ai.impl.AiResponseIdentifierImpl;
import org.symphonyoss.symphony.bots.ai.model.AiArgumentMap;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiResponse;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.ai.model.ArgumentType;
import org.symphonyoss.symphony.bots.ai.HelpDeskAiSessionKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.symphony.bots.helpdesk.service.client.MembershipClient;
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
      HelpDeskAiSessionContext aiSessionContext = (HelpDeskAiSessionContext) sessionContext;
      HelpDeskAiSession helpDeskAiSession = aiSessionContext.getHelpDeskAiSession();
      HelpDeskAiConfig helpDeskAiConfig = helpDeskAiSession.getHelpDeskAiConfig();
      Iterator<String> keySet = aiArgumentMap.getKeySet().iterator();
      String mention = aiArgumentMap.getArgumentAsString(keySet.next());
      String type = aiArgumentMap.getArgumentAsString(keySet.next());

      try {
        SymUser user = helpDeskAiSession.getSymphonyClient().getUsersClient().getUserFromEmail(mention.substring(1));
        if(user != null) {
          if(isMembershipEnumIgnoreCase(type)) {
            Membership membership = new Membership();
            membership.setId(user.getId().toString());
            membership.setGroupId(aiSessionKey.getGroupId());
            membership.setType(type.toUpperCase());

            helpDeskAiSession.getMembershipClient().updateMembership(membership);

            Stream stream = helpDeskAiSession.getSymphonyClient().getStreamsClient().getStream(user);

            responder.addResponse(sessionContext, successResponseAgent(helpDeskAiConfig, aiSessionKey));
            responder.addResponse(sessionContext, successResponseClient(helpDeskAiConfig, stream));
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
      for(MembershipClient.MembershipType membershipType : MembershipClient.MembershipType.values()) {
        if(membershipType.getType().equalsIgnoreCase(val)) {
          return true;
        }
      }

      return false;
    }

    private AiResponse successResponseAgent(HelpDeskAiConfig helpDeskAiConfig, HelpDeskAiSessionKey aiSessionKey) {
      return response(helpDeskAiConfig.getAddMemberAgentSuccessResponse(), aiSessionKey.getStreamId());
    }

    private AiResponse successResponseClient(HelpDeskAiConfig helpDeskAiConfig, Stream stream) {
      return response(helpDeskAiConfig.getAddMemberClientSuccessResponse(), stream.getId());
    }

    private AiResponse invalidMembershipTypeResponse(HelpDeskAiSessionKey aiSessionKey) {
      return response(HelpDeskAiConstants.INVALID_MEMBERSHIP_TYPE, aiSessionKey.getStreamId());
    }

    private AiResponse userNotFoundResponse(HelpDeskAiSessionKey aiSessionKey) {
      return response(HelpDeskAiConstants.USER_NOT_FOUND, aiSessionKey.getStreamId());
    }

    private AiResponse internalErrorResponse(HelpDeskAiSessionKey aiSessionKey) {
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
