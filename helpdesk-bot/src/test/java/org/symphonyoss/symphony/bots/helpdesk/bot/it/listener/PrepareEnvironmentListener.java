package org.symphonyoss.symphony.bots.helpdesk.bot.it.listener;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.utils.AuthenticationUtils;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.utils.CertificateUtils;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.utils.UserUtils;

import java.util.UUID;

/**
 * Listener to prepare environment.
 *
 * Created by rsanchez on 01/03/18.
 */
public class PrepareEnvironmentListener implements TestExecutionListener {

  private static final String CA_KEY_PATH = "caKeyPath";
  private static final String CA_CERT_PATH = "caCertPath";
  private static final String USER_PROVISIONING = "helpdeskProvisioning";
  private static final String BOT_USER = "HelpDesk";

  @Override
  public void beforeTestClass(TestContext testContext) throws Exception {
//    String caKeyPath = System.getProperty(CA_KEY_PATH);
//    String caCertPath = System.getProperty(CA_CERT_PATH);

    CertificateUtils.createCertsDir();
//    CertificateUtils.createUserCertificate(caKeyPath, caCertPath, USER_PROVISIONING);
//
//    String botUsername = BOT_USER + UUID.randomUUID();
//    CertificateUtils.createUserCertificate(caKeyPath, caCertPath, botUsername);

    String botUsername = "helpdesk-it";

    System.setProperty("BOT_USER", botUsername);

    HelpDeskBotConfig config = testContext.getApplicationContext().getBean(HelpDeskBotConfig.class);

    AuthenticationUtils authenticationUtils = new AuthenticationUtils(config);
    SymphonyClient symphonyClient = authenticationUtils.authenticateUser(USER_PROVISIONING);

    UserUtils userUtils = new UserUtils(symphonyClient);
    userUtils.createServiceAccount(botUsername);
  }

  @Override
  public void prepareTestInstance(TestContext testContext) throws Exception {
    // Do nothing
  }

  @Override
  public void beforeTestMethod(TestContext testContext) throws Exception {
    // Do nothing
  }

  @Override
  public void afterTestMethod(TestContext testContext) throws Exception {
    // Do nothing
  }

  @Override
  public void afterTestClass(TestContext testContext) throws Exception {
    // Do nothing
  }

}
