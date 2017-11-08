package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model;

import org.symphonyoss.client.SymphonyClient;

/**
 * Created by nick.tarsillo on 11/7/17.
 */
public class MakerCheckerServiceSession {
  private SymphonyClient symphonyClient;
  private String messageTemplate;
  private String entityTemplate;

  public SymphonyClient getSymphonyClient() {
    return symphonyClient;
  }

  public void setSymphonyClient(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
  }

  public String getMessageTemplate() {
    return messageTemplate;
  }

  public void setMessageTemplate(String messageTemplate) {
    this.messageTemplate = messageTemplate;
  }

  public String getEntityTemplate() {
    return entityTemplate;
  }

  public void setEntityTemplate(String entityTemplate) {
    this.entityTemplate = entityTemplate;
  }
}
