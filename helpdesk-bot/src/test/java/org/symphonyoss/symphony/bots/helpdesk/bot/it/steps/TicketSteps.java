package org.symphonyoss.symphony.bots.helpdesk.bot.it.steps;

import static org.junit.Assert.assertEquals;
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

  private final MessageHelper messageHelper;

  private final TicketHelper ticketHelper;

  private final UserHelper userHelper;

  private final HelpDeskBotConfig helpDeskBotConfig;

  private final StreamHelper streamHelper;

  private Long initialTime = 0L;

  private Ticket claimedTicket;

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

    // Waiting message be processed
    Thread.sleep(5000L);
  }

  @Then("bot can verify a new ticket was created in the queue room")
  public void verifyInitialQuestion() throws MessagesException {
    Optional<SymMessage> message =
        messageHelper.getLatestQueueRoomMessage(initialTime);

    assertTrue(message.isPresent());

    assertTrue(message.get().getMessage().contains("<b>Question:</b> Hi bot, how are you doing?"));
  }

  @Then("$user can verify the ticket successfully created message in the client room")
  public void verifyTicketCreatedMessage(String user) throws MessagesException,
      StreamsException {
    Optional<SymMessage> message =
        messageHelper.getLatestClientMessage(user, initialTime);

    assertTrue(message.isPresent());

    assertTrue(message.get().getMessage().contains(helpDeskBotConfig.getCreateTicketMessage()));
  }

  @When("$user user claims the latest ticket created")
  public void claimTicket(String username) {
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

    List<SymMessage> ticketRoomMessages =
        messageHelper.getTicketRoomMessages(initialTime, claimedTicket.getServiceStreamId());

    assertEquals(2, ticketRoomMessages.size());
    assertTrue(ticketRoomMessages.get(0).getMessage().contains("close the ticket upon ticket resolution"));
    assertTrue(ticketRoomMessages.get(1).getMessage().contains("Hi customer, I'm fine."));
  }

  @Then("$user can verify the ticket claimed message in the client room")
  public void verifyTicketClaimedMessage(String user)
      throws MessagesException, StreamsException {
    Optional<SymMessage> message =
        messageHelper.getLatestClientMessage(user, initialTime);

    assertTrue(message.isPresent());

    assertTrue(message.get().getMessage().contains(helpDeskBotConfig.getAcceptTicketClientSuccessResponse()));
  }

  @When("$user answer the client question")
  public void answerQuestion(String username)
      throws MessagesException, StreamsException, InterruptedException {
    SymMessage message = new SymMessage();
    message.setMessageText("Hi customer, I'm fine.");

    messageHelper.sendAgentMessage(username, message);

    // Waiting message be processed
    Thread.sleep(5000L);
  }

  @Then("$user can verify the agent answer in the client room")
  public void verifyAgentAnswer(String user) throws MessagesException, StreamsException {
    Optional<SymMessage> message =
        messageHelper.getLatestClientMessage(user, initialTime);

    assertTrue(message.isPresent());

    assertTrue(message.get().getMessage().contains("Hi customer, I'm fine."));
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

    String closeMessage = String.format("<messageML><mention uid=\"%d\"/> close</messageML>", botUser.getId());

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

    assertTrue(message.get().getMessage().contains(helpDeskBotConfig.getCloseTicketSuccessResponse()));
  }

}
