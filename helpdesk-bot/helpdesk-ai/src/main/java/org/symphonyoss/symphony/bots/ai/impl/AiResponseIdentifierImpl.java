package org.symphonyoss.symphony.bots.ai.impl;

import org.symphonyoss.symphony.bots.ai.AiResponseIdentifier;

/**
 * Created by nick.tarsillo on 8/23/17.
 */
public class AiResponseIdentifierImpl implements AiResponseIdentifier {
  private String name;
  private String id;

  public AiResponseIdentifierImpl(String id) {
    this.id = id;
  }

  public AiResponseIdentifierImpl(String name, String id) {
    this.name = name;
    this.id = id;
  }

  @Override
  public String getIdentityName() {
    return name;
  }

  @Override
  public String getResponseIdentifier() {
    return id;
  }
}
