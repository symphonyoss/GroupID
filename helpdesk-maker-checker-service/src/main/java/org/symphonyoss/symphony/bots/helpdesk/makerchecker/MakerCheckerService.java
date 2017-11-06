package org.symphonyoss.symphony.bots.helpdesk.makerchecker;

import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.Checker;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.EntityTemplateData;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MessageTemplateData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.bots.utility.template.MessageTemplate;
import org.symphonyoss.symphony.clients.MessagesClient;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.pod.model.Stream;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.InternalServerErrorException;

/**
 * Created by nick.tarsillo on 9/26/17.
 * Used in conjunction with the Symphony AI.
 * Validates a messages, and requests validation from another user when checks fail.
 */
public class MakerCheckerService {
  private static final Logger LOG = LoggerFactory.getLogger(MakerCheckerService.class);

  private MessagesClient messagesClient;
  private Set<Checker> checkerSet = new HashSet<>();
  private String messageTemplate;
  private String entityTemplate;

  public MakerCheckerService(String messageTemplate, String entityTemplate) {
    this.messageTemplate = messageTemplate;
    this.entityTemplate = entityTemplate;
  }

  /**
   * Add a check to the maker checker service.
   * @param checker the check to add.
   */
  public void addCheck(Checker checker) {
    checkerSet.add(checker);
  }

  /**
   * Validates that all checks pass.
   * @param symMessage the message to validate.
   * @return if all the checks passed.
   */
  public boolean allChecksPass(SymMessage symMessage) {
    for(Checker checker: checkerSet) {
      if(!checker.check(symMessage)) {
        return false;
      }
    }

    return true;
  }

  public void acceptMakerCheckerMessage(MakerCheckerMessage makerCheckerMessage) {
    Stream stream = new Stream();
    stream.setId(makerCheckerMessage.getStreamId());
    try {
      List<SymMessage> symMessageList =
          messagesClient.getMessagesFromStream(
              stream, Long.parseLong(makerCheckerMessage.getTimeStamp()) - 1, 0, 10);

      SymMessage match = null;
      for(SymMessage symMessage : symMessageList) {
        if(symMessage.getId().equals(makerCheckerMessage.getMessageId())) {
          match = symMessage;
        }
      }

      for(String streamId: makerCheckerMessage.getProxyToStreamIds()) {
        stream.setId(streamId);
        messagesClient.sendMessage(stream, match);
      }
    } catch (MessagesException e) {
      LOG.error("Error accepting maker checker message: ", e);
      throw new InternalServerErrorException();
    }
  }

  /**
   * If all checks did not pass, get a message to send back to user who sent the message.
   * This message will request validation from another user.
   * @param symMessage the message to base the maker checker message on.
   * @return the maker checker message.
   */
  public void sendMakerCheckerMessage(SymMessage symMessage, Set<String> proxyToIds) {
    String message = "";
    for(Checker checker: checkerSet) {
      if(!checker.check(symMessage)) {
        message = checker.getCheckFailureMessage();
      }
    }

    SymMessage checkerMessage = new SymMessage();
    checkerMessage.setFormat(SymMessage.Format.MESSAGEML);
    checkerMessage.setStream(symMessage.getStream());
    checkerMessage.setStreamId(symMessage.getStreamId());

    MessageTemplate messageTemplate = new MessageTemplate(this.messageTemplate);
    MessageTemplate entityTemplate = new MessageTemplate(this.entityTemplate);
    checkerMessage.setMessage(messageTemplate.buildFromData(new MessageTemplateData(message)));
    checkerMessage.setEntityData(entityTemplate.buildFromData(new EntityTemplateData(symMessage, proxyToIds)));

    try {
      messagesClient.sendMessage(checkerMessage.getStream(), checkerMessage);
    } catch (MessagesException e) {
      LOG.error("Failed to send maker checker message: ", e);
    }
  }

  /**
   * Sets a new message template, for the maker checker message.
   * @param newTemplate the new template.
   */
  public void setMessageTemplate(String newTemplate) {
    messageTemplate = newTemplate;
  }

  /**
   * Sets a new entity data template, for the maker checker message.
   * @param newTemplate
   */
  public void setEntityTemplate(String newTemplate) {
    entityTemplate = newTemplate;
  }
}
