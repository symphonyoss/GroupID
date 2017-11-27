package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.bots.utility.template.TemplateData;

/**
 * Created by nick.tarsillo on 11/24/17.
 */
public class AttachmentEntityTemplateData extends TemplateData {
  private static Logger LOG = LoggerFactory.getLogger(AttachmentEntityTemplateData.class);

  enum ReplacementEnums implements TemplateData.TemplateEnums {
    TYPE("TYPE"),
    ATTACHMENT_ID("ATTACHMENT_ID");

    private String replacement;

    ReplacementEnums(String replacement){this.replacement = replacement;}

    public String getReplacement() {
      return replacement;
    }
  }

  public AttachmentEntityTemplateData(String attachmentId, String type) {
    addData(ReplacementEnums.TYPE.getReplacement(), type);
    addData(ReplacementEnums.ATTACHMENT_ID.getReplacement(), attachmentId);
  }
}
