package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model;

import org.symphonyoss.symphony.bots.utility.template.TemplateData;

/**
 * Created by nick.tarsillo on 9/27/17.
 * Template data for a message.
 */
public class MakerCheckerMessageTemplateData extends TemplateData {
  enum ReplacementEnums implements TemplateEnums {
    MESSAGE("MESSAGE");

    private String replacement;

    ReplacementEnums(String replacement){this.replacement = replacement;}

    public String getReplacement() {
      return replacement;
    }
  }

  public MakerCheckerMessageTemplateData(String message) {
    addData(ReplacementEnums.MESSAGE.getReplacement(), message);
  }
}
