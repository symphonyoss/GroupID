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
import org.symphonyoss.symphony.bots.ai.impl.AiResponseIdentifierImpl;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiSessionKey;
import org.symphonyoss.symphony.bots.ai.model.AiArgumentMap;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiResponse;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.ai.model.ArgumentType;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.clients.RoomMembershipClient;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Command when adding a member to a service room
 * Created by nick.tarsillo on 10/12/17.
 */
public class AddMemberCommand extends AiCommand {
  private static final Logger LOG = LoggerFactory.getLogger(AddMemberCommand.class);

  public AddMemberCommand(String command, String usage) {
    super(command, usage);
    setArgumentTypes(ArgumentType.LONG, ArgumentType.STRING);
    addAction(new AddMemberAction());
  }

  class AddMemberAction implements AiAction {
    /**
     * Fire the AddMember command action
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
      Iterator<String> keySet = aiArgumentMap.getKeySet().iterator();
      Long userId = aiArgumentMap.getArgumentAsLong(keySet.next());
      String type = aiArgumentMap.getArgumentAsString(keySet.next());

      try {
        Membership agentMembership = helpDeskAiSession.getMembershipClient().getMembership(aiSessionKey.getUid());
        SymUser user = helpDeskAiSession.getSymphonyClient().getUsersClient().getUserFromId(userId);
        if(userId.equals(aiSessionKey.getUid())) {
          responder.addResponse(sessionContext, cannotPromoteSelf(aiSessionKey));
        } else if(user.equals(null)) {
          responder.addResponse(sessionContext, userNotFoundResponse(aiSessionKey));
        } else if(!isMembershipEnumIgnoreCase(type)) {
          responder.addResponse(sessionContext, invalidMembershipTypeResponse(aiSessionKey));
        } else if(!agentPermitted(agentMembership.getType(), type)) {
          responder.addResponse(sessionContext, agentNotPermitted(aiSessionKey));
        } else {
          Membership newMembership = new Membership();
          newMembership.setId(userId);
          newMembership.setGroupId(aiSessionContext.getGroupId());
          newMembership.setType(type.toUpperCase());

          Membership membership = helpDeskAiSession.getMembershipClient().getMembership(userId);
          if(membership == null) {
            helpDeskAiSession.getMembershipClient().newMembership(userId,
                MembershipClient.MembershipType.valueOf(type.toUpperCase()));
          } else {
            helpDeskAiSession.getMembershipClient().updateMembership(membership);
          }

          if (membership.getType().equals(MembershipClient.MembershipType.AGENT)) {
            RoomMembershipClient roomMembershipClient =
                helpDeskAiSession.getSymphonyClient().getRoomMembershipClient();
            roomMembershipClient.addMemberToRoom(
                helpDeskAiSession.getHelpDeskAiConfig().getAgentStreamId(),
                userId);
          }

          SymStream stream =
              helpDeskAiSession.getSymphonyClient().getStreamsClient().getStream(user);

          responder.addResponse(sessionContext,
              successResponseAgent(helpDeskAiConfig, aiSessionKey));
          responder.addResponse(sessionContext,
              successResponseClient(helpDeskAiConfig, stream));
        }
      } catch (SymException e) {
        responder.addResponse(sessionContext, internalErrorResponse(aiSessionKey));
        LOG.error("Failed to search for user when adding member: ", e);
      }

      responder.respond(sessionContext);
    }

    /**
     * Checks if input value is a valid MembershipType
     * @param val the input value
     * @return true if input is a valid MembershipType, false otherwise
     */
    private boolean isMembershipEnumIgnoreCase(String val) {
      for(MembershipClient.MembershipType membershipType : MembershipClient.MembershipType.values()) {
        if(membershipType.getType().equalsIgnoreCase(val)) {
          return true;
        }
      }

      return false;
    }


    /**
     * Checks if agent is permitted to become a member of agent room
     * @param agentMembershipType The agent's MembershipType
     * @param newMembershipType The MembershipType that agent is trying to have
     * @return true if permitted, false ortherwise
     */
    private boolean agentPermitted(String agentMembershipType, String newMembershipType) {
      return MembershipClient.MembershipType.valueOf(agentMembershipType).ordinal() >=
          MembershipClient.MembershipType.valueOf(newMembershipType.toUpperCase()).ordinal();
    }

    /**
     * Response to the agent room when new agent is successfully added
     * @param helpDeskAiConfig The HelpDesk AI Configurations
     * @param aiSessionKey The Key for this AI Session Context
     * @return the built AI response
     */
    private AiResponse successResponseAgent(HelpDeskAiConfig helpDeskAiConfig, SymphonyAiSessionKey aiSessionKey) {
      return response(helpDeskAiConfig.getAddMemberAgentSuccessResponse(), aiSessionKey.getStreamId());
    }

    /**
     * Response to the agent when he is successfully added to agent room
     * @param helpDeskAiConfig The HelpDesk AI Configurations
     * @param stream The stream where to send the response
     * @return the built AI response
     */
    private AiResponse successResponseClient(HelpDeskAiConfig helpDeskAiConfig, SymStream stream) {
      return response(helpDeskAiConfig.getAddMemberClientSuccessResponse(), stream.getStreamId());
    }

    /**
     * Response when the MembershipType of the agent is invalid
     * @param aiSessionKey The Key for this AI Session Context
     * @return the built AI response
     */
    private AiResponse invalidMembershipTypeResponse(SymphonyAiSessionKey aiSessionKey) {
      return response(HelpDeskAiConstants.INVALID_MEMBERSHIP_TYPE, aiSessionKey.getStreamId());
    }

    /**
     * Response when the agent cannot be promoted
     * @param aiSessionKey The Key for this AI Session Context
     * @return the built AI response
     */
    private AiResponse cannotPromoteSelf(SymphonyAiSessionKey aiSessionKey) {
      return response(HelpDeskAiConstants.CANNOT_PROMOTE_SELF, aiSessionKey.getStreamId());
    }

    /**
     * Response when agent MembershipType is not permitted
     * @param aiSessionKey The Key for this AI Session Context
     * @return the built AI response
     */
    private AiResponse agentNotPermitted(SymphonyAiSessionKey aiSessionKey) {
      return response(HelpDeskAiConstants.NO_MEMBERSHIP_PERMISSION, aiSessionKey.getStreamId());
    }

    /**
     * Response when the user is not found
     * @param aiSessionKey The Key for this AI Session Context
     * @return the built AI response
     */
    private AiResponse userNotFoundResponse(SymphonyAiSessionKey aiSessionKey) {
      return response(HelpDeskAiConstants.USER_NOT_FOUND, aiSessionKey.getStreamId());
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
      AiMessage aiMessage = new AiMessage(message);
      Set<AiResponseIdentifier> responseIdentifiers = new HashSet<>();
      responseIdentifiers.add(new AiResponseIdentifierImpl(stream));
      return new AiResponse(aiMessage, responseIdentifiers);
    }
  }
}
