package org.symphonyoss.symphony.bots.ai.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.AiResponseIdentifier;
import org.symphonyoss.symphony.bots.ai.common.AiConstants;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
import org.symphonyoss.symphony.bots.ai.model.AiResponse;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.ai.model.SymphonyAiSessionKey;
import org.symphonyoss.symphony.clients.MessagesClient;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Concrete implementation of {@link AiResponder}.
 * <p>
 * Created by nick.tarsillo on 8/21/17.
 */
public class SymphonyAiResponder implements AiResponder {

  private static final Logger LOG = LoggerFactory.getLogger(SymphonyAiResponder.class);

  protected Map<AiSessionContext, Set<AiResponse>> responseMap = new HashMap<>();

  private MessagesClient messagesClient;

  public SymphonyAiResponder(MessagesClient messagesClient) {
    this.messagesClient = messagesClient;
  }

  /**
   * Respond to a user through symphony.
   * @param sessionContext the context to base the response on.
   */
  @Override
  public void respond(AiSessionContext sessionContext) {
    Map<AiResponseIdentifier, SymphonyAiMessage> response = new HashMap<>();
    for (AiResponse aiResponse : responseMap.get(sessionContext)) {
      for (AiResponseIdentifier responseIdentifier : aiResponse.getRespondTo()) {
        if (!response.containsKey(responseIdentifier)) {
          response.put(responseIdentifier, aiResponse.getMessage());
        } else {
          SymphonyAiMessage message = response.get(responseIdentifier);
          message.setAiMessage(message.getAiMessage() + aiResponse.getMessage());

          message.setEntityData(message.getEntityData() + (aiResponse.getMessage()).getEntityData());
          message.getAttachments().addAll((aiResponse.getMessage()).getAttachments());
        }
      }
    }

    for (AiResponseIdentifier respond : response.keySet()) {
      SymphonyAiMessage symphonyAiMessage = response.get(respond);
      publishMessage(respond, symphonyAiMessage);
    }

    responseMap.put(sessionContext, new HashSet<>());
  }

  @Override
  public void addResponse(AiSessionContext sessionContext, AiResponse aiResponse) {
    if (!responseMap.containsKey(sessionContext)) {
      responseMap.put(sessionContext, new HashSet<>());
    }

    responseMap.get(sessionContext).add(aiResponse);
  }

  /**
   * Send the message over Symphony's message client using MessageML.
   * @param respond responder object to identify to where the message should be sent
   * @param symphonyAiMessage message to be sent
   */
  protected void publishMessage(AiResponseIdentifier respond, SymphonyAiMessage symphonyAiMessage) {
    String message = "<messageML>" + symphonyAiMessage.getAiMessage() + "</messageML>";
    SymMessage symMessage = new SymMessage();
    symMessage.setMessage(message);
    symMessage.setEntityData(symphonyAiMessage.getEntityData());
    symMessage.setAttachments(symphonyAiMessage.getAttachments());

    SymStream stream = new SymStream();
    stream.setStreamId(respond.getResponseIdentifier());
    try {
      messagesClient.sendMessage(stream, symMessage);
    } catch (MessagesException e) {
      LOG.error("Ai could not send message: ", e);
    }
  }

  /**
   * Responds with a use menu of all available commands.
   * @param sessionContext the session context to base the response on.
   */
  @Override
  public void respondWithUseMenu(AiSessionContext sessionContext, SymphonyAiMessage message) {
    String response = "<body>" + String.format(AiConstants.NOT_COMMAND, message.getAiMessage()) +
        "<br/><hr/><b>" + AiConstants.MENU_TITLE + "</b><ul><li>" +
        sessionContext.getAiCommandMenu().toString().replace(
            "\n", "</li><li>") + "</li></ul></body>";
    response = response.replace("<li></li>", "");

    SymphonyAiSessionKey symphonyAiSessionKey = sessionContext.getAiSessionKey();

    Set<AiResponseIdentifier> responseIdentifiers = new HashSet<>();
    SymphonyAiResponseIdentifierImpl aiResponseIdentifier =
        new SymphonyAiResponseIdentifierImpl(sessionContext.getSessionName(),
            symphonyAiSessionKey.getStreamId());
    responseIdentifiers.add(aiResponseIdentifier);

    AiResponse aiResponse = new AiResponse(new SymphonyAiMessage(response), responseIdentifiers);
    addResponse(sessionContext, aiResponse);
    respond(sessionContext);
  }

  /**
   * Respond with a suggested command.
   * @param sessionContext the session context to base the response on.
   * @param aiCommandInterpreter the command interpreter, used to interpret what command should
   * be suggested.
   * @param command the message containing the command.
   */
  @Override
  public void respondWithSuggestion(AiSessionContext sessionContext,
      AiCommandInterpreter aiCommandInterpreter, SymphonyAiMessage command) {
    SymphonyAiSessionKey symphonyAiSessionKey = sessionContext.getAiSessionKey();

    AiCommand bestOption =
        getBestCommand(sessionContext, aiCommandInterpreter, command.getAiMessage());

    Set<AiResponseIdentifier> responseIdentifiers = new HashSet<>();
    SymphonyAiResponseIdentifierImpl aiResponseIdentifier =
        new SymphonyAiResponseIdentifierImpl(sessionContext.getSessionName(),
            symphonyAiSessionKey.getStreamId());
    responseIdentifiers.add(aiResponseIdentifier);

    AiResponse aiResponse = new AiResponse(
        new SymphonyAiMessage(
            AiConstants.SUGGEST + bestOption.getCommand() + "? (Type <b>/last</b> to run menu.)"),
        responseIdentifiers);
    addResponse(sessionContext, aiResponse);
    respond(sessionContext);
  }

  /**
   * Retrieve the best command option according to the given command text
   * @param sessionContext current session context
   * @param aiCommandInterpreter command interpreter
   * @param command command text
   * @return best selected option for the command
   */
  protected AiCommand getBestCommand(AiSessionContext sessionContext,
      AiCommandInterpreter aiCommandInterpreter, String command) {
    AiCommand bestOption = null;
    int least = Integer.MAX_VALUE;
    for (AiCommand aiCommand : sessionContext.getAiCommandMenu().getCommandSet()) {
      int current = StringUtils.getLevenshteinDistance(
          aiCommandInterpreter.readCommandWithoutArguments(aiCommand), command);
      if (current < least) {
        bestOption = aiCommand;
        least = current;
      }
    }

    return bestOption;
  }

}
