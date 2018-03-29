package org.symphonyoss.symphony.bots.ai.model;

import org.symphonyoss.symphony.bots.ai.AiAction;
import org.symphonyoss.symphony.bots.ai.AiPermission;
import org.symphonyoss.symphony.bots.ai.AiResponder;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A command that can triggered using a command line phrase.
 * <p>
 * Created by nick.tarsillo on 8/20/17.
 */
public class AiCommand implements Comparable {

  private String command;

  private ArgumentType[] argumentTypes;

  private Set<AiAction> actions = new LinkedHashSet<>();

  /**
   * Constructs a new AI command with the command string, the usage, and the argument types. It
   * has no
   * permissions or actions.
   * @param command Command line argument
   * @param argumentTypes array containing the type of each argument
   */
  public AiCommand(String command, ArgumentType... argumentTypes) {
    this.command = command;
    this.argumentTypes = argumentTypes;
  }

  /**
   * Executes the command.
   * @param sessionContext the session to base the execution on.
   * @param aiResponder a responder the ai can use to respond to users.
   * @param aiArgumentMap a map of arguments to execute the command with.
   */
  public void executeCommand(AiSessionContext sessionContext, AiResponder aiResponder,
      AiArgumentMap aiArgumentMap) {
    for (AiAction aiAction : actions) {
      aiAction.doAction(sessionContext, aiResponder, aiArgumentMap);
    }

    aiResponder.respond(sessionContext);
  }

  @Override
  public int compareTo(Object o) {
    if (o instanceof AiCommand) {
      AiCommand obj = (AiCommand) o;
      return command.compareTo(obj.getCommand());
    }

    return -2;
  }

  /**
   * Adds an {@link AiAction action } to be executed in the command.
   * @param aiAction
   */
  public void addAction(AiAction aiAction) {
    actions.add(aiAction);
  }

  public String getCommand() {
    return command;
  }

  /**
   * Retrieve the argument types for this command
   * @return argument type array
   */
  public ArgumentType[] getArgumentTypes() {
    return argumentTypes;
  }
}
