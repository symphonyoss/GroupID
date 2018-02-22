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
  public String getCommand() {
    return command;
  }

  private String command;
  private String usage;

  private ArgumentType[] argumentTypes;
  private Set<AiAction> actions = new LinkedHashSet<>();
  private Set<AiPermission> permissions = new HashSet<>();

  /**
   * Constructs a new AI command with the command string and usage. It has no argument typing or
   * permissions or actions.
   * @param command Command line argument
   * @param usage Text explaining how to use this command
   */
  public AiCommand(String command, String usage) {
    this.command = command;
    this.usage = usage;
  }

  /**
   * Constructs a new AI command with the command string, the usage, and the argument types. It
   * has no
   * permissions or actions.
   * @param command Command line argument
   * @param usage Text explaining how to use this command
   * @param argumentTypes array containing the type of each argument
   */
  public AiCommand(String command, String usage, ArgumentType... argumentTypes) {
    this.command = command;
    this.usage = usage;
    this.argumentTypes = argumentTypes;
  }

  /**
   * Constructs a new AI command with the command string, the usage and the set of actions and
   * permissions
   * @param command Command line argument
   * @param usage Text explaining how to use this command
   * @param actions Set of actions taken when this command is executed
   * @param permissions Set of permissions for this command
   */
  public AiCommand(String command, String usage, Set<AiAction> actions,
      Set<AiPermission> permissions) {
    this.command = command;
    this.usage = usage;
    this.actions = actions;
    this.permissions = permissions;
  }

  /**
   * Constructs a new AI command using all of its resources: <br>
   * <ul>
   * <li>Command text</li>
   * <li>User friendly command usage</li>
   * <li>Actions taken when executing the command</li>
   * <li>Permissions to execute this command</li>
   * <li>Argument type checking</li>
   * </ul>
   * @param command Command line argument
   * @param usage Text explaining how to use this command
   * @param actions Set of actions taken when this command is executed
   * @param permissions Set of permissions for this command
   * @param argumentTypes array containing the type of each argument
   */
  public AiCommand(String command, String usage, Set<AiAction> actions,
      Set<AiPermission> permissions, ArgumentType... argumentTypes) {
    this.command = command;
    this.usage = usage;
    this.actions = actions;
    this.permissions = permissions;
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

  /**
   * Check if a user is permitted to use this command.
   * @param sessionContext
   * @return
   */
  public boolean permittedToUseCommand(AiSessionContext sessionContext) {
    for (AiPermission aiPermission : permissions) {
      if (!aiPermission.sessionHasPermission(sessionContext)) {
        return false;
      }
    }
    return true;
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
   * Set a new argument type array.
   * @param argumentTypes array of argument types
   */
  public void setArgumentTypes(ArgumentType... argumentTypes) {
    this.argumentTypes = argumentTypes;
  }

  /**
   * Adds an {@link AiAction action } to be executed in the command.
   * @param aiAction
   */
  public void addAction(AiAction aiAction) {
    actions.add(aiAction);
  }

  /**
   * Adds an {@link AiPermission permission} to the permissions set.
   * @param aiPermission
   */
  public void addPermission(AiPermission aiPermission) {
    permissions.add(aiPermission);
  }

  /**
   * Retrieve the command usage text. This can only be set in the object creation.
   * @return command usage text
   */
  public String getUsage() {
    return usage;
  }

  /**
   * Retrieve the argument types for this command
   * @return argument type array
   */
  public ArgumentType[] getArgumentTypes() {
    return argumentTypes;
  }
}
