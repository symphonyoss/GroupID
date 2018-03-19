package org.symphonyoss.symphony.bots.helpdesk.service.it;

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
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.symphonyoss.symphony.bots.helpdesk.service.HelpDeskServiceInit;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.annotation.PostConstruct;

/**
 * Created by rsanchez on 21/02/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {HelpDeskServiceInit.class, TestServletConfiguration.class})
public class HelpDeskServiceStories extends JUnitStories {

  private static final String APP_KEYSTORE = "app_keystore";

  private static final String APP_KEYSTORE_PASS = APP_KEYSTORE + "_password";

  private static final String LOCAL_PORT = "local_port";

  private static final String DEFAULT_KEYSTORE = "root.p12";

  private static final String DEFAULT_KEYSTORE_PASSWORD = "changeit";

  @Autowired
  private ApplicationContext applicationContext;

  @LocalServerPort
  private int localPort;

  public HelpDeskServiceStories() {
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

  @PostConstruct
  public void init() throws URISyntaxException {
    initServicesInfoProvider();
    initAppKeystore();
  }

  private void initServicesInfoProvider() {
    System.setProperty(LOCAL_PORT, String.valueOf(localPort));
  }

  private void initAppKeystore() throws URISyntaxException {
    URL resource = getClass().getClassLoader().getResource(DEFAULT_KEYSTORE);
    File keystore = new File(resource.toURI());

    System.setProperty(APP_KEYSTORE, keystore.getAbsolutePath());
    System.setProperty(APP_KEYSTORE_PASS, DEFAULT_KEYSTORE_PASSWORD);
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

}
