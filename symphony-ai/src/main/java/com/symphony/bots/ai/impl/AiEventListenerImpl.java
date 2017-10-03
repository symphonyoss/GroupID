package com.symphony.bots.ai.impl;

import com.symphony.bots.ai.AiCommandInterpreter;
import com.symphony.bots.ai.AiEventListener;
import com.symphony.bots.ai.AiResponder;
import com.symphony.bots.ai.model.AiCommand;
import com.symphony.bots.ai.model.AiConversation;
import com.symphony.bots.ai.model.AiMessage;
import com.symphony.bots.ai.model.AiSessionContext;

/**
 * Created by nick.tarsillo on 8/20/17.
 */
public class AiEventListenerImpl implements AiEventListener {
  private AiCommandInterpreter aiCommandInterpreter;
  private AiResponder aiResponder;
  private boolean suggestCommands;

  public AiEventListenerImpl(
      AiCommandInterpreter aiCommandInterpreter,
      AiResponder aiResponder,
      boolean suggestCommands) {
    this.aiCommandInterpreter = aiCommandInterpreter;
    this.aiResponder = aiResponder;
    this.suggestCommands = suggestCommands;
  }

  @Override
  public void onCommand(AiMessage command, AiSessionContext sessionContext) {
      boolean commandExecuted = false;
      for (AiCommand aiCommand : sessionContext.getAiCommandMenu().getCommandSet()) {
        if (aiCommandInterpreter.isCommand(aiCommand, command)) {
          aiCommand.executeCommand(sessionContext, aiResponder,
              aiCommandInterpreter.readCommandArguments(aiCommand, command));
          commandExecuted = true;
        }
      }

      if (!commandExecuted) {
        aiResponder.respondWithUseMenu(sessionContext);
        if (suggestCommands) {
          aiResponder.respondWithSuggestion(sessionContext, aiCommandInterpreter, command);
        }
      }
  }

  @Override
  public void onConversation(AiMessage message, AiConversation aiConversation) {
    aiConversation.onMessage(aiResponder, message);
    aiConversation.getPreviousMessages().add(message.getAiMessage());
  }

}
