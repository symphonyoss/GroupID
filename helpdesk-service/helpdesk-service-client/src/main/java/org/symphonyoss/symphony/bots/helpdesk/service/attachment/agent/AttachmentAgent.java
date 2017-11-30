package org.symphonyoss.symphony.bots.helpdesk.service.attachment.agent;

/**
 * Created by alexandre-silva-daitan on 30/11/17.
 * * Service for managing and getting attachments of agents with bot.
 */
public class AttachmentAgent {
  public enum AttachmentType {
    OPENED("OPENED"),
    APPROVED("APPROVED"),
    DENIED("DENIED");

    private String state;

    AttachmentType(String state) {
      this.state = state;
    }

    public String getState() {
      return state;
    }
  }
}
