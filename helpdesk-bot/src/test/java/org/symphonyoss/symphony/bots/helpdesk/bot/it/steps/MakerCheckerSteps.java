package org.symphonyoss.symphony.bots.helpdesk.bot.it.steps;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.springframework.stereotype.Component;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.exception.TicketRoomNotFoundException;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers.MakerCheckerHelper;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers.MessageHelper;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers.UserHelper;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Class responsible for managing maker checker steps.
 * <p>
 * Created by crepache on 05/03/18.
 */
@Component
public class MakerCheckerSteps {

  private Long initialTime = 0L;

  private final MessageHelper messageHelper;

  private final MakerCheckerHelper makerCheckerHelper;

  private final UserHelper userHelper;

  private static final String ATTACHMENTS_DIR = "attachment/";

  public MakerCheckerSteps(MessageHelper messageHelper, MakerCheckerHelper makerCheckerHelper, UserHelper userHelper) {
    this.messageHelper = messageHelper;
    this.makerCheckerHelper = makerCheckerHelper;
    this.userHelper = userHelper;
  }

  @When("$agent agent sends an attachment $attachment")
  public void sendAttachment(String agent, String attachment)
      throws SymException, URISyntaxException {
    URL url = this.getClass().getClassLoader().getResource(ATTACHMENTS_DIR + attachment);

    File fileAttachment = new File(url.toURI());

    List<SymAttachmentInfo> attachmentsList = new ArrayList<>();
    SymAttachmentInfo symAttachmentInfo = new SymAttachmentInfo();
    symAttachmentInfo.setId(UUID.randomUUID().toString());
    symAttachmentInfo.setName(fileAttachment.getName());
    attachmentsList.add(symAttachmentInfo);

    SymMessage message = new SymMessage();
    message.setMessageText("Check my attachment, please ?");
    message.setAttachment(fileAttachment);
    message.setAttachments(attachmentsList);

    messageHelper.sendAgentMessage(agent, message);
  }

  @When("$agent agent approve attachment $attachment")
  public void approveAttachment(String agent, String attachment)
      throws MessagesException, StreamsException {
    SymUser userAgent = userHelper.getUser(agent);
    Optional<SymMessage> message = messageHelper.getLatestTicketMessage(userAgent, initialTime);

    if (!message.isPresent()) {
      throw new TicketRoomNotFoundException("Message not found");
    }

    if (message.get().getAttachment().getName().equals(attachment)) {
      makerCheckerHelper.approveAttachment(message.get().getId(), userAgent.getId());
    }

  }

  @When("$agent agent deny attachment $attachment")
  public void denyAttachment(String agent, String attachment)
      throws MessagesException, StreamsException {
    SymUser userAgent = userHelper.getUser(agent);
    Optional<SymMessage> message = messageHelper.getLatestTicketMessage(userAgent, initialTime);

    if (!message.isPresent()) {
      throw new TicketRoomNotFoundException("Message not found");
    }

    if (message.get().getAttachment().getName().equals(attachment)) {
      makerCheckerHelper.denyAttachment(message.get().getId(), userAgent.getId());
    }
  }

  @Then("bot can verify attachment $attachment is $state")
  public void verifyStateOfMakerChecker(String attachment, String state) {

  }

}
