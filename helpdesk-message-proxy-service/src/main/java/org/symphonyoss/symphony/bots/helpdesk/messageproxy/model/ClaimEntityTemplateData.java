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
    USER_NAME("USER_NAME"),
    BOT_HOST("BOT_HOST"),
    SERVICE_HOST("SERVICE_HOST"),
    STREAM_ID("STREAM_ID"),
    HEADER("HEADER"),
    COMPANY("COMPANY"),
    QUESTION("QUESTION");

    private String replacement;

    ReplacementEnums(String replacement){this.replacement = replacement;}

    public String getReplacement() {
      return replacement;
    }
  }

  public ClaimEntityTemplateData(String ticketId, String ticketState, String username,
      String botHost, String serviceHost, String streamId, String header, String company,
      String question) {
    addData(ReplacementEnums.TICKET_ID.getReplacement(), ticketId);
    addData(ReplacementEnums.TICKET_STATE.getReplacement(), ticketState);
    addData(ReplacementEnums.USER_NAME.getReplacement(), username);
    addData(ReplacementEnums.BOT_HOST.getReplacement(), botHost);
    addData(ReplacementEnums.SERVICE_HOST.getReplacement(), serviceHost);
    addData(ReplacementEnums.STREAM_ID.getReplacement(), streamId);
    addData(ReplacementEnums.HEADER.getReplacement(), header);
    addData(ReplacementEnums.COMPANY.getReplacement(), company);
    addData(ReplacementEnums.QUESTION.getReplacement(), question);
  }
}
