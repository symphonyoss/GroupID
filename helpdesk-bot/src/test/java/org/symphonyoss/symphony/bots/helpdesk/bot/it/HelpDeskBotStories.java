package org.symphonyoss.symphony.bots.helpdesk.bot.it;

import org.apache.commons.lang3.StringUtils;
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
import org.symphonyoss.client.model.Room;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.bots.helpdesk.bot.authentication.HelpDeskAuthenticationService;
import org.symphonyoss.symphony.bots.helpdesk.bot.bootstrap.HelpDeskBootstrap;
import org.symphonyoss.symphony.bots.helpdesk.bot.client.HelpDeskHttpClient;
import org.symphonyoss.symphony.bots.helpdesk.bot.client.HelpDeskSymphonyClient;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.init.SpringHelpDeskBotInit;
import org.symphonyoss.symphony.bots.helpdesk.bot.listener.HelpDeskRoomEventListener;
import org.symphonyoss.symphony.clients.model.SymRoomAttributes;

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

  private static final String QUEUE_ROOM_PROPERTY = "AGENT_STREAM_ID";

  private static final String[] SUPPORTED_ENVS = { "nexus1", "nexus2", "nexus3", "nexus4" };

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private HelpDeskSymphonyClient helpDeskSymphonyClient;

  @Autowired
  private HelpDeskBotConfig config;

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
    initSymphonyClient();
    String queueRoom = getQueueRoom().getStreamId();

    createBotCertificate();
    setupSystemProperties(queueRoom);
  }


  private void createBotCertificate() {
    // TODO APP-1629
  }

  private void setupSystemProperties(String queueRoom) {
    if (StringUtils.isNotBlank(queueRoom)) {
      System.setProperty(QUEUE_ROOM_PROPERTY, queueRoom);
    }
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

  private Room getQueueRoom() {
    try {
      return createRoom(Boolean.TRUE);
    } catch (RoomException e) {
      try {
        return createRoom(Boolean.FALSE);
      } catch (RoomException e1) {
        throw new IllegalStateException("Couldn't create queue room.");
      }
    }
  }

  private Room createRoom(Boolean showHistory) throws RoomException {
    SymRoomAttributes symRoomAttributes = new SymRoomAttributes();
    symRoomAttributes.setViewHistory(showHistory);

    String randomId = UUID.randomUUID().toString();
    symRoomAttributes.setName("Queue Room" + randomId);
    symRoomAttributes.setDescription("Queue Room" + randomId);

    return helpDeskSymphonyClient.getRoomService().createRoom(symRoomAttributes);
  }

  private void initSymphonyClient() {
    HelpDeskHttpClient httpClient = new HelpDeskHttpClient();
    httpClient.setupClient(config);

    HelpDeskAuthenticationService authService = new HelpDeskAuthenticationService(config, httpClient);
    SymAuth symAuth = authService.authenticate();

    helpDeskSymphonyClient = new HelpDeskSymphonyClient(httpClient);

    try {
      helpDeskSymphonyClient.init(symAuth, config.getEmail(), config.getAgentUrl(), config.getPodUrl());

      HelpDeskRoomEventListener roomEventListener = applicationContext.getBean(HelpDeskRoomEventListener.class);

      helpDeskSymphonyClient.getMessageService().addRoomServiceEventListener(roomEventListener);
    } catch (InitException e) {
      throw new IllegalStateException("Cannot instantiate symphony client.");
    }
  }

}
