package org.symphonyoss.symphony.bots.helpdesk.messageproxy.model;

import org.symphonyoss.symphony.bots.utility.template.TemplateData;

/**
 * Created by nick.tarsillo on 9/27/17.
 * Template data for a message.
 */
public class ClaimMessageTemplateData extends TemplateData {
  enum ReplacementEnums implements TemplateEnums {
    MESSAGE("MESSAGE");

    private String replacement;

    ReplacementEnums(String replacement){this.replacement = replacement;}

    public String getReplacement() {
      return replacement;
    }
  }

  public ClaimMessageTemplateData(String message) {
    addData(ReplacementEnums.MESSAGE.getReplacement(), message);
  }
}
