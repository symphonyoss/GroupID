package org.symphonyoss.symphony.bots.helpdesk.makerchecker.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.bots.utility.message.EntityBuilder;
import org.symphonyoss.symphony.bots.utility.message.SymMessageBuilder;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsanchez on 01/12/17.
 */
public class MakerCheckerMessageBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(MakerCheckerMessageBuilder.class);

  private static final String BASE_EVENT = "com.symphony.bots.helpdesk.event.makerchecker";

  private static final String VERSION = "1.0";

  private static final String MAKER_CHECKER_MESSAGE_TEMPLATE = "makerCheckerMessage.xml";

  private static String message;

  private final SymMessageBuilder messageBuilder;

  private String botHost;

  private String serviceHost;

  private Long makerId;

  private String streamId;

  private List<String> proxyToStreamIds = new ArrayList<>();

  private Long timestamp;

  private String messageId;

  private String groupId;

  private String makerCheckerId;

  private String attachmentId;

  public MakerCheckerMessageBuilder() {
    if (StringUtils.isEmpty(message)) {
      message = parseTemplate();
    }

    this.messageBuilder = SymMessageBuilder.message(message);
  }

  private String parseTemplate() {
    StringBuilder message = new StringBuilder();
    InputStream resource = getClass().getClassLoader().getResourceAsStream(MAKER_CHECKER_MESSAGE_TEMPLATE);

    try (BufferedReader buffer = new BufferedReader(new InputStreamReader(resource))) {
      buffer.lines().forEach(message::append);
    } catch (IOException e) {
      LOGGER.error("Fail to parse maker checker message template");
    }

    return message.toString();
  }

  public MakerCheckerMessageBuilder botHost(String host) {
    this.botHost = host;
    return this;
  }

  public MakerCheckerMessageBuilder serviceHost(String host) {
    this.serviceHost = host;
    return this;
  }

  public MakerCheckerMessageBuilder makerId(Long makerId) {
    this.makerId = makerId;
    return this;
  }

  public MakerCheckerMessageBuilder streamId(String streamId) {
    this.streamId = streamId;
    return this;
  }

  public MakerCheckerMessageBuilder addProxyToStreamId(String streamId) {
    this.proxyToStreamIds.add(streamId);
    return this;
  }

  public MakerCheckerMessageBuilder timestamp(Long timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  public MakerCheckerMessageBuilder messageId(String messageId) {
    this.messageId = messageId;
    return this;
  }

  public MakerCheckerMessageBuilder groupId(String groupId) {
    this.groupId = groupId;
    return this;
  }

  public MakerCheckerMessageBuilder makerCheckerId(String makerCheckerId) {
    this.makerCheckerId = makerCheckerId;
    return this;
  }

  public MakerCheckerMessageBuilder attachmentId(String attachmentId) {
    this.attachmentId = attachmentId;
    return this;
  }

  public SymMessage build() {
    if (messageBuilder == null) {
      return null;
    }

    try {
      EntityBuilder bodyBuilder = EntityBuilder.createEntity(BASE_EVENT, VERSION);

      String attachmentUrl = String.format("%s/v1/makerchecker/%s", serviceHost, makerCheckerId);
      bodyBuilder.addField("attachmentUrl", attachmentUrl);

      String approveUrl = String.format("%s/v1/makerchecker/approve", botHost);
      bodyBuilder.addField("approveUrl", approveUrl);

      String denyUrl = String.format("%s/v1/makerchecker/deny", botHost);
      bodyBuilder.addField("denyUrl", denyUrl);

      bodyBuilder.addField("makerId", makerId);
      bodyBuilder.addField("streamId", streamId);
      bodyBuilder.addField("proxyToStreamIds", proxyToStreamIds);
      bodyBuilder.addField("timestamp", timestamp);
      bodyBuilder.addField("messageId", messageId);
      bodyBuilder.addField("groupId", groupId);
      bodyBuilder.addField("makerCheckerId", makerCheckerId);
      bodyBuilder.addField("attachmentId", attachmentId);

      EntityBuilder builder = EntityBuilder.createEntity();
      builder.addField("makerchecker", bodyBuilder.toObject());

      String entityData = builder.build();

      return messageBuilder.entityData(entityData).build();
    } catch (JsonProcessingException e) {
      LOGGER.error("Fail to create entity data");
      return null;
    }
  }

}
