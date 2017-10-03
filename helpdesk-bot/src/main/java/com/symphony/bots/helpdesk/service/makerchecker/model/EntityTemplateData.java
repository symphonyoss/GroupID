package com.symphony.bots.helpdesk.service.makerchecker.model;

import com.symphony.bots.helpdesk.util.template.TemplateData;

/**
 * Created by nick.tarsillo on 9/27/17.
 * Template data for an entity.
 */
public class EntityTemplateData extends TemplateData {
  enum ReplacementEnums implements TemplateData.TemplateEnums {
    UID("UID");

    private String replacement;

    ReplacementEnums(String replacement){this.replacement = replacement;}

    public String getReplacement() {
      return replacement;
    }
  }

  public EntityTemplateData(String uid) {
    addData(EntityTemplateData.ReplacementEnums.UID.getReplacement(), uid);
  }
}
