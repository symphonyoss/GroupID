package org.symphonyoss.symphony.bots.helpdesk.messageproxy.model;

import org.symphonyoss.symphony.bots.utility.template.TemplateData;

/**
 * Created by nick.tarsillo on 11/7/17.
 *
 * Template data for claim entity data.
 */
public class ClaimEntityTemplateData extends TemplateData {
  enum ReplacementEnums implements TemplateEnums {
    TICKET_ID("TICKET_ID"),
    TICKET_STATE("TICKET_STATE"),
    USER_NAME("USER_NAME");

    private String replacement;

    ReplacementEnums(String replacement){this.replacement = replacement;}

    public String getReplacement() {
      return replacement;
    }
  }

  public ClaimEntityTemplateData(String ticketId, String ticketState, String username) {
    addData(ReplacementEnums.TICKET_ID.getReplacement(), ticketId);
    addData(ReplacementEnums.TICKET_STATE.getReplacement(), ticketState);
    addData(ReplacementEnums.USER_NAME.getReplacement(), username);
  }
}
