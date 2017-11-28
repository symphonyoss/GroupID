package org.symphonyoss.symphony.bots.helpdesk.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by rsanchez on 13/11/17.
 */
@SpringBootApplication
@EnableSwagger2
@EnableWebMvc
public class HelpDeskServiceInit {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(HelpDeskServiceInit.class, args);
  }

}
