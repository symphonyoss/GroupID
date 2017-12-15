package org.symphonyoss.symphony.bots.helpdesk.bot.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.symphonyoss.client.exceptions.InitException;
import org.symphonyoss.symphony.bots.ai.HelpDeskAi;
import org.symphonyoss.symphony.bots.helpdesk.bot.HelpDeskBot;
import org.symphonyoss.symphony.bots.helpdesk.bot.authentication.HelpDeskAuthenticationException;
import org.symphonyoss.symphony.bots.helpdesk.bot.client.HelpDeskSymphonyClient;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.listener.AutoConnectionAcceptListener;
import org.symphonyoss.symphony.bots.helpdesk.bot.listener.HelpDeskRoomEventListener;
import org.symphonyoss.symphony.bots.helpdesk.messageproxy.ChatListener;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Listener to indicate the application is ready to service requests.
 *
 * Created by rsanchez on 15/12/17.
 */
public class HelpDeskBootstrap implements ApplicationListener<ApplicationReadyEvent> {

  private static final Logger LOGGER = LoggerFactory.getLogger(HelpDeskBootstrap.class);

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    ApplicationContext applicationContext = event.getApplicationContext();

    Consumer<ApplicationContext> consumerClient = (context) -> initSymphonyClient(context);
    Consumer<ApplicationContext> consumerBot = (context) -> registerBot(context);
    Consumer<ApplicationContext> consumerAi = (context) -> initializeAi(context);

    try {
      execMultiplesTimes(consumerClient.andThen(consumerBot).andThen(consumerAi),
          applicationContext, 5);
    } catch (InterruptedException e) {
      LOGGER.error("Fail to start helpdesk bot", e);
    }

    ready(applicationContext);
  }

  private void execMultiplesTimes(Consumer<ApplicationContext> consumer,
      ApplicationContext applicationContext, int times) throws InterruptedException {
    int count = 0;

    do {
      try {
        consumer.accept(applicationContext);
        break;
      } catch (Exception e) {
        LOGGER.error(e.getMessage(), e);
      }

      count++;

      Thread.sleep(TimeUnit.SECONDS.toMillis(count));
    } while (count < times);
  }

  /**
   * Authenticates bot user, starts symphony client, and register listeners
   * @param applicationContext Spring application context
   */
  private void initSymphonyClient(ApplicationContext applicationContext) {
    try {
      HelpDeskSymphonyClient symphonyClient = applicationContext.getBean(HelpDeskSymphonyClient.class);
      symphonyClient.init();

      registerListeners(symphonyClient, applicationContext);
    } catch (HelpDeskAuthenticationException | InitException e) {
      throw new HelpDeskBootstrapException("Fail to start helpdesk bot", e);
    }
  }

  /**
   * Register listeners
   * @param symphonyClient Symphony client
   * @param applicationContext Spring application context
   */
  private void registerListeners(HelpDeskSymphonyClient symphonyClient, ApplicationContext applicationContext) {
    ChatListener chatListener = applicationContext.getBean(ChatListener.class);
    HelpDeskRoomEventListener roomEventListener = applicationContext.getBean(HelpDeskRoomEventListener.class);
    AutoConnectionAcceptListener connectionListener = applicationContext.getBean(AutoConnectionAcceptListener.class);

    symphonyClient.getMessageService().addRoomServiceEventListener(roomEventListener);
    symphonyClient.getMessageService().addConnectionsEventListener(connectionListener);
    symphonyClient.getMessageService().addMessageListener(chatListener);
  }

  /**
   * Register bot as a group id member
   * @param applicationContext Spring application context
   */
  private void registerBot(ApplicationContext applicationContext) {
    HelpDeskBot helpDeskBot = applicationContext.getBean(HelpDeskBot.class);
    helpDeskBot.registerDefaultAgent();
  }

  /**
   * Register Helpdesk AI
   * @param applicationContext Spring application context
   */
  private void initializeAi(ApplicationContext applicationContext) {
    HelpDeskAi helpDeskAi = applicationContext.getBean(HelpDeskAi.class);
    helpDeskAi.init();
  }

  /**
   * Indicates the application is ready to service requests.
   * @param applicationContext Spring application context
   */
  private void ready(ApplicationContext applicationContext) {
    HelpDeskBot helpDeskBot = applicationContext.getBean(HelpDeskBot.class);
    HelpDeskBotConfig configuration = applicationContext.getBean(HelpDeskBotConfig.class);

    helpDeskBot.ready();

    LOGGER.info("Help Desk Bot startup complete for groupId: " + configuration.getGroupId());
  }

}
