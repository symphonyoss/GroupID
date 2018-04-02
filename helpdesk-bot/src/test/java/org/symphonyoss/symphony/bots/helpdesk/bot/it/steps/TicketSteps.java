package org.symphonyoss.symphony.bots.helpdesk.bot.it.steps;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.springframework.stereotype.Component;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers.MessageHelper;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers.StreamHelper;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers.TicketHelper;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers.UserHelper;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.TicketResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.MemberInfo;

import java.util.List;
import java.util.Optional;

/**
 * Class responsible for managing ticket steps.
 * <p>
 * Created by rsanchez on 23/02/18.
 */
@Component
public class TicketSteps {

  public static final String PERSONAL_QUESTION = "Hi bot, what are you doing?";
  public static final String HELP_QUESTION = "Hi bot, can you help me?";
  public static final String PERSONAL = "personal";
  private final MessageHelper messageHelper;

  private final TicketHelper ticketHelper;

  private final UserHelper userHelper;

  private final HelpDeskBotConfig helpDeskBotConfig;

  private final StreamHelper streamHelper;

  private static final String AGENT_TICKET_CREATION_PERSONAL_MESSAGE =
      "<div data-format=\"PresentationML"
          + "\" data-version=\"V4\">    <div class=\"entity\" data-entity-id=\"helpdesk\">        "
          + "<div class=\"card barStyle\">            <div class=\"cardHeader\">                "
          + "<span><b>Equities Desk Bot</b></span>            </div>            <div "
          + "class=\"cardBody\">                <span><b>Company:</b> Symphony Engineering "
          + "Services "
          + "Dev 5</span><br/>                <span><b>Customer:</b> "
          + "%s</span><br/>                "
          + "<span><b>Question:</b>  Hi bot, what are you doing?</span>            </div>        "
          + "</div>    </div></div>";

  private static final String TICKET_CLAIMED_BY_AGENT_MESSAGE = "<div data-format"
      + "=\"PresentationML\" data-version=\"V4\">    <div class=\"entity\" "
      + "data-entity-id=\"helpdesk\">        <div class=\"card barStyle\">            <div "
      + "class=\"cardBody\">                <span>Ticket <b>%s</b> has been claimed      "
      + "              by <b>%s</b></span>            "
      + "</div>        </div>    </div></div>";

  private static final String AGENT_TICKET_CREATION_HELP_MESSAGE =
      "<div data-format=\"PresentationML"
          + "\" data-version=\"V4\">    <div class=\"entity\" data-entity-id=\"helpdesk\">        "
          + "<div class=\"card barStyle\">            <div class=\"cardHeader\">                "
          + "<span><b>Equities Desk Bot</b></span>            </div>            <div "
          + "class=\"cardBody\">                <span><b>Company:</b> Symphony Engineering "
          + "Services "
          + "Dev 5</span><br/>                <span><b>Customer:</b> "
          + "%s</span><br/>                "
          + "<span><b>Question:</b>  Hi bot, can you help me?</span>            </div>        "
          + "</div>    </div></div>";

  private static final String CLIENT_TICKET_CREATION_MESSAGE = "<div data-format=\"PresentationML"
      + "\" data-version=\"V4\">%s</div>";

  private static final String MESSAGE_HISTORY_0 = "<div data-format=\"PresentationML\" "
      + "data-version=\"V4\">Use <span class=\"entity\" "
      + "data-entity-id=\"mention1\">@%s</span> "
      + "<b>Close</b>  to close the ticket upon ticket resolution.</div>";

  private static final String MESSAGE_HISTORY_1 = "<div data-format=\"PresentationML\" "
      + "data-version=\"V4\"><b>%s</b>:  Hi bot, what are you doing?</div>";

  private static final String MESSAGE_HISTORY_2 =
      "<div data-format=\"PresentationML\" data-version=\"V4\">Hi customer, I'm fine.</div>";

  private static final String MESSAGE_HISTORY_3 = "<div data-format=\"PresentationML\" "
      + "data-version=\"V4\"><b>%s</b>:  Hi bot, can you help me?</div>";

  private static final String TICKET_CLAIMED_MESSAGE = "<div data-format=\"PresentationML\" "
      + "data-version=\"V4\">%s</div>";

  private static final String AGENT_RESPONSE_MESSAGE = "<div data-format=\"PresentationML\" "
      + "data-version=\"V4\"> Hi customer, I'm fine.</div>";

