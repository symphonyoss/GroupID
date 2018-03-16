package org.symphonyoss.symphony.bots.helpdesk.bot.it.steps;

import org.jbehave.core.annotations.Given;
import org.springframework.stereotype.Component;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.helpdesk.bot.util.TestContext;
import org.symphonyoss.symphony.bots.helpdesk.bot.util.CertificateUtils;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.utils.UserUtils;
import org.symphonyoss.symphony.clients.model.SymUser;

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

  private CertificateUtils certificateUtils = new CertificateUtils();

  @Given("a new user account $user with roles $roles")
  public void createEndUser(String user, List<String> roles) {
    SymphonyClient symphonyClient = context.getAuthenticatedUser(USER_PROVISIONING);

    String username = user + UUID.randomUUID();

    UserUtils userUtils = new UserUtils(symphonyClient);
    SymUser endUser = userUtils.createEndUser(username, roles);

    context.setUsers(user, endUser);
  }

  @Given("a certificate for $user user")
  public void createUserCertificate(String user) {
    String username = context.getUser(user).getUsername();

    String caKeyPath = System.getProperty(CA_KEY_PATH);
    String caCertPath = System.getProperty(CA_CERT_PATH);

    certificateUtils.createUserCertificate(caKeyPath, caCertPath, username);
  }

}
