package org.symphonyoss.symphony.bots.ai.impl;

import org.apache.commons.lang3.StringUtils;
import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.AiEventListener;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;

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
      AiCommandMenu commandMenu = sessionContext.getAiCommandMenu();
      for (AiCommand aiCommand : commandMenu.getCommandSet()) {
        if ( startsWithPrefix(commandMenu, command) &&
            aiCommandInterpreter.isCommand(aiCommand, command, commandMenu.getCommandPrefix())) {
          aiCommand.executeCommand(sessionContext, aiResponder,
              aiCommandInterpreter.readCommandArguments(aiCommand, command));
          commandExecuted = true;
        }
      }

      if (!commandExecuted && startsWithPrefix(commandMenu, command)) {
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

  private boolean startsWithPrefix(AiCommandMenu commandMenu, AiMessage command) {
    return StringUtils.isNotBlank(commandMenu.getCommandPrefix())
        && command.getAiMessage().startsWith(commandMenu.getCommandPrefix());
  }

}