  private static final String AGENT_RESPONSE_FIRST_MESSAGE = "<div data-format=\"PresentationML\" "
      + "data-version=\"V4\"> Hi customer, I'm good, and you?</div>";

  private static final String TICKET_CLOSED_MESSAGE = "<div data-format=\"PresentationML\" "
      + "data-version=\"V4\">%s</div>";

  private static final String IDLE_TICKET_MESSAGE = "<div data-format=\"PresentationML\" "
      + "data-version=\"V4\">    <div class=\"entity\" data-entity-id=\"helpdesk\">        <div "
      + "class=\"card barStyle\">            <div class=\"cardHeader\">                <span>    "
      + "                Ticket %s has been idle for 60 seconds.                </span>  "
      + "          </div>        </div>    </div></div>";

  private Long initialTime = 0L;

  private Ticket claimedTicket;

  private String clientUsername;

  private String agentUsername;

  public TicketSteps(MessageHelper messageHelper, TicketHelper ticketHelper, UserHelper userHelper,
      HelpDeskBotConfig helpDeskBotConfig,
      StreamHelper streamHelper) {
    this.messageHelper = messageHelper;
    this.ticketHelper = ticketHelper;
    this.userHelper = userHelper;
    this.helpDeskBotConfig = helpDeskBotConfig;
    this.streamHelper = streamHelper;
  }

  @When("$user sends an initial $question question to the bot")
  public void sendInitialQuestion(String user, String question)
      throws StreamsException, MessagesException, InterruptedException {
    this.initialTime = System.currentTimeMillis();

    SymMessage message = new SymMessage();

    if (question.equals(PERSONAL)) {
      message.setMessageText(PERSONAL_QUESTION);
    } else {
      message.setMessageText(HELP_QUESTION);
    }

    messageHelper.sendClientMessage(user, message);

    clientUsername = userHelper.getUser(user).getUsername();

    // Waiting message be processed
    Thread.sleep(5000L);
  }

  @Then("bot can verify a new idle message was created in the queue room")
  public void verifyIdleMessage() throws MessagesException, InterruptedException {

    // Waiting message be processed
    Thread.sleep(61000L);

    Optional<SymMessage> message = messageHelper.getLatestQueueRoomMessage(initialTime);

    assertTrue(message.isPresent());

    Optional<Ticket> ticket = ticketHelper.getUnservicedTicket();

    String expectedString = String.format(IDLE_TICKET_MESSAGE, ticket.get().getId());

    assertEquals(expectedString, message.get().getMessage());

  }

  @Then("bot can verify that ticket still claimed by $agent")
  public void verifyNoneMessage(String agent) throws MessagesException {

    Optional<SymMessage> message = messageHelper.getLatestQueueRoomMessage(initialTime);

    assertTrue(message.isPresent());

    Optional<Ticket> ticket = ticketHelper.getClaimedTicket();

    assertTrue(ticket.isPresent());

    String expectedString =
        String.format(TICKET_CLAIMED_BY_AGENT_MESSAGE, ticket.get().getId(),
            userHelper.getUser(agent.toUpperCase()).getUsername());

    assertEquals(expectedString, message.get().getMessage());

  }

  @Then("bot can verify a new ticket was created in the queue room with $question question")
  public void verifyInitialQuestion(String question)
      throws MessagesException {
    Optional<SymMessage> message =
        messageHelper.getLatestQueueRoomMessage(initialTime);

    assertTrue(message.isPresent());
    assertTrue(clientUsername != null);

    String expectedString;

    if (question.equals(PERSONAL)) {
      expectedString = String.format(AGENT_TICKET_CREATION_PERSONAL_MESSAGE, clientUsername);
    } else {
      expectedString = String.format(AGENT_TICKET_CREATION_HELP_MESSAGE, clientUsername);
    }
    assertEquals(expectedString, message.get().getMessage());
  }

  @Then("$user can verify the ticket successfully created message in the client room")
  public void verifyTicketCreatedMessage(String user) throws MessagesException,
      StreamsException {
    Optional<SymMessage> message =
        messageHelper.getLatestClientMessage(user, initialTime);

    assertTrue(message.isPresent());

    assertEquals(
        String.format(CLIENT_TICKET_CREATION_MESSAGE, helpDeskBotConfig.getCreateTicketMessage()),
        message.get().getMessage());
  }

