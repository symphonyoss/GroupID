package org.symphonyoss.symphony.bots.helpdesk.bot.it.steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class responsible for managing user steps.
 * <p>
 * Created by rsanchez on 23/02/18.
 */
@Component
public class UserSteps {

  @Given("a new user account $user with roles $roles")
  public void createEndUser(String user, List<String> roles) {
    // TODO
  }

  @Given("a service account $user with roles $roles")
  public void createServiceAccount(String user, List<String> roles) {
    // TODO
  }

  @Given("a certificate for $user")
  public void createUserCertificate(String user) {
    // TODO
  }

}
