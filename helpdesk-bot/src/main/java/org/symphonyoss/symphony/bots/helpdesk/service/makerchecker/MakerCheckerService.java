package org.symphonyoss.symphony.bots.helpdesk.service.makerchecker;

import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;
import org.symphonyoss.symphony.bots.helpdesk.model.HelpDeskBotSession;
import org.symphonyoss.symphony.bots.helpdesk.model.MakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.model.Checker;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.model.EntityTemplateData;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.model.MessageTemplateData;
import org.symphonyoss.symphony.bots.helpdesk.service.messageproxy.model.ProxyAiMessage;
import org.symphonyoss.symphony.bots.helpdesk.util.template.MessageTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private Set<Checker> checkerSet = new HashSet<>();
  private HelpDeskBotSession helpDeskBotSession;
  private String messageTemplate;
  private String entityTemplate;

  public MakerCheckerService(HelpDeskBotSession helpDeskSession) {
    helpDeskBotSession = helpDeskSession;
    messageTemplate = helpDeskBotSession.getHelpDeskBotConfig().getMakerCheckerMessageTemplate();
    entityTemplate = helpDeskBotSession.getHelpDeskBotConfig().getMakerCheckerEntityTemplate();
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
  public boolean allChecksPass(SymphonyAiMessage symMessage) {
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
          helpDeskBotSession.getSymphonyClient().getMessagesClient().getMessagesFromStream(
              stream, Long.parseLong(makerCheckerMessage.getTimeStamp()) - 1, 0, 10);

      SymMessage match = null;
      for(SymMessage symMessage : symMessageList) {
        if(symMessage.getId().equals(makerCheckerMessage.getMessageId())) {
          match = symMessage;
        }
      }

      stream.setId(makerCheckerMessage.getProxyToStreamId());
      helpDeskBotSession.getSymphonyClient().getMessagesClient().sendMessage(stream, match);
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
  public SymphonyAiMessage getMakerCheckerMessage(ProxyAiMessage symMessage) {
    String message = "";
    for(Checker checker: checkerSet) {
      if(!checker.check(symMessage)) {
        message = checker.getCheckFailureMessage();
      }
    }

    SymMessage checkerMessage = new SymMessage();
    checkerMessage.setFormat(SymMessage.Format.MESSAGEML);

    MessageTemplate messageTemplate = new MessageTemplate(this.messageTemplate);
    MessageTemplate entityTemplate = new MessageTemplate(this.entityTemplate);
    symMessage.setAiMessage(messageTemplate.buildFromData(new MessageTemplateData(message)));
    symMessage.setEntityData(entityTemplate.buildFromData(new EntityTemplateData(symMessage)));

    return symMessage;
  }

  /**
   * Sets a new message template, for the maker checker message.
   * @param newTemplate the new template.
   */
  public void setMessageTemplate(String newTemplate) {
    messageTemplate = newTemplate;
    helpDeskBotSession.getHelpDeskBotConfig().setMakerCheckerMessageTemplate(newTemplate);
  }

  /**
   * Sets a new entity data template, for the maker checker message.
   * @param newTemplate
   */
  public void setEntityTemplate(String newTemplate) {
    entityTemplate = newTemplate;
    helpDeskBotSession.getHelpDeskBotConfig().setMakerCheckerEntityTemplate(newTemplate);
  }
}