  @When("$user user claims the latest ticket created")
  public void claimTicket(String username)
      throws InterruptedException {
    // Waiting message be processed
    Thread.sleep(5000L);

    Optional<Ticket> ticket = ticketHelper.getUnservicedTicket();

    assertTrue(ticket.isPresent());

    SymUser agentUser = userHelper.getUser(username.toUpperCase());

    TicketResponse response = ticketHelper.acceptTicket(ticket.get().getId(), agentUser.getId());

    claimedTicket = ticket.get();
    assertEquals("Ticket accepted.", response.getMessage());
    assertEquals("UNRESOLVED", response.getState());
  }

  @Then("bot can verify the $user user was added to the ticket room")
  public void verifyAgentInTheTicketRoom(String user) throws SymException {
    List<MemberInfo> membershipList = getMembershipList();
    Long userId = userHelper.getUser(user.toUpperCase()).getId();

    boolean match = membershipList.stream()
        .anyMatch(memberInfo -> memberInfo.getId().equals(userId));

    assertTrue(match);
  }

  @Then("$user user can see all the $question history conversation in the ticket room")
  public void verifyHistoryConversation(String username, String question) throws MessagesException {
    assertTrue(claimedTicket != null);
    assertTrue(clientUsername != null);

    List<SymMessage> ticketRoomMessages =
        messageHelper.getTicketRoomMessages(initialTime, claimedTicket.getServiceStreamId());

    assertEquals(2, ticketRoomMessages.size());
    assertEquals(String.format(MESSAGE_HISTORY_0, userHelper.getBotUser().getUsername()),
        ticketRoomMessages.get(1).getMessage());

    if (question.equals(PERSONAL)) {
      assertEquals(String.format(MESSAGE_HISTORY_1, clientUsername),
          ticketRoomMessages.get(0).getMessage());
    } else {
      assertEquals(String.format(MESSAGE_HISTORY_3, clientUsername),
          ticketRoomMessages.get(0).getMessage());
    }
  }

  @Then("$user user can see all the history conversation in the ticket room after agent answer")
  public void verifyHistoryConversationAfterAsnwer(String username) throws MessagesException {
    assertTrue(claimedTicket != null);
    assertTrue(clientUsername != null);
    assertTrue(agentUsername != null);

    List<SymMessage> ticketRoomMessages =
        messageHelper.getTicketRoomMessages(initialTime, claimedTicket.getServiceStreamId());

    assertEquals(3, ticketRoomMessages.size());
    assertEquals(String.format(MESSAGE_HISTORY_0, userHelper.getBotUser().getUsername()),
        ticketRoomMessages.get(2).getMessage());
    assertEquals(String.format(MESSAGE_HISTORY_1, clientUsername),
        ticketRoomMessages.get(1).getMessage());
    assertEquals(MESSAGE_HISTORY_2, ticketRoomMessages.get(0).getMessage());
  }

  @Then("$user can verify the ticket claimed message in the client room")
  public void verifyTicketClaimedMessage(String user)
      throws MessagesException, StreamsException {
    Optional<SymMessage> message =
        messageHelper.getLatestClientMessage(user, initialTime);

    assertTrue(message.isPresent());

    assertEquals(String.format(TICKET_CLAIMED_MESSAGE,
        helpDeskBotConfig.getAcceptTicketClientSuccessResponse()), message.get().getMessage());
  }

  @When("$user answer the client question")
  public void answerQuestion(String username)
      throws MessagesException, InterruptedException {
    SymMessage message = new SymMessage();
    message.setMessageText("Hi customer, I'm fine.");

    messageHelper.sendAgentMessage(username, message);

    agentUsername = userHelper.getUser(username.toUpperCase()).getUsername();

    // Waiting message be processed
    Thread.sleep(5000L);
  }

  @When("$user answer the first client question")
  public void answerFirstQuestion(String username)
      throws MessagesException, InterruptedException {
    SymMessage message = new SymMessage();
    message.setMessageText("Hi customer, I'm good, and you?");

    messageHelper.sendAgentMessageToOther(username, message);

    agentUsername = userHelper.getUser(username.toUpperCase()).getUsername();

    // Waiting message be processed
    Thread.sleep(5000L);
  }


  @Then("$user can verify the agent answer in the client room")
  public void verifyAgentAnswer(String user) throws MessagesException, StreamsException {
    Optional<SymMessage> message =
        messageHelper.getLatestClientMessage(user, initialTime);

    assertTrue(message.isPresent());

    assertEquals(AGENT_RESPONSE_MESSAGE, message.get().getMessage());
  }

