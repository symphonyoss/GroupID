package org.symphonyoss.symphony.bots.helpdesk.makerchecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.Checker;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerEntityTemplateData;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerMessageTemplateData;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerServiceSession;
import org.symphonyoss.symphony.bots.utility.template.MessageTemplate;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.pod.model.Stream;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.BadRequestException;

/**
 * Created by nick.tarsillo on 9/26/17.
 * Used in conjunction with the Symphony AI.
 * Validates a messages, and requests validation from another user when checks fail.
 */
public class MakerCheckerService {
  private static final Logger LOG = LoggerFactory.getLogger(MakerCheckerService.class);
  private static final String MESSAGE_NOT_FOUND = "Message with id %s could not be found.";
  private static final String STREAM_NOT_FOUND= "The stream %s could not be found.";

  private Set<Checker> checkerSet = new HashSet<>();

  private MakerCheckerServiceSession session;

  public MakerCheckerService(MakerCheckerServiceSession session) {
    this.session = session;
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

  /**
   * Accept a maker checker message.
   * Find the message.
   * Send the message to client stream.
   * @param makerCheckerMessage the maker checker message
   */
  public void acceptMakerCheckerMessage(MakerCheckerMessage makerCheckerMessage) {
    Stream stream = new Stream();
    stream.setId(makerCheckerMessage.getStreamId());
    try {
      List<SymMessage> symMessageList =
          session.getSymphonyClient().getMessagesClient().getMessagesFromStream(
              stream, Long.parseLong(makerCheckerMessage.getTimeStamp()) - 1, 0, 10);

      SymMessage match = null;
      for(SymMessage symMessage : symMessageList) {
        if(symMessage.getId().equals(makerCheckerMessage.getMessageId())) {
          match = symMessage;
        }
      }

      if(match == null) {
        throw new BadRequestException(String.format(MESSAGE_NOT_FOUND, makerCheckerMessage.getMessageId()));
      }

      for(String streamId: makerCheckerMessage.getProxyToStreamIds()) {
        stream.setId(streamId);
        session.getSymphonyClient().getMessagesClient().sendMessage(stream, match);
      }
    } catch (MessagesException e) {
      LOG.warn("Error accepting maker checker message: ", e);
      throw new BadRequestException(String.format(STREAM_NOT_FOUND, stream.getId()));
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
    checkerMessage.setStream(symMessage.getStream());
    checkerMessage.setStreamId(symMessage.getStreamId());

    MessageTemplate messageTemplate = new MessageTemplate(session.getMessageTemplate());
    MessageTemplate entityTemplate = new MessageTemplate(session.getEntityTemplate());
    checkerMessage.setMessage(messageTemplate.buildFromData(new MakerCheckerMessageTemplateData(message)));
    checkerMessage.setEntityData(entityTemplate.buildFromData(new MakerCheckerEntityTemplateData(symMessage, proxyToIds)));

    try {
      session.getSymphonyClient().getMessagesClient().sendMessage(
          checkerMessage.getStream(), checkerMessage);
    } catch (MessagesException e) {
      LOG.error("Failed to send maker checker message: ", e);
    }
  }

  /**
   * Sets a new message template, for the maker checker message.
   * @param newTemplate the new template.
   */
  public void setMessageTemplate(String newTemplate) {
    session.setMessageTemplate(newTemplate);
  }

  /**
   * Sets a new entity data template, for the maker checker message.
   * @param newTemplate
   */
  public void setEntityTemplate(String newTemplate) {
    session.setEntityTemplate(newTemplate);
  }
}
