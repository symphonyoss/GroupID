package org.symphonyoss.symphony.bots.helpdesk.service.init;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by nick.tarsillo on 9/25/17.
 */
@SpringBootApplication(scanBasePackages = { "org.symphonyoss.symphony.bots.helpdesk.service" })
@EnableSwagger2
@EnableWebMvc
public class SpringHelpDeskServiceInit {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(SpringHelpDeskServiceInit.class, args);
  }

}
