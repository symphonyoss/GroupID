package org.symphonyoss.symphony.bots.helpdesk.bot.it.steps;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.springframework.stereotype.Component;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.symphony.bots.helpdesk.bot.health.HealthCheckFailedException;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.exception.TicketRoomNotFoundException;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers.MakerCheckerHelper;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers.MessageHelper;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers.UserHelper;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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

  private static final String ATTACHMENTS_DIR = "attachment/";

  private static final String APPROVE_URL = "approveUrl";

  private static final String DENY_URL = "denyUrl";

  private static final String STATE = "state";

  private Long initialTime = 0L;

  private final MessageHelper messageHelper;

  private final MakerCheckerHelper makerCheckerHelper;

  private final UserHelper userHelper;

  private static final ObjectMapper MAPPER = new ObjectMapper();

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

  @When("$agent agent approve the attachment")
  public void approveAttachment(String agent)
      throws MessagesException, HealthCheckFailedException, MalformedURLException {
    SymUser userAgent = userHelper.getUser(agent.toUpperCase());
    Optional<SymMessage> message = messageHelper.getLatestTicketMessage(userAgent, initialTime);

    if (!message.isPresent()) {
      throw new TicketRoomNotFoundException("Message not found");
    }

    String url = getValueFromParam(message.get().getEntityData(), APPROVE_URL);

    makerCheckerHelper.actionAttachment(url, userAgent.getId());
  }

  @When("$agent agent deny the attachment")
  public void denyAttachment(String agent)
      throws MessagesException, HealthCheckFailedException, MalformedURLException {
    SymUser userAgent = userHelper.getUser(agent.toUpperCase());
    Optional<SymMessage> message = messageHelper.getLatestTicketMessage(userAgent, initialTime);

    if (!message.isPresent()) {
      throw new TicketRoomNotFoundException("Message not found");
    }

    String url = getValueFromParam(message.get().getEntityData(), DENY_URL);

    makerCheckerHelper.actionAttachment(url, userAgent.getId());
  }

  @Then("$agent can verify the attachment is $state")
  public void verifyStateOfMakerChecker(String agent, String state)
      throws MessagesException, HealthCheckFailedException {
    SymUser userAgent = userHelper.getUser(agent.toUpperCase());
    Optional<SymMessage> message = messageHelper.getLatestTicketMessage(userAgent, initialTime);

    assertEquals(state.toUpperCase(), getValueFromParam(message.get().getEntityData(), STATE));
  }

  private String getValueFromParam(String entityDate, String key) throws HealthCheckFailedException {
    JsonNode node = null;

    try {
      node = MAPPER.readTree(entityDate);
    } catch (IOException e) {
      throw new HealthCheckFailedException("Failed to read response entity.");
    }

    return node.get("makerchecker").get(key).asText();
  }

}
