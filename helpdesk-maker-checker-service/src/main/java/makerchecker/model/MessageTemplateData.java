package makerchecker.model;

import com.symphony.bots.helpdesk.util.template.TemplateData;

/**
 * Created by nick.tarsillo on 9/27/17.
 * Template data for a message.
 */
public class MessageTemplateData extends TemplateData{
  enum ReplacementEnums implements TemplateEnums {
    MESSAGE("MESSAGE");

    private String replacement;

    ReplacementEnums(String replacement){this.replacement = replacement;}

    public String getReplacement() {
      return replacement;
    }
  }

  public MessageTemplateData(String message) {
    addData(ReplacementEnums.MESSAGE.getReplacement(), message);
  }
}
