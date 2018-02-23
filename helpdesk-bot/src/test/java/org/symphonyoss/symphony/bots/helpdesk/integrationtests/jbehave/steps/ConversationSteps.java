package org.symphonyoss.symphony.bots.helpdesk.integrationtests.jbehave.steps;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

/**
 * Created by alexandre-silva-daitan on 20/02/18.
 */
public class ConversationSteps {

  @When("user $user creates an IM with $bot")
  public void createsAnIM(String userRef, String botRef) {
    //TODO
  }

  @When("$bot create a ticket to help the client $user")
  public void createATicket(String botRef, String userRef) {
    //TODO
  }

  @Then("$bot send a successful message to $user")
  public void sendSuccessfulMessage(String botRef, String userRef) {
    //TODO
  }

  @Then("$bot send a message to $ticketRoom without create new ticket")
  public void sendMessageToTicketRoom(String botRef, String roomRef) {
    //TODO
  }

  @Then("$user talk to $bot")
  public void userTalkToBot(String userRef, String botRef) {
    //TODO
  }

  @Then("$agent receive an error message")
  public void receiveAnErrorMessage(String agentRef) {
    //TODO
  }

  @Then("$bot send a message to $user")
  public void sendAMessage(String botRef, String userRef) {
    //TODO
  }

}
