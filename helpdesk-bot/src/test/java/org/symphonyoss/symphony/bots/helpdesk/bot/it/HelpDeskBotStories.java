package org.symphonyoss.symphony.bots.helpdesk.bot.it;

import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.PrintStreamStepdocReporter;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.SilentStepMonitor;
import org.jbehave.core.steps.spring.SpringStepsFactory;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.symphonyoss.client.exceptions.InitException;
import org.symphonyoss.client.exceptions.RoomException;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.bots.helpdesk.bot.authentication.HelpDeskAuthenticationService;
import org.symphonyoss.symphony.bots.helpdesk.bot.bootstrap.HelpDeskBootstrap;
import org.symphonyoss.symphony.bots.helpdesk.bot.client.HelpDeskHttpClient;
import org.symphonyoss.symphony.bots.helpdesk.bot.client.HelpDeskSymphonyClient;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.init.SpringHelpDeskBotInit;
import org.symphonyoss.symphony.clients.model.SymRoomAttributes;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.UserAttributes;
import org.symphonyoss.symphony.pod.model.UserCreate;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by rsanchez on 15/02/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = SpringHelpDeskBotInit.class)
public class HelpDeskBotStories extends JUnitStories {

  private static final String[] SUPPORTED_ENVS = { "nexus1", "nexus2", "nexus3", "nexus4" };

  private static final String ROLE_INDIVIDUAL = "INDIVIDUAL";

  private static final String AGENT = "Agent";

  private static final String CERTS_DIR = "certs";

  private static final String MESSAGE_BOT_CERTIFICATE_NOT_FOUND = "Bot certificate not found.";

  private static final String MESSAGE_PROVISIONING_CERTIFICATE_NOT_FOUND =
      "Provisioning certificate not found.";

  private static final String USER_PROVISIONING = "userProvisioning";

  private static final String EXTENSION_CERTIFICATE = ".p12";

  private static final String BEGIN_NAME_OF_CERTIFICATE = "Bot";

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private HelpDeskBotConfig config;

  private HelpDeskSymphonyClient helpDeskSymphonyClient;

  private TestContext testContext = TestContext.getInstance();

  private CertificateUtils certificateUtils = new CertificateUtils();

  public HelpDeskBotStories() {
    initJBehaveConfiguration();
  }

  private void initJBehaveConfiguration() {
    Class<?> thisClass = this.getClass();
    useConfiguration(new MostUsefulConfiguration()
        .useStoryLoader(new LoadFromClasspath(thisClass.getClassLoader()))
        .usePendingStepStrategy(new FailingUponPendingStep())
        .useStepdocReporter(new PrintStreamStepdocReporter())
        .useStoryReporterBuilder(new StoryReporterBuilder()
            .withCodeLocation(CodeLocations.codeLocationFromClass(thisClass))
            .withDefaultFormats()
            .withFormats(Format.CONSOLE, Format.TXT, Format.HTML, Format.XML, Format.STATS)
            .withCrossReference(new CrossReference())
            .withFailureTrace(true))
        .useStepMonitor(new SilentStepMonitor()));
  }

  @Before
  public void bootstrap() {
    String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();

    long count = Arrays.stream(activeProfiles)
        .filter(profile -> Arrays.asList(SUPPORTED_ENVS).contains(profile))
        .count();

    if (count == 0) {
      throw new IllegalStateException("You must setup environment");
    }

    prepareEnvironment();

    new HelpDeskBootstrap().execute(applicationContext);
  }

  private void prepareEnvironment() {
    createCertsDir();

    String caKeyPath = System.getProperty("caKeyPath");
    String caCertPath = System.getProperty("caCertPath");

    provisioningSteps(caKeyPath, caCertPath);

    initSymphonyClient();

    createQueueRoom();
    createUsersAndAddToQueueRoom();
  }

  /**
   * Creates directory of certificates in tmp directory and set on Context.
   */
  private void createCertsDir() {
    String certsDir = System.getProperty("java.io.tmpdir") + File.separator + CERTS_DIR;

    File directory = new File(certsDir);
    if (!directory.exists()) {
      directory.mkdirs();
    }

    testContext.setCertsDir(certsDir);
  }

  @Override
  public InjectableStepsFactory stepsFactory() {
    return new SpringStepsFactory(configuration(), applicationContext);
  }

  @Override
  protected List<String> storyPaths() {
    return new StoryFinder().findPaths(CodeLocations.codeLocationFromClass(this.getClass()),
        "**/*.story", "**/excluded*.story");
  }

  /**
   * Create queue room. There is a retry behavior to avoid errors when
   * the POD doesn't support to create private room with the view history flag set to TRUE.
   * @return the created stream
   */
  private Room createQueueRoom() {
    try {
      return createRoom(Boolean.TRUE);
    } catch (RoomException e) {
      try {
        return createRoom(Boolean.FALSE);
      } catch (RoomException e1) {
        throw new IllegalStateException("Couldn't create queue room.", e1);
      }
    }
  }

