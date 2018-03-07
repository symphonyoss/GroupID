package org.symphonyoss.symphony.bots.helpdesk.bot.it.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Assert;
import org.springframework.stereotype.Component;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.UsersEnum;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers.MessageHelper;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers.StreamHelper;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers.TicketHelper;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers.UserHelper;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.utils.StreamUtils;
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

  private final MessageHelper messageHelper;

  private final TicketHelper ticketHelper;

  private final UserHelper userHelper;

  private final HelpDeskBotConfig helpDeskBotConfig;

  private final StreamHelper streamHelper;

  private static final String AGENT_TICKET_CREATION_MESSAGE = "<div data-format=\"PresentationML"
      + "\" data-version=\"V4\">    <div class=\"entity\" data-entity-id=\"helpdesk\">        "
      + "<div class=\"card barStyle\">            <div class=\"cardHeader\">                "
      + "<span><b>Equities Desk Bot</b></span>            </div>            <div "
      + "class=\"cardBody\">                <span><b>Company:</b> Symphony Engineering Services "
      + "Dev 5</span><br/>                <span><b>Customer:</b> "
      + "%s</span><br/>                "
      + "<span><b>Question:</b>  Hi bot, how are you doing?</span>            </div>        "
      + "</div>    </div></div>";

  private static final String CLIENT_TICKET_CREATION_MESSAGE = "<div data-format=\"PresentationML"
      + "\" data-version=\"V4\">%s</div>";

  private static final String MESSAGE_HISTORY_0 = "<div data-format=\"PresentationML\" "
      + "data-version=\"V4\">Use <span class=\"entity\" "
      + "data-entity-id=\"mention1\">@%s</span> "
      + "<b>Close</b>  to close the ticket upon ticket resolution.</div>";

  private static final String MESSAGE_HISTORY_1 = "<div data-format=\"PresentationML\" "
      + "data-version=\"V4\"><b>%s</b>:  Hi bot, how are you doing?</div>";

  private static final String MESSAGE_HISTORY_2 =
      "<div data-format=\"PresentationML\" data-version=\"V4\">Hi customer, I'm fine.</div>";

  private static final String TICKET_CLAIMED_MESSAGE = "<div data-format=\"PresentationML\" "
      + "data-version=\"V4\">%s</div>";

  private static final String AGENT_RESPONSE_MESSAGE = "<div data-format=\"PresentationML\" "
      + "data-version=\"V4\"> Hi customer, I'm fine.</div>";

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

  @When("$user sends an initial question to the bot")
  public void sendInitialQuestion(String user)
      throws StreamsException, MessagesException, InterruptedException {
    this.initialTime = System.currentTimeMillis();

    SymMessage message = new SymMessage();
    message.setMessageText("Hi bot, how are you doing?");

    messageHelper.sendClientMessage(user, message);

    clientUsername = userHelper.getUser(user).getUsername();

    // Waiting message be processed
    Thread.sleep(5000L);
  }

  @Then("bot can verify a new idle message was created in the queue room")
  public void verifyIdleMessage() throws StreamsException, MessagesException, InterruptedException {

    // Waiting message be processed
    Thread.sleep(60000L);

    Optional<SymMessage> message = messageHelper.getLatestQueueRoomMessage(initialTime);

    assertTrue(message.isPresent());

    Optional<Ticket> ticket = ticketHelper.getUnservicedTicket();

    String expectedString = String.format(IDLE_TICKET_MESSAGE, ticket.get().getId());

    assertEquals(expectedString, message.get().getMessage());

  }

  @Then("bot can verify a new ticket was created in the queue room")
  public void verifyInitialQuestion() throws MessagesException, InterruptedException {
    Optional<SymMessage> message =
        messageHelper.getLatestQueueRoomMessage(initialTime);

    assertTrue(message.isPresent());
    assertTrue(clientUsername != null);

    String expectedString = String.format(AGENT_TICKET_CREATION_MESSAGE, clientUsername);

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
  public void claimTicket(String username) throws StreamsException, MessagesException, InterruptedException {
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
    assertTrue(claimedTicket != null);

    SymStream ticketStream = streamHelper.getTicketStream(claimedTicket);
    List<MemberInfo> membershipList = streamHelper.getStreamMembershipList(ticketStream);
    Long userId = userHelper.getUser(user.toUpperCase()).getId();

    boolean match = membershipList.stream()
        .anyMatch(memberInfo -> memberInfo.getId().equals(userId));

    assertTrue(match);
  }

  @Then("$user user can see all the history conversation in the ticket room")
  public void verifyHistoryConversation(String username) throws MessagesException {
    assertTrue(claimedTicket != null);
    assertTrue(clientUsername != null);

    List<SymMessage> ticketRoomMessages =
        messageHelper.getTicketRoomMessages(initialTime, claimedTicket.getServiceStreamId());

    assertEquals(2, ticketRoomMessages.size());
    assertEquals(String.format(MESSAGE_HISTORY_0, userHelper.getBotUser().getUsername()),
        ticketRoomMessages.get(1).getMessage());
    assertEquals(String.format(MESSAGE_HISTORY_1, clientUsername),
        ticketRoomMessages.get(0).getMessage());
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
      throws MessagesException, StreamsException, InterruptedException {
    SymMessage message = new SymMessage();
    message.setMessageText("Hi customer, I'm fine.");

    messageHelper.sendAgentMessage(username, message);

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

  @When("$user user join the conversation")
  public void joinConversation(String user) throws SymException {
    Long agentId = userHelper.getUser(user.toUpperCase()).getId();

    ticketHelper.joinTicketRoom(claimedTicket.getId(), agentId);
  }

  @When("$user user sends a message to close the ticket")
  public void closeTicket(String username)
      throws InterruptedException, MessagesException, StreamsException {
    SymUser botUser = userHelper.getBotUser();

    String closeMessage =
        String.format("<messageML><mention uid=\"%d\"/> close</messageML>", botUser.getId());

    SymMessage message = new SymMessage();
    message.setMessage(closeMessage);

    messageHelper.sendAgentMessage(username, message);

    // Waiting message be processed
    Thread.sleep(5000L);
  }

  @Then("bot can verify there are no agents in the ticket room")
  public void emptyRoom() throws SymException {
    assertTrue(claimedTicket != null);

    SymStream ticketStream = streamHelper.getTicketStream(claimedTicket);
    List<MemberInfo> membershipList = streamHelper.getStreamMembershipList(ticketStream);
    Long botId = userHelper.getBotUser().getId();

    assertEquals(1, membershipList.size());
    assertEquals(botId, membershipList.get(0).getId());
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

    streamHelper.removeMembershipFromRoom(symStream.get().getStreamId(),userId);
  }

}
