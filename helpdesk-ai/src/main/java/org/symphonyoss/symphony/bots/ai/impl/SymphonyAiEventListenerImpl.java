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

  private boolean suggestCommands;

  public SymphonyAiEventListenerImpl(AiCommandInterpreter aiCommandInterpreter, AiResponder aiResponder,
      boolean suggestCommands) {
    this.aiCommandInterpreter = aiCommandInterpreter;
    this.aiResponder = aiResponder;
    this.suggestCommands = suggestCommands;
  }

  /**
   * Check if this message contains a command.
   *
   * @param message {@link SymphonyAiMessage} containing the conversation message
   * @param aiConversation The {@link AiConversation} object containing the current conversation
   */
  @Override
  public void onMessage(SymphonyAiMessage message, AiConversation aiConversation) {
    AiSessionContext sessionContext = aiConversation.getAiSessionContext();

    AiCommandMenu commandMenu = sessionContext.getAiCommandMenu();
    String prefix = commandMenu.getCommandPrefix();

    if (aiConversation.isAllowCommands() && aiCommandInterpreter.hasPrefix(message, prefix)) {
      onCommand(message, sessionContext);
    } else {
      onConversation(message, aiConversation);
    }
  }

  /**
   * Process message as commands.
   *
   * @param message Symphony message
   * @param sessionContext AI session context
   */
  private void onCommand(SymphonyAiMessage message, AiSessionContext sessionContext) {
    List<AiCommand> commands = getMessageCommands(message, sessionContext);

    if (commands.isEmpty()) {
      processInvalidCommand(message, sessionContext);
    } else {
      commands.forEach(aiCommand -> processCommand(aiCommand, message, sessionContext));
    }
  }

  /**
   * Returns a list of commands in the message for this context.
   *
   * @param message Symphony message
   * @param sessionContext AI session context
   * @return List of commands
   */
  private List<AiCommand> getMessageCommands(SymphonyAiMessage message, AiSessionContext sessionContext) {
    AiCommandMenu commandMenu = sessionContext.getAiCommandMenu();
    String prefix = commandMenu.getCommandPrefix();

    return commandMenu.getCommandSet()
        .stream()
        .filter(aiCommand -> aiCommandInterpreter.isCommand(aiCommand, message, prefix))
        .collect(Collectors.toList());
  }

  /**
   * Process an invalid command sending a message with the list of available commands and how to
   * use them. This method can also provide suggestion of commands if required.
   *
   * @param message Symphony message
   * @param sessionContext AI session context
   */
  private void processInvalidCommand(SymphonyAiMessage message, AiSessionContext sessionContext) {
    aiResponder.respondWithUseMenu(sessionContext, message);

    if (suggestCommands) {
      aiResponder.respondWithSuggestion(sessionContext, aiCommandInterpreter, message);
    }
  }

  /**
   * Process a command sending a message with the list of available commands and how to
   * use them. This method can also provide suggestion of commands if required.
   *
   * @param command AI command
   * @param message Symphony message
   * @param sessionContext AI session context
   */
  private void processCommand(AiCommand command, SymphonyAiMessage message, AiSessionContext sessionContext) {
    String prefix = sessionContext.getAiCommandMenu().getCommandPrefix();

    AiArgumentMap args = aiCommandInterpreter.readCommandArguments(command, message, prefix);
    command.executeCommand(sessionContext, aiResponder, args);
  }

  /**
   * Process message as a conversation.
   *
   * @param message Symphony message
   * @param aiConversation AI conversation
   */
  private void onConversation(SymphonyAiMessage message, AiConversation aiConversation) {
    String messageId = message.getMessageId();

    if (!messageId.equals(aiConversation.getLastMessageId())) {
      aiConversation.onMessage(aiResponder, message);
      aiConversation.setLastMessageId(messageId);
    }
  }

}
