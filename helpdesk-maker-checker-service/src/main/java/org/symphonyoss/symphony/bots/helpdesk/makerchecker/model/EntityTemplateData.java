package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.bots.utility.template.TemplateData;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.Set;

/**
 * Created by nick.tarsillo on 9/27/17.
 * Template data for an entity.
 */
public class EntityTemplateData extends TemplateData {
  private static Logger LOG = LoggerFactory.getLogger(EntityTemplateData.class);
  private static ObjectMapper MAPPER = new ObjectMapper();

  enum ReplacementEnums implements TemplateData.TemplateEnums {
    UID("UID"),
    STREAM_ID("STREAM_ID"),
    PROXY_TO_STREAM_ID("PROXY_TO_STREAM_ID"),
    TIMESTAMP("TIMESTAMP"),
    MESSAGE_ID("MESSAGE_ID");

    private String replacement;

    ReplacementEnums(String replacement){this.replacement = replacement;}

    public String getReplacement() {
      return replacement;
    }
  }

  public EntityTemplateData(SymMessage symMessage, Set<String> proxyToIds) {
    addData(ReplacementEnums.UID.getReplacement(), symMessage.getFromUserId().toString());
    addData(ReplacementEnums.MESSAGE_ID.getReplacement(), symMessage.getId());
    addData(ReplacementEnums.STREAM_ID.getReplacement(), symMessage.getStreamId());
    addData(ReplacementEnums.TIMESTAMP.getReplacement(), symMessage.getTimestamp());
    try {
      addData(ReplacementEnums.PROXY_TO_STREAM_ID.getReplacement(),
          MAPPER.writeValueAsString(proxyToIds));
    } catch (JsonProcessingException e) {
      LOG.error("Could not map proxy list for maker checker entity data: ", e);
    }
  }
}
