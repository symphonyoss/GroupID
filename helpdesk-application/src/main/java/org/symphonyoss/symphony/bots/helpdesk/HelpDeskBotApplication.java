package org.symphonyoss.symphony.bots.helpdesk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by robson on 18/10/17.
 */
@SpringBootApplication
public class HelpDeskBotApplication {

  public static void main(String[] args) throws Exception {
    SpringApplication application = new SpringApplication(HelpDeskBotApplication.class);
    application.run(args);
  }

}
