package org.symphonyoss.symphony.bots.helpdesk.bot.it.steps;

import static org.junit.Assert.assertFalse;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.springframework.stereotype.Component;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.TestContext;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.exception.UserNotAuthenticatedException;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.Stream;

import java.util.List;

/**
 * Class responsible for managing ticket steps.
 * <p>
 * Created by rsanchez on 23/02/18.
 */
@Component
public class TicketSteps {

  private final SymphonyClient symphonyClient;

  private final TestContext context = TestContext.getInstance();

  private Long initialTime = 0L;

  public TicketSteps(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
  }

  @When("$user sends an initial question to the bot")
  public void sendInitialQuestion(String username)
      throws StreamsException, MessagesException, InterruptedException {
    this.initialTime = System.currentTimeMillis();

    SymUser botUser = symphonyClient.getLocalUser();

    SymphonyClient userClient = getUserContext(username);

    SymStream stream = userClient.getStreamsClient().getStream(botUser);

    SymMessage message = new SymMessage();
    message.setMessageText("Hi bot, how are you doing?");

    userClient.getMessageService().sendMessage(stream, message);

    // Waiting message be processed
    Thread.sleep(5000L);
  }

  @Then("bot can verify a new ticket was created in the queue room")
  public void verifyInitialQuestion() throws MessagesException {
    Stream queueRoom = context.getQueueRoom().getStream();

    List<SymMessage> messagesFromStream =
        symphonyClient.getMessagesClient().getMessagesFromStream(queueRoom, initialTime, 0, 1);

    assertFalse(messagesFromStream.isEmpty());

    // TODO Verify message content
  }

  @Then("$user can verify the ticket successfully created message in the client room")
  public void verifyTicketCreatedMessage(String username) throws MessagesException,
      StreamsException {
    SymUser botUser = symphonyClient.getLocalUser();

    SymphonyClient userClient = getUserContext(username);

    SymStream stream = userClient.getStreamsClient().getStream(botUser);

    List<SymMessage> messagesFromStream =
        userClient.getMessagesClient().getMessagesFromStream(stream, initialTime, 1, 1);

    assertFalse(messagesFromStream.isEmpty());

    // TODO Verify message content
  }

  @When("$user user claims the latest ticket created")
  public void claimTicket(String username) {
    // TODO
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
  public void answerQuestion(String username) {
    // TODO
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
  public void closeTicket(String username) {
    // TODO
  }

  @Then("bot can verify there are no agents in the ticket room")
  public void emptyRoom() {
    // TODO
  }

  @Then("$user can verify the ticket closed message in the client room")
  public void verifyTicketClosedMessage() {
    // TODO
  }

  private SymphonyClient getUserContext(String username) {
    SymphonyClient userClient = context.getAuthenticatedUser(username);

    if (userClient == null) {
      throw new UserNotAuthenticatedException("User " + username + " is not authenticated");
    }

    return userClient;
  }

}
