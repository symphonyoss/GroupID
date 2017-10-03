package com.symphony.bots.ai.model;

import com.symphony.bots.ai.AiAction;
import com.symphony.bots.ai.AiPermission;
import com.symphony.bots.ai.AiResponder;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by nick.tarsillo on 8/20/17.
 * A command that can triggered using a command line phrase.
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

  public AiCommand(String command, String usage) {
    this.command = command;
    this.usage = usage;
  }

  public AiCommand(String command, String usage, ArgumentType... argumentTypes) {
    this.command = command;
    this.usage = usage;
    this.argumentTypes = argumentTypes;
  }

  public AiCommand(String command, String usage, Set<AiAction> actions, Set<AiPermission> permissions) {
    this.command = command;
    this.usage = usage;
    this.actions = actions;
    this.permissions = permissions;
  }

  public AiCommand(String command, String usage, Set<AiAction> actions, Set<AiPermission> permissions, ArgumentType... argumentTypes) {
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
  public void executeCommand(AiSessionContext sessionContext, AiResponder aiResponder, AiArgumentMap aiArgumentMap) {
    for(AiAction aiAction : actions) {
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
    for(AiPermission aiPermission : permissions) {
      if(!aiPermission.sessionHasPermission(sessionContext)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int compareTo(Object o) {
    if(o instanceof AiCommand) {
      AiCommand obj = (AiCommand) o;
      return command.compareTo(obj.getCommand());
    }

    return -2;
  }

  public void setArgumentTypes(ArgumentType... argumentTypes) {
    this.argumentTypes = argumentTypes;
  }

  public void addAction(AiAction aiAction) {
    actions.add(aiAction);
  }

  public void addPermission(AiPermission aiPermission) {
    permissions.add(aiPermission);
  }

  public String getUsage() {
    return usage;
  }

  public ArgumentType[] getArgumentTypes() {
    return argumentTypes;
  }
}
