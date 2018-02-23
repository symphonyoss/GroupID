package org.symphonyoss.symphony.bots.helpdesk.integrationtests.jbehave.steps;

import org.jbehave.core.annotations.Then;

/**
 * Created by alexandre-silva-daitan on 21/02/18.
 */
public class RoomSteps {

  @Then("$agent join into a $room")
  public void joinIntoARoom(String agentRef, String roomRef) {
    //TODO
  }

  @Then("$agent join conversation of a claimed ticket")
  public void joinConversation(String agentRef) {
    //TODO
  }

  @Then("$bot close the $room")
  public void closeRoom(String botRef, String roomRef) {
    //TODO
  }

}
