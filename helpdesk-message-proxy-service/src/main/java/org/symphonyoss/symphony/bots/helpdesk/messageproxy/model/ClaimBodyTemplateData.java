package org.symphonyoss.symphony.bots.helpdesk.messageproxy.model;

import org.symphonyoss.symphony.bots.utility.template.TemplateData;

/**
 * Created by nick.tarsillo on 11/20/17.
 */
public class ClaimBodyTemplateData extends TemplateData {
  enum ReplacementEnums implements TemplateEnums {
    COMPANY("COMPANY"),
    USER_NAME("USER_NAME"),
    QUESTION("QUESTION");

    private String replacement;

    ReplacementEnums(String replacement){this.replacement = replacement;}

    public String getReplacement() {
      return replacement;
    }
  }

  public ClaimBodyTemplateData(String company, String name, String question) {
    addData(ReplacementEnums.COMPANY.getReplacement(), company);
    addData(ReplacementEnums.USER_NAME.getReplacement(), name);
    addData(ReplacementEnums.QUESTION.getReplacement(), question);
  }
}
