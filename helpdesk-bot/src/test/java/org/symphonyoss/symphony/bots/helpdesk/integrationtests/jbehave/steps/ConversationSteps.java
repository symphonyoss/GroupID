package org.symphonyoss.symphony.bots.helpdesk.integrationtests.jbehave.steps;

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

}
