package org.symphonyoss.symphony.bots.ai.impl;

import org.symphonyoss.symphony.bots.ai.AiCommandInterpreter;
import org.symphonyoss.symphony.bots.ai.AiEventListener;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.model.AiArgumentMap;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.ai.model.SymphonyAiSessionKey;

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
  public void onMessage(SymphonyAiSessionKey sessionKey, SymphonyAiMessage message,
      AiConversation aiConversation) {
    if (isNewMessage(message, aiConversation)) {

      AiCommandMenu commandMenu = aiConversation.getAiCommandMenu();

      if (aiConversation.isAllowCommands() && aiCommandInterpreter.hasPrefix(message,
          commandMenu.getCommandPrefix())) {
        onCommand(sessionKey, message, aiConversation);
      } else {
        onConversation(message, aiConversation);
      }

      aiConversation.setLastMessageId(message.getMessageId());
    }
  }

  private boolean isNewMessage(SymphonyAiMessage message, AiConversation aiConversation) {
    return !message.getMessageId().equals(aiConversation.getLastMessageId());
  }

  /**
   * Process message as commands.
   *
   * @param sessionKey session key
   * @param message Symphony message
   * @param aiConversation AI conversation
   */
  private void onCommand(SymphonyAiSessionKey sessionKey, SymphonyAiMessage message,
      AiConversation aiConversation) {
    List<AiCommand> commands = getMessageCommands(message, aiConversation);

    if (commands.isEmpty()) {
      processInvalidCommand(sessionKey, message, aiConversation);
    } else {
      commands.forEach(aiCommand -> processCommand(sessionKey, aiCommand, message, aiConversation));
    }
  }

  /**
   * Returns a list of commands in the message for this context.
   *
   * @param message Symphony message
   * @param aiConversation AI Conversation
   * @return List of commands
   */
  private List<AiCommand> getMessageCommands(SymphonyAiMessage message, AiConversation aiConversation) {
    AiCommandMenu commandMenu = aiConversation.getAiCommandMenu();
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
   * @param sessionKey session key
   * @param message Symphony message
   * @param aiConversation AI conversation
   */
  private void processInvalidCommand(SymphonyAiSessionKey sessionKey, SymphonyAiMessage message,
      AiConversation aiConversation) {
    aiResponder.respondWithUseMenu(sessionKey, aiConversation.getAiCommandMenu(), message);
  }

  /**
   * Process a command sending a message with the list of available commands and how to
   * use them. This method can also provide suggestion of commands if required.
   *
   * @param command AI command
   * @param message Symphony message
   * @param aiConversation AI conversation
   */
  private void processCommand(SymphonyAiSessionKey sessionKey, AiCommand command,
      SymphonyAiMessage message, AiConversation aiConversation) {
    String prefix = aiConversation.getAiCommandMenu().getCommandPrefix();

    AiArgumentMap args = aiCommandInterpreter.readCommandArguments(command, message, prefix);
    command.executeCommand(sessionKey, aiResponder, args);
  }

  /**
   * Process message as a conversation.
   *
   * @param message Symphony message
   * @param aiConversation AI conversation
   */
  private void onConversation(SymphonyAiMessage message, AiConversation aiConversation) {
    aiConversation.onMessage(aiResponder, message);
  }
}
