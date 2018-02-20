package org.symphonyoss.symphony.bots.helpdesk.integrationtests.jbehave.steps;

import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.When;

/**
 * Created by alexandre-silva-daitan on 20/02/18.
 */
public class AuthenticationSteps {

  @When("user $username logs in")
  public void userLogin(@Named("user") String username) {
    //TODO
  }
}