  /**
   * Creates a new stream for Queue Room and set on TestContext.
   * @param showHistory Show History
   * @return the created stream
   */
  private Room createRoom(Boolean showHistory) throws RoomException {
    SymRoomAttributes symRoomAttributes = new SymRoomAttributes();
    symRoomAttributes.setViewHistory(showHistory);

    String randomId = UUID.randomUUID().toString();
    symRoomAttributes.setName("Queue Room " + randomId);
    symRoomAttributes.setDescription("Queue Room " + randomId);

    Room queueRoom = helpDeskSymphonyClient.getRoomService().createRoom(symRoomAttributes);
    testContext.setQueueRoom(queueRoom);

    return queueRoom;
  }

  /**
   * Create HTTP client, Authenticates bot user and starts Help desk symphony client.
   */
  private void initSymphonyClient() {
    HelpDeskHttpClient httpClient = new HelpDeskHttpClient();
    httpClient.setupClient(config);

    HelpDeskAuthenticationService authService = new HelpDeskAuthenticationService(config, httpClient);
    SymAuth symAuth = authService.authenticate();

    helpDeskSymphonyClient = new HelpDeskSymphonyClient(httpClient);

    try {
      helpDeskSymphonyClient.init(symAuth, config.getEmail(), config.getAgentUrl(), config.getPodUrl());
    } catch (InitException e) {
      throw new IllegalStateException("Cannot instantiate symphony client.", e);
    }
  }

  /**
   * Creates a new user on POD
   * @param user user
   * @param userType type of account
   * @param roles list of permissions
   * @return the SymUser
   */
  private SymUser createUser(String user, UserAttributes.AccountTypeEnum userType, List<String> roles) {
    UserCreate userCreate = new UserCreate();

    UserAttributes userAttributes = buildUserAttributes(user, userType);
    userCreate.setUserAttributes(userAttributes);

    userCreate.setRoles(roles);

    try {
      return helpDeskSymphonyClient.getUsersClient().createUser(userCreate);
    } catch (UsersClientException e) {
      throw new IllegalStateException("Cannot create user.", e);
    }
  }

  /**
   * Build user attributes
   * @param user user
   * @param userType type of account
   * @return the SymUser
   */
  private UserAttributes buildUserAttributes(String user, UserAttributes.AccountTypeEnum userType) {
    String rnd = UUID.randomUUID().toString();
    String userName = user + "." + rnd;

    UserAttributes userAttrs = new UserAttributes();
    userAttrs.setFirstName(user);
    userAttrs.setLastName(rnd);
    userAttrs.setEmailAddress(userName + "@example.com");
    userAttrs.setDisplayName(userName);
    userAttrs.setUserName(userName);
    userAttrs.setAccountType(userType);

    return userAttrs;
  }

  /**
   * Add user on queue room.
   * @return userId the id of user
   */
  private void addUserOnQueueRoom(Long userId) {
    try {
      helpDeskSymphonyClient.getRoomMembershipClient().addMemberToRoom(testContext.getQueueRoom().getStreamId(), userId);
    } catch (SymException e) {
      throw new IllegalStateException("Couldn't add user on this room.", e);
    }
  }

  /**
   * Method responsible to create user, add on queue room and set users on the testContext.
   */
  private void createUsersAndAddToQueueRoom() {
    List<String> roles = new ArrayList<>();
    roles.add(ROLE_INDIVIDUAL);

    SymUser agent = createUser(AGENT, UserAttributes.AccountTypeEnum.NORMAL, roles);
    addUserOnQueueRoom(agent.getId());
    testContext.setUsers(UsersEnum.AGENT1, agent);

    agent = createUser(AGENT, UserAttributes.AccountTypeEnum.NORMAL, roles);
    addUserOnQueueRoom(agent.getId());
    testContext.setUsers(UsersEnum.AGENT2, agent);

    agent = createUser(AGENT, UserAttributes.AccountTypeEnum.NORMAL, roles);
    addUserOnQueueRoom(agent.getId());
    testContext.setUsers(UsersEnum.AGENT3, agent);
  }

  /**
   * Creates a new service account on POD
   * @param userName name of user
   */
  private void createServiceAccount(String userName) {
    List<String> roles = new ArrayList<>();
    roles.add(ROLE_INDIVIDUAL);

    createUser(userName, UserAttributes.AccountTypeEnum.SYSTEM, roles);
  }

  /**
   * Method responsible to create a certificate for user provisioning and use the certificate to
   * create a new service account and create a certificate to authenticates the new service account.
   * @param caKeyPath path to certificate key
   * @param caCertPath path to certificate path
   */
  private void provisioningSteps(String caKeyPath, String caCertPath) {
    String userProvisioning = USER_PROVISIONING;
    String certsDir = testContext.getCertsDir();

    certificateUtils.createCertificateP12(caKeyPath, caCertPath, userProvisioning);

    File provisioningCertificate = new File(certsDir + userProvisioning + EXTENSION_CERTIFICATE);
    if (!provisioningCertificate.exists()) {
      throw new IllegalStateException(MESSAGE_PROVISIONING_CERTIFICATE_NOT_FOUND);
    }

    UUID uuid = UUID.randomUUID();
    String userBot = BEGIN_NAME_OF_CERTIFICATE + uuid;
    certificateUtils.createCertificateP12(caKeyPath, caCertPath, userBot);

    File botCertificate = new File(certsDir + userBot + EXTENSION_CERTIFICATE);
    if (!botCertificate.exists()) {
      throw new IllegalStateException(MESSAGE_BOT_CERTIFICATE_NOT_FOUND);
    }

    createServiceAccount(userBot);
  }
}
