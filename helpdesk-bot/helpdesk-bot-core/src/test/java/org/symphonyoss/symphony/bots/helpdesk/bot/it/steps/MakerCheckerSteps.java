package org.symphonyoss.symphony.bots.helpdesk.bot.it.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParseException;
import org.apache.commons.lang3.StringUtils;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.springframework.stereotype.Component;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.symphony.bots.helpdesk.bot.health.HealthCheckFailedException;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.exception.MessageNotFoundException;
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

  private static final String MAKERCHECKER_NODE = "makerchecker";

  private static final String WHITE_SPACE = " ";

  private static final String APPROVED = "APPROVED";

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
    initialTime = System.currentTimeMillis();
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
      throw new MessageNotFoundException("Message not found");
    }

    String url = getValueFromEntityData(message.get().getEntityData(), APPROVE_URL);

    makerCheckerHelper.actionAttachment(url, userAgent.getId());
  }

  @When("$agent agent try approves the attachment")
  public void tryApproveAttachment(String agent)
      throws MessagesException, HealthCheckFailedException, MalformedURLException {
    SymUser userAgent = userHelper.getUser(agent.toUpperCase());
    Optional<SymMessage> message = messageHelper.getLatestTicketMessage(userAgent, initialTime);

    if (!message.isPresent()) {
      throw new MessageNotFoundException("Message not found");
    }

    String url = getValueFromEntityData(message.get().getEntityData(), APPROVE_URL);

    assertNull(url);
    assertEquals(APPROVED, getValueFromEntityData(message.get().getEntityData(), STATE));
  }

  @When("$agent agent deny the attachment")
  public void denyAttachment(String agent)
      throws MessagesException, HealthCheckFailedException, MalformedURLException {
    SymUser userAgent = userHelper.getUser(agent.toUpperCase());
    Optional<SymMessage> message = messageHelper.getLatestTicketMessage(userAgent, initialTime);

    if (!message.isPresent()) {
      throw new MessageNotFoundException("Message not found");
    }

    String url = getValueFromEntityData(message.get().getEntityData(), DENY_URL);

    makerCheckerHelper.actionAttachment(url, userAgent.getId());
  }

  @Then("$agent can verify the attachment $attachment was approved by $agent2")
  public void verifyApprovedMakerChecker(String agent, String attachment, String agent2)
      throws MessagesException, HealthCheckFailedException {
    SymUser userAgent = userHelper.getUser(agent.toUpperCase());
    SymUser userAgentApprover = userHelper.getUser(agent2.toUpperCase());
    Optional<SymMessage> message = messageHelper.getLatestTicketMessage(userAgent, initialTime);


    String messageAction = userAgentApprover.getDisplayName() + WHITE_SPACE + "approved" + WHITE_SPACE + attachment
          + " attachment. It has been delivered to the client(s).";

    assertEquals(messageAction , message.get().getMessageText().trim());
  }

  @Then("$agent can verify the attachment $attachment is $state")
  public void verifyStateOfMakerChecker(String agent, String attachment, String state)
      throws MessagesException, HealthCheckFailedException {
    SymUser userAgent = userHelper.getUser(agent.toUpperCase());
    Optional<SymMessage> message = messageHelper.getLatestTicketMessage(userAgent, initialTime);

    assertEquals(state.toUpperCase(), getValueFromEntityData(message.get().getEntityData(), STATE));

    String messageAction = null;
    if (state.equals("approved")) {
      messageAction = userAgent.getDisplayName() + WHITE_SPACE + state + WHITE_SPACE + attachment
          + " attachment. It has been delivered to the client(s).";
    } else {
      messageAction = userAgent.getDisplayName() + WHITE_SPACE + state + WHITE_SPACE + attachment
          + " attachment. It has not been delivered to the client(s).";
    }

    assertEquals(messageAction , message.get().getMessageText().trim());
  }

  /**
   * Method responsible for parsing the input entity data and retrieving the value for the given
   * key relative to the maker-checker JSON tree root
   *
   * @param entityData Json with message of makerchecker
   * @param key key of node in the node makerchecker
   * @return String value of node
   */
  private String getValueFromEntityData(String entityData, String key) {
    JsonNode node = null;

    try {
      node = MAPPER.readTree(entityData);
    } catch (IOException e) {
      throw new JsonParseException("Failed to read response entity.");
    }

    return node.get(MAKERCHECKER_NODE).get(key) != null ? node.get(MAKERCHECKER_NODE).get(key).asText(StringUtils.EMPTY) : null;
  }

}
