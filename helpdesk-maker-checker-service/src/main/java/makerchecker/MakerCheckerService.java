package makerchecker;

import com.symphony.bots.ai.impl.SymphonyAiMessage;
import com.symphony.bots.helpdesk.config.BotConfig;
import com.symphony.bots.helpdesk.model.HelpDeskBotSession;
import makerchecker.model.Checker;
import makerchecker.model.EntityTemplateData;
import makerchecker.model.MessageTemplateData;
import com.symphony.bots.helpdesk.util.file.FileUtil;
import com.symphony.bots.helpdesk.util.template.MessageTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nick.tarsillo on 9/26/17.
 * Used in conjunction with the Symphony AI.
 * Validates a messages, and requests validation from another user when checks fail.
 */
public class MakerCheckerService {
  private static final Logger LOG = LoggerFactory.getLogger(MakerCheckerService.class);

  private Set<Checker> checkerSet = new HashSet<>();
  private String messageTemplateFileLocation;
  private String entityTemplateFileLocation;

  public MakerCheckerService(HelpDeskBotSession helpDeskSession){
    messageTemplateFileLocation =
        System.getProperty(BotConfig.TEMPLATE_DIR) +
        helpDeskSession.getGroupId() + " " +
        System.getProperty(BotConfig.MAKERCHECKER_MESSAGE_TEMPLATE_NAME);
    entityTemplateFileLocation =
        System.getProperty(BotConfig.TEMPLATE_DIR) +
            helpDeskSession.getGroupId() + " " +
            System.getProperty(BotConfig.MAKERCHECKER_ENTITY_TEMPLATE_NAME);
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

  /**
   * If all checks did not pass, get a message to send back to user who sent the message.
   * This message will request validation from another user.
   * @param symMessage the message to base the maker checker message on.
   * @return the maker checker message.
   */
  public SymphonyAiMessage getMakerCheckerMessage(SymphonyAiMessage symMessage) {
    String message = "";
    for(Checker checker: checkerSet) {
      if(!checker.check(symMessage)) {
        message = checker.getCheckFailureMessage();
      }
    }

    SymMessage checkerMessage = new SymMessage();
    checkerMessage.setFormat(SymMessage.Format.MESSAGEML);
    try {
      MessageTemplate messageTemplate = new MessageTemplate(FileUtil.readFile(messageTemplateFileLocation));
      MessageTemplate entityTemplate = new MessageTemplate(FileUtil.readFile(entityTemplateFileLocation));
      symMessage.setAiMessage(messageTemplate.buildFromData(new MessageTemplateData(message)));
      symMessage.setEntityData(entityTemplate.buildFromData(new EntityTemplateData(symMessage.getFromUserId().toString())));
    } catch (IOException e) {
      LOG.error("failed to create maker checker message:", e);
    }

    return symMessage;
  }

  /**
   * Sets a new message template, for the maker checker message.
   * @param newTemplate the new template.
   */
  public void setMessageTemplate(String newTemplate) {
    try {
      FileUtil.writeFile(newTemplate, messageTemplateFileLocation);
    } catch (FileNotFoundException | UnsupportedEncodingException e) {
      LOG.error("Changing message template failed: ", e);
    }
  }

  /**
   * Sets a new entity data template, for the maker checker message.
   * @param newTemplate
   */
  public void setEntityTemplate(String newTemplate) {
    try {
      FileUtil.writeFile(newTemplate, entityTemplateFileLocation);
    } catch (FileNotFoundException | UnsupportedEncodingException e) {
      LOG.error("Changing entity template failed: ", e);
    }
  }
}
