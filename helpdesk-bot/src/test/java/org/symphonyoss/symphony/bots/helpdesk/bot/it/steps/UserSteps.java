package org.symphonyoss.symphony.bots.helpdesk.bot.it.steps;

import org.jbehave.core.annotations.Given;
import org.springframework.stereotype.Component;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.TestContext;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.utils.CertificateUtils;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.utils.UserUtils;

import java.util.List;
import java.util.UUID;

/**
 * Class responsible for managing user steps.
 * <p>
 * Created by rsanchez on 23/02/18.
 */
@Component
public class UserSteps {

  private static final String USER_PROVISIONING = "helpdeskProvisioning";

  private static final String CA_KEY_PATH = "caKeyPath";

  private static final String CA_CERT_PATH = "caCertPath";

  private final TestContext context = TestContext.getInstance();

  @Given("a new user account $user with roles $roles")
  public void createEndUser(String user, List<String> roles) {
    SymphonyClient symphonyClient = context.getAuthenticatedUser(USER_PROVISIONING);

    String username = user + UUID.randomUUID();

    UserUtils userUtils = new UserUtils(symphonyClient);
    userUtils.createEndUser(username, roles);
  }

  @Given("a certificate for $user user")
  public void createUserCertificate(String user) {
    String caKeyPath = System.getProperty(CA_KEY_PATH);
    String caCertPath = System.getProperty(CA_CERT_PATH);

    CertificateUtils.createUserCertificate(caKeyPath, caCertPath, user);
  }

}
