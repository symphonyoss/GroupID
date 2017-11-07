package org.symphonyoss.symphony.bots.ai.impl;

import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.AiResponseIdentifier;
import org.symphonyoss.symphony.bots.ai.common.AiConstants;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiResponse;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by nick.tarsillo on 8/21/17.
 *
 * Example implementation of base AI.
 */
public class AiResponderImpl implements AiResponder {
  protected Map<AiSessionContext, Set<AiResponse>> responseMap = new HashMap<>();

  @Override
  public void respond(AiSessionContext sessionContext) {
    Map<AiResponseIdentifier, String> response = new HashMap<>();
    for(AiResponse aiResponse: responseMap.get(sessionContext)) {
      for(AiResponseIdentifier responseIdentifier: aiResponse.getRespondTo()) {
        response.put(responseIdentifier, response.get(responseIdentifier) + aiResponse.getMessage());
      }
    }

    for (AiResponseIdentifier respond : response.keySet()) {
      System.out.println(
          "To " + respond.getIdentityName() + ", \n" + response.get(sessionContext) + "\n From, "
              + sessionContext.getSessionName());
    }
  }

  @Override
  public void addResponse(AiSessionContext sessionContext, AiResponse aiResponse) {
    if(!responseMap.containsKey(sessionContext)) {
      responseMap.put(sessionContext, new HashSet<>());
    }

    responseMap.get(sessionContext).add(aiResponse);
  }

  @Override
  public void respondWithUseMenu(AiSessionContext sessionContext) {
    String response = AiConstants.MENU_TITLE + "\n" + sessionContext.getAiCommandMenu().toString();

    Set<AiResponseIdentifier> responseIdentifiers = new HashSet<>();
    AiResponseIdentifierImpl aiResponseIdentifier =
        new AiResponseIdentifierImpl(sessionContext.getSessionName(),
            sessionContext.getAiSessionKey().getSessionKey());
    responseIdentifiers.add(aiResponseIdentifier);

    AiResponse aiResponse = new AiResponse(new AiMessage(response), responseIdentifiers);
    addResponse(sessionContext, aiResponse);
    respond(sessionContext);
  }

  @Override
  public void respondWithSuggestion(AiSessionContext sessionContext,
      AiCommandInterpreter aiCommandInterpreter, AiMessage command) {
    respondWithUseMenu(sessionContext);

    AiCommand bestOption = getBestCommand(sessionContext, aiCommandInterpreter, command.getAiMessage());

    Set<AiResponseIdentifier> responseIdentifiers = new HashSet<>();
    AiResponseIdentifierImpl aiResponseIdentifier =
        new AiResponseIdentifierImpl(sessionContext.getSessionName(),
            sessionContext.getAiSessionKey().getSessionKey());
    responseIdentifiers.add(aiResponseIdentifier);

    AiResponse aiResponse = new AiResponse(
        new AiMessage(AiConstants.SUGGEST + bestOption.getCommand() + "? (Type /last to run menu.)"),
        responseIdentifiers);
    addResponse(sessionContext, aiResponse);
    respond(sessionContext);
  }

  protected AiCommand getBestCommand(AiSessionContext sessionContext,
      AiCommandInterpreter aiCommandInterpreter, String command) {
    AiCommand bestOption = null;
    int least = 999999999;
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
