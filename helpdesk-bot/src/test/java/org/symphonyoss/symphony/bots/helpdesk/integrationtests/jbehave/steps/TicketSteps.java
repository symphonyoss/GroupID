package org.symphonyoss.symphony.bots.helpdesk.integrationtests.jbehave.steps;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

/**
 * Created by alexandre-silva-daitan on 21/02/18.
 */
public class TicketSteps {

  @Then("user $agent claim a ticket in the $room")
  public void ClaimATicket(String agentRef, String roomRef) {
    //TODO
  }

  @When("$user send a close message to $bot")
  public void closeMessage(String userRef, String botRef) {
    //TODO
  }

}
