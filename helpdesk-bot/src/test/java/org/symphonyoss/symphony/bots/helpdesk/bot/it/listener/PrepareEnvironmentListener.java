package org.symphonyoss.symphony.bots.helpdesk.bot.it.listener;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.AuthenticationException;
import org.symphonyoss.client.exceptions.InitException;
import org.symphonyoss.client.exceptions.RoomException;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.UsersEnum;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.utils.AuthenticationUtils;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.utils.CertificateUtils;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.utils.StreamUtils;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.utils.UserUtils;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.Arrays;
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
  private static final String AGENT_USER = "Agent";
  private static final String QUEUE_ROOM = "Queue Room";
  private static final String[] SUPPORTED_ENVS = { "nexus1", "nexus2", "nexus3", "nexus4" };
  private static final String AGENT_STREAM_ID = "agentStreamId";
  private static final String GROUP_ID = "groupId";

  private static final org.symphonyoss.symphony.bots.helpdesk.bot.it.TestContext CONTEXT = org
      .symphonyoss.symphony.bots.helpdesk.bot.it.TestContext.getInstance();


  @Override
  public void beforeTestClass(TestContext testContext) throws Exception {
    String caKeyPath = System.getProperty(CA_KEY_PATH);
    String caCertPath = System.getProperty(CA_CERT_PATH);

    CertificateUtils.createCertsDir();
    CertificateUtils.createUserCertificate(caKeyPath, caCertPath, USER_PROVISIONING);

    changeGroupId();

    ApplicationContext context = testContext.getApplicationContext();

    validateEnvironment(context);

    SymphonyClient symphonyClient = authenticate(context);
    Room queueRoom = createQueueRoom(symphonyClient);
    createUsers(symphonyClient, queueRoom);

    testContext.markApplicationContextDirty(DirtiesContext.HierarchyMode.CURRENT_LEVEL);
  }

  private void changeGroupId() {
    String groupId = StringUtils.abbreviate(UUID.randomUUID().toString(), 20);
    System.setProperty(GROUP_ID, groupId);

  }

  private void validateEnvironment(ApplicationContext applicationContext) {
    String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();

    long count = Arrays.stream(activeProfiles)
        .filter(profile -> Arrays.asList(SUPPORTED_ENVS).contains(profile))
        .count();

    if (count == 0) {
      throw new IllegalStateException("You must setup environment");
    }
  }

  private SymphonyClient authenticate(ApplicationContext context)
      throws InitException, AuthenticationException {
    HelpDeskBotConfig config = context.getBean(HelpDeskBotConfig.class);

    AuthenticationUtils authenticationUtils = new AuthenticationUtils(config);
    return authenticationUtils.authenticateUser(USER_PROVISIONING);
  }

  private Room createQueueRoom(SymphonyClient symphonyClient) throws RoomException {
    StreamUtils streamUtils = new StreamUtils(symphonyClient);

    Room queueRoom = streamUtils.createRoom(QUEUE_ROOM, Boolean.FALSE);
    CONTEXT.setQueueRoom(queueRoom);

    System.setProperty(AGENT_STREAM_ID, queueRoom.getStreamId());

    return queueRoom;
  }

  private void createUsers(SymphonyClient symphonyClient, Room queueRoom) throws SymException {
    String caKeyPath = System.getProperty(CA_KEY_PATH);
    String caCertPath = System.getProperty(CA_CERT_PATH);

    String botUsername = BOT_USER + UUID.randomUUID();
    CertificateUtils.createUserCertificate(caKeyPath, caCertPath, botUsername);

    System.setProperty("BOT_USER", botUsername);

    UserUtils userUtils = new UserUtils(symphonyClient);
    SymUser botUser = userUtils.createServiceAccount(botUsername);

    SymUser agent1 = userUtils.createEndUser(AGENT_USER + UUID.randomUUID());
    SymUser agent2 = userUtils.createEndUser(AGENT_USER + UUID.randomUUID());
    SymUser agent3 = userUtils.createEndUser(AGENT_USER + UUID.randomUUID());

    CONTEXT.setUsers(UsersEnum.AGENT1.name(), agent1);
    CONTEXT.setUsers(UsersEnum.AGENT2.name(), agent2);
    CONTEXT.setUsers(UsersEnum.AGENT3.name(), agent3);

    CertificateUtils.createUserCertificate(caKeyPath, caCertPath, agent1.getUsername());
    CertificateUtils.createUserCertificate(caKeyPath, caCertPath, agent2.getUsername());
    CertificateUtils.createUserCertificate(caKeyPath, caCertPath, agent3.getUsername());

    StreamUtils streamUtils = new StreamUtils(symphonyClient);
    String streamId = queueRoom.getStreamId();

    streamUtils.addMembershipToRoom(streamId, botUser.getId());
    streamUtils.addMembershipToRoom(streamId, agent1.getId());
    streamUtils.addMembershipToRoom(streamId, agent2.getId());
    streamUtils.addMembershipToRoom(streamId, agent3.getId());
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
