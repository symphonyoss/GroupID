package org.symphonyoss.symphony.bots.helpdesk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application Main class to run HelpDesk Renderer.
 * <p>
 * Created by robson on 10/18/17.
 */
@SpringBootApplication
public class HelpDeskBotApplication {

  public static void main(String[] args) throws Exception {
    SpringApplication application = new SpringApplication(HelpDeskBotApplication.class);
    application.run(args);
  }

}
