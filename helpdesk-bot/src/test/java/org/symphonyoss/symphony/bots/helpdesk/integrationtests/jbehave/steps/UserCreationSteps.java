package org.symphonyoss.symphony.bots.helpdesk.integrationtests.jbehave.steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.When;

import java.util.List;

/**
 * Created by alexandre-silva-daitan on 20/02/18.
 */
public class UserCreationSteps {

  @Given("a new user $user in a private pod with roles $roles")
  public void createUserInPrivatePodWithRoles(String userRef, List<String> roles) {
    //TODO
  }

  @When("set bot user $bot")
  public void setupBot(String botRef) {
    //TODO
  }
}