  @Then("$user can verify the agent answer your question in the client room")
  public void verifyFirstAgentAnswer(String user) throws MessagesException, StreamsException {
    Optional<SymMessage> message =
        messageHelper.getLatestClientMessage(user, initialTime);

    assertTrue(message.isPresent());

    assertEquals(AGENT_RESPONSE_FIRST_MESSAGE, message.get().getMessage());
  }

  @When("$user user join the conversation")
  public void joinConversation(String user) {
    Long agentId = userHelper.getUser(user.toUpperCase()).getId();

    ticketHelper.joinTicketRoom(claimedTicket.getId(), agentId);
  }

  @When("$user user sends a message to close the ticket")
  public void closeTicket(String username)
      throws InterruptedException, MessagesException {
    SymUser botUser = userHelper.getBotUser();

    String closeMessage =
        String.format("<messageML><mention uid=\"%d\"/> close</messageML>", botUser.getId());

    SymMessage message = new SymMessage();
    message.setMessage(closeMessage);

    messageHelper.sendAgentMessage(username, message);

    // Waiting message be processed
    Thread.sleep(5000L);
  }

  @When("$user user sends a message to close the ticket (PresentationML 2.0 format)")
  public void closeTicketFormat2_0(String username)
      throws InterruptedException, MessagesException {
    SymUser botUser = userHelper.getBotUser();

    String closeMessage =
        "<div data-format=\"PresentationML\" data-version=\"2.0\" class=\"wysiwyg\"><span><span "
            + "class=\"entity\" data-entity-id=\"0\">@HalpDesk Lucas</span> close</span></div>";

    String closeEntityData = String.format(
        "{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":%d}],"
            + "\"type\":\"com.symphony.user.mention\"}}", botUser.getId());

    SymMessage message = new SymMessage();
    message.setMessage(closeMessage);

    messageHelper.sendAgentMessage(username, message);

    // Waiting message be processed
    Thread.sleep(5000L);
  }

  @When("$user user sends a message to close the other ticket")
  public void closeOtherTicket(String username)
      throws InterruptedException, MessagesException {
    SymUser botUser = userHelper.getBotUser();

    String closeMessage =
        String.format("<messageML><mention uid=\"%d\"/> close</messageML>", botUser.getId());

    SymMessage message = new SymMessage();
    message.setMessage(closeMessage);

    messageHelper.sendAgentMessageToOther(username, message);

    // Waiting message be processed
    Thread.sleep(5000L);
  }

  @Then("bot can verify there are no agents in the ticket room")
  public void verifyOnlyBotInTicketRoom() throws SymException {
    List<MemberInfo> membershipList = getMembershipList();
    Long botId = userHelper.getBotUser().getId();

    assertEquals(1, membershipList.size());
    assertEquals(botId, membershipList.get(0).getId());
  }

  @Then("bot can verify only user $agent is in the ticket room")
  public void verifyAgentInTicketRoom(String agent) throws SymException {
    List<MemberInfo> membershipList = getMembershipList();
    Long botId = userHelper.getBotUser().getId();
    Long agentId = userHelper.getUser(agent.toUpperCase()).getId();

    assertEquals(2, membershipList.size());
    assertTrue(membershipList.stream().anyMatch(memberInfo -> memberInfo.getId().equals(botId)));
    assertTrue(membershipList.stream().anyMatch(memberInfo -> memberInfo.getId().equals(agentId)));
  }

  private List<MemberInfo> getMembershipList() throws SymException {
    assertTrue(claimedTicket != null);

    SymStream ticketStream = streamHelper.getTicketStream(claimedTicket);
    return streamHelper.getStreamMembershipList(ticketStream);
  }

  @Then("$user can verify the ticket closed message in the client room")
  public void verifyTicketClosedMessage(String user)
      throws MessagesException, StreamsException {
    Optional<SymMessage> message =
        messageHelper.getLatestClientMessage(user, initialTime);

    assertTrue(message.isPresent());

    assertEquals(
        String.format(TICKET_CLOSED_MESSAGE, helpDeskBotConfig.getCloseTicketSuccessResponse()),
        message.get().getMessage());
  }

  @Then("$user leaves the ticket room")
  public void leavesTicketRoom(String user) throws SymException {

    Long userId = userHelper.getUser(user.toUpperCase()).getId();
    Optional<SymStream> symStream = streamHelper.getTicketStream(userId);
    assertTrue(symStream.isPresent());

    streamHelper.removeMembershipFromRoom(symStream.get().getStreamId(), userId);
  }

}
