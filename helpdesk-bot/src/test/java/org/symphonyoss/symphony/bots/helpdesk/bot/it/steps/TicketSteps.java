package org.symphonyoss.symphony.bots.helpdesk.bot.it.steps;

import org.jbehave.core.annotations.Then;
import org.springframework.stereotype.Component;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.TestContext;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.exception.UserNotAuthenticatedException;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;

/**
 * Class responsible for managing ticket steps.
 * <p>
 * Created by rsanchez on 23/02/18.
 */
@Component
public class TicketSteps {

  private final SymphonyClient symphonyClient;

  private final TestContext context = TestContext.getInstance();

  public TicketSteps(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
  }

  @Then("$user sends an initial question to the bot")
  public void sendInitialQuestion(String username) throws StreamsException, MessagesException {
    SymUser botUser = symphonyClient.getLocalUser();

    SymphonyClient userClient = context.getAuthenticatedUser(username);

    if (userClient == null) {
      throw new UserNotAuthenticatedException("User " + username + " is not authenticated");
    }

    SymStream stream = userClient.getStreamsClient().getStream(botUser);

    SymMessage message = new SymMessage();
    message.setMessageText("Hi bot, how are you doing?");

    userClient.getMessageService().sendMessage(stream, message);
  }

}
