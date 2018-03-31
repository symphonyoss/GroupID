package org.symphonyoss.symphony.bots.ai.impl;

import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.AiEventListener;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.model.AiArgumentMap;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Concrete implementation for an {@link AiEventListener AI event listener}
 * <p>
 * Created by nick.tarsillo on 8/20/17.
 */
public class SymphonyAiEventListenerImpl implements AiEventListener {

  private AiCommandInterpreter aiCommandInterpreter;

  private AiResponder aiResponder;

  public SymphonyAiEventListenerImpl(AiCommandInterpreter aiCommandInterpreter, AiResponder aiResponder) {
    this.aiCommandInterpreter = aiCommandInterpreter;
    this.aiResponder = aiResponder;
  }

  @Override
  public void onCommand(SymphonyAiMessage command, AiSessionContext sessionContext) {
    AiCommandMenu commandMenu = sessionContext.getAiCommandMenu();
    String prefix = commandMenu.getCommandPrefix();

    if (!aiCommandInterpreter.hasPrefix(command, prefix)) {
      return;
    }

    List<AiCommand> commands = commandMenu.getCommandSet()
        .stream()
        .filter(aiCommand -> aiCommandInterpreter.isCommand(aiCommand, command, prefix))
        .collect(Collectors.toList());

    if (commands.isEmpty()) {
      aiResponder.respondWithUseMenu(sessionContext.getAiSessionKey(), commandMenu, command);
    } else {
      commands.forEach(aiCommand -> {
        AiArgumentMap args = aiCommandInterpreter.readCommandArguments(aiCommand, command, prefix);
        aiCommand.executeCommand(sessionContext.getAiSessionKey(), aiResponder, args);
      });
    }
  }

  @Override
  public void onConversation(SymphonyAiMessage message, AiConversation aiConversation) {
    String prefix = aiConversation.getAiSessionContext().getAiCommandMenu().getCommandPrefix();
    SymphonyAiMessage lastMessage = aiConversation.getLastMessage();

    if ((!aiConversation.isAllowCommands() || !aiCommandInterpreter.hasPrefix(message, prefix)) &&
        (lastMessage == null || !lastMessage.equals(message))) {
      aiConversation.onMessage(aiResponder, message);
      aiConversation.setLastMessage(message);
    }
  }

}
