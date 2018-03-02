package org.symphonyoss.symphony.bots.helpdesk.bot.it.steps;

import static org.junit.Assert.assertTrue;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.springframework.stereotype.Component;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers.MessageHelper;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers.TicketHelper;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.helpers.UserHelper;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.TicketResponse;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Ticket;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;

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

  private Long initialTime = 0L;

  public TicketSteps(MessageHelper messageHelper, TicketHelper ticketHelper, UserHelper userHelper) {
    this.messageHelper = messageHelper;
    this.ticketHelper = ticketHelper;
    this.userHelper = userHelper;
  }

  @When("$user sends an initial question to the bot")
  public void sendInitialQuestion(String username)
      throws StreamsException, MessagesException, InterruptedException {
    this.initialTime = System.currentTimeMillis();

    SymMessage message = new SymMessage();
    message.setMessageText("Hi bot, how are you doing?");

    messageHelper.sendClientMessage(username, message);

    // Waiting message be processed
    Thread.sleep(5000L);
  }

  @Then("bot can verify a new ticket was created in the queue room")
  public void verifyInitialQuestion() throws MessagesException {
    Optional<SymMessage> message =
        messageHelper.getLatestQueueRoomMessage(initialTime);

    assertTrue(message.isPresent());

    // TODO Verify message content
  }

  @Then("$user can verify the ticket successfully created message in the client room")
  public void verifyTicketCreatedMessage(String username) throws MessagesException,
      StreamsException {
    Optional<SymMessage> message =
        messageHelper.getLatestClientMessage(username, initialTime);

    assertTrue(message.isPresent());

    // TODO Verify message content
  }

  @When("$user user claims the latest ticket created")
  public void claimTicket(String username) {
    Optional<Ticket> ticket = ticketHelper.getUnservicedTicket();

    assertTrue(ticket.isPresent());

    SymUser agentUser = userHelper.getAgentUser(username);

    TicketResponse response = ticketHelper.acceptTicket(ticket.get().getId(), agentUser.getId());

    // TODO Evaluate response
  }

  @Then("bot can verify the $user user was added to the ticket room")
  public void verifyAgentInTheTicketRoom(String username) {
    // TODO
  }

  @Then("$user user can see all the history conversation in the ticket room")
  public void verifyHistoryConversation(String username) {
    // TODO
  }

  @Then("$user can verify the ticket claimed message in the client room")
  public void verifyTicketClaimedMessage(String username) {
    // TODO
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
  public void verifyAgentAnswer(String username) {
    // TODO
  }

  @When("$user user join the conversation")
  public void joinConversation(String username) {
    // TODO
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
  public void emptyRoom() {
    // TODO
  }

  @Then("$user can verify the ticket closed message in the client room")
  public void verifyTicketClosedMessage() {
    // TODO
  }

}
